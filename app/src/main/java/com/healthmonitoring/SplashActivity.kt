package com.healthmonitoring

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.healthmonitoring.util.PrefMan
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

  lateinit var prefMan: PrefMan

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    splash_loader.smoothToShow()
    val i = Intent()

    prefMan = PrefMan(this)

    if (prefMan.isDetailSaved()) {
      i.setClass(this, ModeSelectorActivity::class.java)
    } else {
      i.setClass(this, LoginActivity::class.java)
    }

    Handler().postDelayed({
      startActivity(i)
      finish()
    }, 1500)
  }
}
