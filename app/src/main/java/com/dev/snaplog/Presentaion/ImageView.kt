//package com.dev.snaplog.Presentaion
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Share
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FilledTonalIconButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.core.net.toUri
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import com.dev.snaplog.Db.ScreenshotData
//import kotlinx.serialization.json.Json
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FullImage(dataJson: String,navController: NavController) {
//    val screenshotData = remember { Json.decodeFromString<ScreenshotData>(dataJson) } // ✅ Convert JSON to Object
//    Scaffold (
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(text = "Details")
//                },
//                navigationIcon = {
//                    FilledTonalIconButton(onClick = {
//                        navController.navigateUp()
//                    }) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
//                    }
//                }
//                , actions = {
//                    IconButton(onClick = {}) {
//                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
//                    }
//                    FilledTonalIconButton(onClick = {}) {
//                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
//                    }
//                }
//
//            )
//        }
//    ){
//        paddingValues->
//
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .padding(paddingValues)) {
//            AsyncImage(
//                model = screenshotData.screenshotPath!!.toUri(), contentDescription = null
//
//            )
//
//        }
//    }
//
//}
