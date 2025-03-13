package com.dev.snaplog.Presentaion

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLogScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val SnapLogViewModel = SnapLogViewModel(context)
   var recognitionText = SnapLogViewModel.recognizedText.collectAsState()
    // Image Picker Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        uri : Uri? ->
        uri?.let {
            imageUri = it
      //       SnapLogViewModel.recognizeText(uri,context)
           // SnapLogViewModel.recognizeObjects(uri,context)

        }

    }
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "ScreenLog") }
            )
        }
    ){
        paddingValues->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(onClick = {
                launcher.launch("image/*")
            }) {
                Text("pick image for ML Testing")
            }

            Button(onClick = {
                GlobalScope.launch {
                    SnapLogViewModel.generateDesc(recognitionText.value)
                }

            }) {
                Text("generate desc")
            }

            Spacer(modifier = Modifier.height(20.dp))
            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null
           , modifier = Modifier.size(200.dp)
       )
   }
           Text(text = recognitionText.value)
        }
    }

}