package com.gnommostudios.widgetestados.service

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AlertDialog

import com.gnommostudios.widgetestados.R

object PermissionUtils {

    //--------------------------------------------------
    // Permissions Methods
    //--------------------------------------------------

    private fun hasPermission(activity: Context, permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(permission)
        } else false
    }

    fun alertAndFinish(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.app_name).setMessage(activity.getString(R.string.permissions_denial))

        // Add the buttons.
        builder.setPositiveButton(android.R.string.ok) { _, _ -> activity.finish() }

        val dialog = builder.create()
        dialog.show()
    }

    fun canAccessCoarseLocation(activity: Activity): Boolean {
        return PermissionUtils.hasPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    fun canReadPhoneState(activity: Activity): Boolean {
        return PermissionUtils.hasPermission(activity, Manifest.permission.READ_PHONE_STATE)
    }
}