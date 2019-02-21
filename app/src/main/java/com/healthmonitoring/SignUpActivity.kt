package com.healthmonitoring

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.google.firebase.firestore.FirebaseFirestore
import com.healthmonitoring.model.User
import com.healthmonitoring.util.BCrypt
import com.healthmonitoring.util.HealthUtil
import com.healthmonitoring.util.PrefMan
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class SignUpActivity : AppCompatActivity() {

  lateinit var prefMan: PrefMan
  var allEmails: ArrayList<String> = ArrayList()
  lateinit var db: FirebaseFirestore
  var emailExists = false
  var weight = 0
  var height = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    db = FirebaseFirestore.getInstance()

    setContentView(R.layout.activity_sign_up)

    prefMan = PrefMan(this)

    db.collection("users")
        .get()
        .addOnCompleteListener {
          if (it.isComplete && it.isSuccessful) {
            it.result.documents.forEach {
              allEmails.add(it.id)
            }
            sign_up_email.addTextChangedListener(object : TextWatcher {
              override fun afterTextChanged(p0: Editable?) {
                sign_up_email_layout.isErrorEnabled = false
                if (allEmails.contains(p0.toString())) {
                  emailExists = true
                  sign_up_email_layout.isErrorEnabled = true
                  sign_up_email_layout.error = "This email is already registered"
                }else{
                  emailExists = false
                }
              }

              override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

              }

              override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

              }
            })
          }
        }
        .addOnFailureListener {
          it.printStackTrace()
        }

    sign_up_weight.setLimitExceededListener { limit, exceededValue ->
      toast("Max weight can be ${sign_up_weight.max}")
    }

    sign_up_weight.setValueChangedListener { value, action ->
      weight = value
    }


    sign_up_height.setLimitExceededListener { limit, exceededValue ->
      toast("Max height can be ${sign_up_height.max}")
    }

    sign_up_height.setValueChangedListener { value, action ->
      height = value
    }


    details_submit.setOnClickListener {
      sign_up_name_layout.isErrorEnabled = false
      sign_up_confirm_password_layout.isErrorEnabled = false
      sign_up_password_layout.isErrorEnabled = false
      sign_up_email_layout.isErrorEnabled = false
      if(emailExists){
        toast("Please enter another email address")
      }else if (sign_up_name.text.toString().isEmpty()) {
        sign_up_name_layout.error = "Name cannot be blank"
      } else if (!Patterns.EMAIL_ADDRESS.matcher(sign_up_email.text.toString()).matches()) {
        sign_up_email_layout.error = "Invalid email"
      } else if (sign_up_password.text.toString().length < 6) {
        sign_up_password_layout.error = "Minimum 6 characters"
      } else if (sign_up_password.text.toString() != sign_up_confirm_password.text.toString()) {
        sign_up_confirm_password_layout.error = "Passwords do not match"
      } else {
        details_submit.startAnimation()
        var gender = HealthUtil.GENDER_MALE
        when(sign_up_gender.getDirection()){
          StickySwitch.Direction.LEFT -> gender = HealthUtil.GENDER_MALE

          StickySwitch.Direction.RIGHT -> gender = HealthUtil.GENDER_FEMALE
        }
        val details = User(email = sign_up_email.text.toString(), password = BCrypt.hashpw(sign_up_password.text.toString(), BCrypt.gensalt()),
            name = sign_up_name.text.toString(),
            gender = gender,
            age = sign_up_age.value,
            weight = sign_up_weight.value, height = sign_up_height.value)
        prefMan.saveDetails(details, {
          details_submit.revertAnimation()
          details_submit.setText("Registered successfully")
          Handler().postDelayed({
            startActivity<ModeSelectorActivity>()
            finish()
          }, 1000)
        }, {
          details_submit.revertAnimation()
          toast("Failed to register")
        })

      }
    }
  }
}
