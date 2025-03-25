package com.dev.snaplog.Presentaion.Viewmodel

import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PermissionViewmodel : ViewModel() {

    //[media images] [post notification]
  val visiablePermissionDialog = mutableStateListOf<String>()

    fun dismissDialog (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            visiablePermissionDialog.removeFirst()
        }
    }

    fun onPermissionResult (
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiablePermissionDialog.contains(permission)) {
            visiablePermissionDialog.add(permission)
        }
    }


}