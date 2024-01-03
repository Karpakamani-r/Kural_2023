package com.w2c.kural.utils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun ViewGroup.visible() {
    this.visibility = View.VISIBLE
}

fun ViewGroup.hide() {
    this.visibility = View.GONE
}

//Formatted date 15-07-2021 09:07 PM
val todayDate: String
    get() {
        val currentDate = Date()
        val simpleDateFormat = SimpleDateFormat(
            "dd-MM-yyyy hh:mm a",
            Locale.getDefault()
        )
        val formattedDate = simpleDateFormat.format(currentDate)
        Log.d("Formatted date", formattedDate) //Formatted date 15-07-2021 09:07 PM
        return formattedDate
    }

fun getDifferentMillsToNextDay(): Long {
    val currentTime = Calendar.getInstance().time
    val now = Calendar.getInstance(Locale.getDefault())
    now.time = currentTime
    return getTargetTime().timeInMillis - now.timeInMillis
}

private fun getTargetTime(): Calendar {
    val calender = Calendar.getInstance(Locale.getDefault())
    calender.time = Date()
    calender.set(Calendar.HOUR_OF_DAY, 3)
    calender.set(Calendar.MINUTE, 0)
    calender.set(Calendar.SECOND, 0)
    calender.add(Calendar.DAY_OF_MONTH, 1)
    return calender
}