package com.healthmonitoring

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.healthmonitoring.util.HealthUtil.BMI
import kotlinx.android.synthetic.main.activity_bmi.*
import org.jetbrains.anko.toast

class BMIActivity : AppCompatActivity() {

  var height:Int = 0
  var weight:Int = 0
  var bmi:Float = 0f

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_bmi)

    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setTitle("Body Mass Index")

    weight = intent.getIntExtra("weight",0)
    height = intent.getIntExtra("height", 0)

    bmi_weight.value = weight
    bmi_height.value = height

    bmi_weight.setLimitExceededListener { limit, exceededValue ->
      toast("Max weight can be ${bmi_weight.max}")
    }

    bmi_weight.setValueChangedListener { value, action ->
      weight = value
    }

    bmi_height.setLimitExceededListener { limit, exceededValue ->
      toast("Max height can be ${bmi_height.max}")
    }

    bmi_height.setValueChangedListener { value, action ->
      height = value
    }

    bmi_calculate.setOnClickListener {
      Log.d("val", "${weight} ${bmi_weight.value}   ${height} ${bmi_height.value}")
      bmi = BMI.getBMI(bmi_weight.value.toFloat(), bmi_height.value.toFloat())
      bmi_result.setText(bmi.toString())
      if(bmi < 15.9){
        bmi_result_remark.setText(BMI.BMI_SEVERE_THIN_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_SEVERE_THIN_COLOR)
      }else if(bmi > 16 && bmi < 16.99){
        bmi_result_remark.setText(BMI.BMI_MODERATE_THIN_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_MODERATE_THIN_COLOR)
      }else if(bmi > 17 && bmi < 18.49){
        bmi_result_remark.setText(BMI.BMI_MILD_THIN_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_MILD_THIN_COLOR)
      }else if(bmi > 18.5 && bmi < 24.99){
        bmi_result_remark.setText(BMI.BMI_NORMAL_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_NORMAL_COLOR)
      }else if(bmi > 25 && bmi < 29.99){
        bmi_result_remark.setText(BMI.BMI_OVERWEIGHT_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_OVERWEIGHT_COLOR)
      }else if(bmi > 30 && bmi < 34.99){
        bmi_result_remark.setText(BMI.BMI_OBESE_1_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_OBESE_1_COLOR)
      }else if(bmi > 35 && bmi < 40){
        bmi_result_remark.setText(BMI.BMI_OBESE_2_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_OBESE_2_COLOR)
      }else{
        bmi_result_remark.setText(BMI.BMI_OBESE_3_TEXT)
        bmi_result_remark.setTextColor(BMI.BMI_OBESE_3_COLOR)
      }
    }


    if(weight != 0 && height != 0){
      bmi_calculate.performClick()
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    onBackPressed()
    return true
  }
}
