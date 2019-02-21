package com.healthmonitoring

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.util.Patterns
import com.healthmonitoring.util.PrefMan
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

  lateinit var prefMan: PrefMan

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    prefMan = PrefMan(this)

    login_submit.setOnClickListener {
      login_email_layout.isErrorEnabled = false
      login_password_layout.isErrorEnabled = false
      if (!Patterns.EMAIL_ADDRESS.matcher(login_email.text.toString()).matches()) {
        login_email_layout.isErrorEnabled = true
        login_email_layout.error = "Invalid email"
      } else if (login_password.text.toString().length < 6) {
        login_password_layout.isErrorEnabled = true
        login_password_layout.error = "Minimum 6 characters"
      } else {
        login_submit.startAnimation()
        prefMan.login(login_email.text.toString(), login_password.text.toString(), { success, message ->
          if(success){
            login_submit.revertAnimation {
              login_submit.setText("Logged in successfully")
              Handler().postDelayed({
                startActivity<ModeSelectorActivity>()
                finish()
              }, 1000)
            }

          }else{
            toast(message)
            login_submit.revertAnimation()
          }
        })
      }
    }

    login_register.setOnClickListener {
      startActivity<SignUpActivity>()
      finish()
    }
  }
}
