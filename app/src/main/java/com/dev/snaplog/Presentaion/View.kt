package com.dev.snaplog.Presentaion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun View(screenshotFetchViewmodel: ScreenshotFetchViewmodel,snapLogViewModel: SnapLogViewModel) {
    //var screenshots by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current
    // Observe LiveData from ViewModel
    val screenshots by snapLogViewModel.screenshot.observeAsState(emptyList())


    LaunchedEffect(screenshots) {
        println("hiii : ${screenshots}")
    }
         Scaffold (
             topBar = {
                 TopAppBar(
                     title = { Text(
                         text = "ScreenLog"
                     ) }
                 )
             },
             floatingActionButton = {
                 FloatingActionButton(onClick = {
                  //   snapLogViewModel.getDescriptionForAllImages(imagePathList = screenshots,context)
                 }) {
                     Text(text = "test")
                 }
             }
         ){ paddingValues ->
             LazyVerticalGrid(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(paddingValues),
                 columns = GridCells.Fixed(3),
                 contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                 flingBehavior = ScrollableDefaults.flingBehavior()
             ) {
                 items(screenshots) {
                    ScreenShotView(it.screenshotPath!!)
                 }
             }
         }



}

@Composable
fun ScreenShotView(imagePath : String) {
    Card(
      elevation = CardDefaults.cardElevation(10.dp)
        , modifier = Modifier.padding(5.dp),
        border = BorderStroke(
            width = 5.dp,
            brush = SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) // Lightened primary color
        )


    ){
        AsyncImage(
            model = imagePath.toUri(),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().size(150.dp).align(alignment = Alignment.CenterHorizontally),
            contentScale = ContentScale.Crop
        )
    }
}

