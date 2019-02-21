package com.healthmonitoring

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import com.healthmonitoring.model.User
import com.healthmonitoring.util.HealthUtil
import com.healthmonitoring.util.PrefMan
import com.healthmonitoring.util.PrefMan.Companion.CONNECTION_MODE_PATIENT
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.sql.Timestamp
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

  lateinit var bt: BluetoothSPP
  lateinit var prefMan: PrefMan
  var connectionMode: Int = 0
  val entries = ArrayList<Entry>()
  var startTime: Long = 0
  lateinit var lineDataSet: LineDataSet
  lateinit var lineData: LineData
  var isHeartBeat: Boolean = false
  val timer = Timer()
  val timer2 = Timer()
  val db = FirebaseFirestore.getInstance()
  lateinit var user: User

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    prefMan = PrefMan(this)
    chart.setNoDataText("Pulse will be shown upon connection")

    connectionMode = prefMan.getMode()

    db.collection("users").document(prefMan.getEmail()!!).get()
        .addOnCompleteListener {
          if (it.isComplete && it.isSuccessful) {
            user = it.result.toObject(User::class.java)
            if (connectionMode == CONNECTION_MODE_PATIENT) {
              bt = BluetoothSPP(this)

              if (!bt.isBluetoothAvailable) {
                alert("Bluetooth not supported by device") {
                  yesButton { this@MainActivity.finish() }
                }.show()
              }

              if (connectionMode == CONNECTION_MODE_PATIENT) {
                if (!bt.isBluetoothEnabled) {
                  bt.bluetoothAdapter.enable()
                }

                bt.setupService()
                bt.startService(BluetoothState.DEVICE_OTHER)

                if (prefMan.getSession()) {
                  bt.autoConnect(prefMan.getDevice())
                } else {
                  val intent = Intent(this, DeviceList::class.java)
                  startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
                }
              }
            } else {
              chart.visibility = View.GONE
            }
            setupListener()
          }
        }
        .addOnFailureListener {
          it.printStackTrace()
        }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if (resultCode == Activity.RESULT_OK) {
        bt.connect(data)
      }
    }
  }

  fun setupListener() {
    if (connectionMode == PrefMan.CONNECTION_MODE_PATIENT) {
      bt.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
        override fun onDeviceDisconnected() {
          connection_status.setTextColor(Color.RED)
          connection_status.setText("Disconnected")
          heart_beat.stop()
        }

        override fun onDeviceConnected(name: String?, address: String?) {
          prefMan.saveDevice(name!!)
          deviceConnected()
        }

        override fun onDeviceConnectionFailed() {
          connection_status.setTextColor(Color.RED)
          connection_status.setText("Failed to connect")
        }
      })

      bt.setBluetoothStateListener {
        when (it) {
          BluetoothState.STATE_CONNECTED -> {
            deviceConnected()
          }

          BluetoothState.STATE_CONNECTING -> {
            connection_status.setTextColor(Color.BLUE)
            connection_status.setText("Connecting")
          }

          BluetoothState.STATE_NONE -> {
            connection_status.setTextColor(Color.BLACK)
            connection_status.setText("NONE")
          }
        }
      }
    } else if (connectionMode == PrefMan.CONNECTION_MODE_USER) {
      connection_status.visibility = View.GONE
      db.collection("users").document(user.email)
          .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            try {
              if (firebaseFirestoreException != null) {
                firebaseFirestoreException.printStackTrace()
                toast("Error occurred")
                return@addSnapshotListener
              }
              user = documentSnapshot.toObject(User::class.java)
              /*if ((System.currentTimeMillis() - user.lastUpdated?.time!!) < 5 * 60 * 1000) {
                heart_beat.start()
              } else {
                heart_beat.stop()
              }*/
              updateValues()
              updateColors()
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.menu_bmi -> {
        startActivity<BMIActivity>("weight" to user.weight, "height" to user.height)
      }
    }
    return true
  }

  fun deviceConnected() {
    startTime = System.currentTimeMillis()
    lineDataSet = LineDataSet(entries, "")
    lineDataSet.color = ContextCompat.getColor(this, R.color.colorPrimary)
    lineDataSet.disableDashedLine()
    lineDataSet.setDrawCircleHole(false)
    lineDataSet.setDrawCircles(false)
    lineDataSet.setDrawFilled(true)
    lineDataSet.setDrawValues(false)
    lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    lineDataSet.label = ""
    lineDataSet.lineWidth = 2f
    lineDataSet.fillColor = ContextCompat.getColor(this, R.color.colorPrimary)
    lineDataSet.fillAlpha = 255
    lineData = LineData(lineDataSet)
    chart.data = lineData
    chart.axisLeft.isEnabled = false
    chart.axisLeft.setAxisMaxValue(600f)
    chart.axisLeft.setAxisMinValue(400f)
    chart.axisRight.isEnabled = false
    chart.axisRight.setAxisMaxValue(600f)
    chart.axisRight.setAxisMinValue(400f)
    chart.xAxis.isEnabled = false
    chart.background = null
    chart.legend.isEnabled = false
    chart.setPinchZoom(false)
    chart.description.isEnabled = false
    chart.setViewPortOffsets(0f, 0f, 0f, 0f)
    chart.setDrawGridBackground(false)
    connection_status.setText("Connected")
    connection_status.setTextColor(Color.parseColor("#2E7D32"))
    heart_beat.start()
    bt.setOnDataReceivedListener { data, message ->
      val values = message.split(",")
      if (values.size == 5) {
        user.bpm = (values[0].toInt() / 3).toInt()
        user.ibi = (values[1].toInt() * 3).toInt()
        user.value = values[2].toInt()

        updateValues()

        isHeartBeat = values[3] == "1"

        user.temperature = values[4].toFloat()
      }
    }
    /*timer.scheduleAtFixedRate(object : TimerTask() {
      override fun run() {
        try {
          if (lineDataSet.entryCount > 100) {
            lineDataSet.removeFirst()
          }
          lineDataSet.addEntry(Entry(getTime(), user.value.toFloat()))
          refreshChart()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }, 0, 50)*/
    timer2.scheduleAtFixedRate(object : TimerTask() {
      override fun run() {
        pushUpdates()
      }
    }, 0, 1000)
  }

  fun getTime(): Float = (System.currentTimeMillis() - startTime).toFloat()

  fun pushUpdates() {
    runOnUiThread { updateColors() }
    user.lastUpdated = Timestamp(System.currentTimeMillis())
    db.collection("users").document(prefMan.getEmail()!!)
        .set(user)
  }

  override fun onDestroy() {
    if (connectionMode == PrefMan.CONNECTION_MODE_PATIENT) {
      bt.stopService()
      timer.cancel()
      timer2.cancel()
    }
    super.onDestroy()
  }

  fun updateColors() {
    beat_bpm.setTextColor(HealthUtil.Pulse.getHealthLevel(user))
    temperature.setText(String.format("%.1f°C", user.temperature))
    if (user.temperature > 37.5f) {
      temperature.setTextColor(Color.parseColor("#FF3D00"))
    } else {
      temperature.setTextColor(Color.parseColor("#2E7D32"))
    }
  }

  fun updateValues() {
    beat_bpm.setText("${user.bpm} BPM")
    beat_ibi.setText("${user.ibi}ms")

    //temperature.setText("${user.temperature}°C")

    //heart_beat.duration = user.ibi
  }

  fun refreshChart() {
    runOnUiThread {
      lineData.notifyDataChanged()
      chart.notifyDataSetChanged()
      chart.invalidate()
    }
  }

  fun updateHeart() {
    runOnUiThread {
      if (isHeartBeat) {
        heart_beat.clearColorFilter()
      } else {
        heart_beat.setColorFilter(Color.GRAY)
      }
    }
  }
}
