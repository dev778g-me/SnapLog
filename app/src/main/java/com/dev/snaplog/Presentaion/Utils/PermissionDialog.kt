//package com.dev.snaplog.Presentaion.Utils
//
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.window.Dialog
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PermissionDialog(
//
//    onDismiss: () -> Unit,
//    onok: () -> Unit,
//
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text(text = "Permission Required") },
//        text = {
//            Text(text = "Grant the Permission to Continue ")
//        },
//        confirmButton = {
//            Button(onClick = {
//                if (isPermanentlyDeclined) {
//                    onGotoSettings.invoke()
//                } else{
//                    onok.invoke()
//                }
//            }) { }
//        },
//        dismissButton = {}
//    )
//}