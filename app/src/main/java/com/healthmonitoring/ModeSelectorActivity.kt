package com.healthmonitoring

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.healthmonitoring.util.PrefMan
import kotlinx.android.synthetic.main.activity_mode_selector.*
import org.jetbrains.anko.startActivity

class ModeSelectorActivity : AppCompatActivity(), View.OnClickListener {

  lateinit var prefMan: PrefMan
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    prefMan = PrefMan(this)

    if(prefMan.getMode() != 0){
      startActivity<MainActivity>()
      finish()
      return
    }

    setContentView(R.layout.activity_mode_selector)

    select_mode_patient.setOnClickListener(this)
    select_mode_user.setOnClickListener(this)
  }

  override fun onClick(p0: View?) {
    when(p0?.id){
      R.id.select_mode_patient -> {
        prefMan.saveMode(PrefMan.CONNECTION_MODE_PATIENT)
        startActivity<MainActivity>()
        finish()
      }

      R.id.select_mode_user -> {
        prefMan.saveMode(PrefMan.CONNECTION_MODE_USER)
        startActivity<MainActivity>()
        finish()
      }
    }
  }

}
