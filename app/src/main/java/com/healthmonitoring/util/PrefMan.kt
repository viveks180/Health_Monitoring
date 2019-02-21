package com.healthmonitoring.util

import android.content.Context
import android.telecom.Call
import com.google.firebase.firestore.FirebaseFirestore
import com.healthmonitoring.model.User

class PrefMan(val ctx: Context) {
  val prefs = ctx.getSharedPreferences("details", Context.MODE_PRIVATE)
  val editor = prefs.edit()
  val db = FirebaseFirestore.getInstance()

  companion object {
    val CONNECTION_MODE_PATIENT = 1
    val CONNECTION_MODE_USER = 2
  }

  fun saveString(key: String, value: String) {
    editor.putString(key, value)
    editor.apply()
  }

  fun saveInt(key: String, value: Int) {
    editor.putInt(key, value)
    editor.apply()
  }

  fun saveBool(key: String, value: Boolean) {
    editor.putBoolean(key, value)
    editor.apply()
  }

  fun getString(key: String): String? = prefs.getString(key, null)

  fun getInt(key: String): Int = prefs.getInt(key, 0)

  fun getBool(key: String): Boolean = prefs.getBoolean(key, false)

  fun saveDevice(name: String) {
    saveString("device", name)
    saveBool("is_saved", true)
  }

  fun getDevice(): String? = getString("device")

  fun getSession(): Boolean = getBool("is_saved")

  fun clearSession() = saveBool("device", false)

  fun saveMode(mode: Int) = saveInt("mode", mode)

  fun getMode(): Int = getInt("mode")

  fun isDetailSaved(): Boolean = getBool("is_detail_saved")

  fun saveDetails(user: User, onCompletion: () -> Unit, onFailure: () -> Unit) {
    db.collection("users")
        .document(user.email).set(user)
        .addOnCompleteListener {
          if(it.isComplete && it.isSuccessful){
            onCompletion()
          }else{
            onFailure()
          }
        }
        .addOnFailureListener {
          it.printStackTrace()
          onFailure()
        }
    saveEmail(user.email)
  }

  fun getDetails(): User = db.collection("users").document(getEmail()!!).get().result.toObject(User::class.java)

  fun saveEmail(email: String){
    saveBool("is_detail_saved", true)
    saveString("email", email)
  }

  fun getEmail(): String? = getString("email")

  fun login(email: String, password:String, onResult: (success:Boolean, message:String) -> Unit){
    db.collection("users")
        .document(email)
        .get()
        .addOnCompleteListener {
          if(it.isComplete && it.isSuccessful){
            if(it.result.exists()){
              if(BCrypt.checkpw(password, it.result.getString("password"))){
                onResult(true, "Logged in successfully")
                saveEmail(email)
              }else{
                onResult(false, "Incorrect password")
              }
            }else{
              onResult(false, "Not registered")
            }
          }else{
            onResult(false, "Unknown error")
          }
        }
        .addOnFailureListener {
          it.printStackTrace()
          onResult(false, "Unknown error")
        }
  }
}