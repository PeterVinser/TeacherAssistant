package com.piotrokninski.teacherassistant.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.piotrokninski.teacherassistant.view.main.MainActivity

object PermissionsHelper {

    fun checkCalendarPermissions(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_CALENDAR
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_CALENDAR
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle("Calendar write permission")
                    .setMessage("Do you want to allow the access to calendar?")
                    .setPositiveButton("Ask me") { _, _ ->
                        requestCalendarPermission(activity)
                    }
                    .setNegativeButton("No") { _, _ ->
                        (activity as MainActivity).notifyCalendarFragment(false)
                    }
                    .show()
            } else {
                requestCalendarPermission(activity)
            }
        } else {
            (activity as MainActivity).notifyCalendarFragment(true)
        }
    }

    fun onRequestPermissionsResult(activity: Activity, requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            AppConstants.PERMISSION_CALENDAR -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    (activity as MainActivity).notifyCalendarFragment(true)
                } else {
                    (activity as MainActivity).notifyCalendarFragment(false)
                }
            }
        }
    }


    private fun requestCalendarPermission(activity: Activity) {
        val permissions = arrayOf(Manifest.permission.WRITE_CALENDAR)
        ActivityCompat.requestPermissions(activity, permissions, AppConstants.PERMISSION_CALENDAR)
    }
}