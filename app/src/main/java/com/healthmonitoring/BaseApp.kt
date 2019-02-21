package com.healthmonitoring

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging

class BaseApp:Application() {

  companion object {
    //lateinit var boxStore:BoxStore
  }

  override fun onCreate() {
    super.onCreate()
    FirebaseMessaging.getInstance().subscribeToTopic("default")
    //boxStore = MyObjectBox.builder().androidContext(this).build()
  }
}