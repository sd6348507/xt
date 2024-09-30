package com.roemsoft.common.unit

import android.app.Activity
import androidx.core.content.PermissionChecker

object PermissionUtils {

    fun checkAndRequestPermissions(context: Activity, requestCode: Int, vararg permissions: String) {
        val deniedPermissions: MutableList<String> = ArrayList(permissions.size)
        for (permission in permissions) {
            if (PermissionChecker.checkSelfPermission(
                    context,
                    permission
                ) == PermissionChecker.PERMISSION_DENIED
            ) {
                deniedPermissions.add(permission)
            }
        }
        if (deniedPermissions.isNotEmpty()) {
            val ps = deniedPermissions.toTypedArray()
            context.requestPermissions(ps, requestCode)
        }
    }
}