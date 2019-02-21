package com.healthmonitoring.util

import android.graphics.Color
import com.healthmonitoring.model.User

object HealthUtil {


  val GENDER_MALE = 1
  val GENDER_FEMALE = 2

  object BMI {
    val BMI_SEVERE_THIN_TEXT = "Severely thin"
    val BMI_SEVERE_THIN_COLOR = Color.parseColor("#D50000")

    val BMI_MODERATE_THIN_TEXT = "Moderately thin"
    val BMI_MODERATE_THIN_COLOR = Color.parseColor("#E53935")

    val BMI_MILD_THIN_TEXT = "Mildly thin"
    val BMI_MILD_THIN_COLOR = Color.parseColor("#FF8F00")

    val BMI_NORMAL_TEXT = "Normal"
    val BMI_NORMAL_COLOR = Color.parseColor("#4CAF50")

    val BMI_OVERWEIGHT_TEXT = "Overweight"
    val BMI_OVERWEIGHT_COLOR = Color.parseColor("#EF5350")

    val BMI_OBESE_1_TEXT = "Obese Class I"
    val BMI_OBESE_1_COLOR = Color.parseColor("#D50000")

    val BMI_OBESE_2_TEXT = "Obese Class II"
    val BMI_OBESE_2_COLOR = Color.parseColor("#D50000")

    val BMI_OBESE_3_TEXT = "Obese Class III"
    val BMI_OBESE_3_COLOR = Color.parseColor("#D50000")

    fun getBMI(weight: Float, height: Float): Float {
      val h = height/100
      val h2 = h * h
      return (weight / h2)
    }
  }

  object Pulse {

    val HEALTH_UNKNOWN = Color.BLACK

    val HEALTH_ATHLETE = Color.parseColor("#00C853")
    val RANGE_ATHLETE_M = IntRange(49, 55)
    val RANGE_ATHLETE_F = IntRange(54, 60)

    val HEALTH_EXCELLENT = Color.parseColor("#00E676")
    val RANGE_EXCELLENT_M = IntRange(56, 61)
    val RANGE_EXCELLENT_F = IntRange(61, 65)

    val HEALTH_GOOD = Color.parseColor("#76FF03")
    val RANGE_GOOD_M = IntRange(62, 65)
    val RANGE_GOOD_F = IntRange(66, 69)

    val HEALTH_ABOVE_AVG = Color.parseColor("#8BC34A")
    val RANGE_ABOVE_AVG_M = IntRange(66, 69)
    val RANGE_ABOVE_AVG_F = IntRange(70, 73)

    val HEALTH_AVG = Color.parseColor("#FFD600")
    val RANGE_AVG_M = IntRange(70, 73)
    val RANGE_AVG_F = IntRange(74, 78)

    val HEALTH_BELOW_AVG = Color.parseColor("#EF6C00")
    val RANGE_BELOW_AVG_M = IntRange(74, 81)
    val RANGE_BELOW_AVG_F = IntRange(79, 84)

    val HEALTH_POOR = Color.parseColor("#DD2C00")

    fun getHealthLevel(user: User): Int {
      val gender = user.gender
      val bpm = user.bpm

      when (gender) {
        GENDER_MALE -> {
          if (RANGE_ATHLETE_M.first > bpm) {
            return HEALTH_UNKNOWN
          } else if (RANGE_ATHLETE_M.contains(bpm)) {
            return HEALTH_ATHLETE
          } else if (RANGE_EXCELLENT_M.contains(bpm)) {
            return HEALTH_EXCELLENT
          } else if (RANGE_GOOD_M.contains(bpm)) {
            return HEALTH_GOOD
          } else if (RANGE_ABOVE_AVG_M.contains(bpm)) {
            return HEALTH_ABOVE_AVG
          } else if (RANGE_AVG_M.contains(bpm)) {
            return HEALTH_AVG
          } else if (RANGE_BELOW_AVG_M.contains(bpm)) {
            return HEALTH_BELOW_AVG
          } else {
            return HEALTH_POOR
          }
        }

        GENDER_FEMALE -> {
          if (RANGE_ATHLETE_F.first > bpm) {
            return HEALTH_UNKNOWN
          } else if (RANGE_ATHLETE_F.contains(bpm)) {
            return HEALTH_ATHLETE
          } else if (RANGE_EXCELLENT_F.contains(bpm)) {
            return HEALTH_EXCELLENT
          } else if (RANGE_GOOD_F.contains(bpm)) {
            return HEALTH_GOOD
          } else if (RANGE_ABOVE_AVG_F.contains(bpm)) {
            return HEALTH_ABOVE_AVG
          } else if (RANGE_AVG_F.contains(bpm)) {
            return HEALTH_AVG
          } else if (RANGE_BELOW_AVG_F.contains(bpm)) {
            return HEALTH_BELOW_AVG
          } else {
            return HEALTH_POOR
          }
        }

        else -> return HEALTH_UNKNOWN
      }
    }
  }
}