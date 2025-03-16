package com.dev.snaplog.Presentaion

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dev.snaplog.Db.ScreenshotData
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import com.dev.snaplog.navigation.Routes
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun View(
    screenshotFetchViewmodel: ScreenshotFetchViewmodel,
    snapLogViewModel: SnapLogViewModel,
    navController: NavHostController
) {
    val visibleItems = remember { mutableStateMapOf<Int, Boolean>() }
    //var screenshots by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current
    // Observe LiveData from ViewModel
    val screenshots by snapLogViewModel.screenshot.observeAsState(emptyList())

    var newScreenShotPath by remember { mutableStateOf<List<String>>(emptyList()) }

    var hasProcessed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Use SharedPreferences to persist the processed flag across app launches
        val prefs = context.getSharedPreferences("ScreenshotPrefs", Context.MODE_PRIVATE)
        val hasProcessed = prefs.getBoolean("hasProcessed", false)

        // Get all available screenshots on the device
        val allScreenshotPaths = screenshotFetchViewmodel.getAllScreenshot(context)
        println("Found ${allScreenshotPaths.size} screenshots on device")

        // Get a snapshot of the current processed paths (assumed to be persisted in your database)
        val currentScreenshots = screenshots.toList()
        val processedPaths = currentScreenshots.map { it.screenshotPath }.toSet()
        println("Found ${processedPaths.size} processed screenshots in database")

        // Filter out screenshots that have already been processed
        val newPaths = allScreenshotPaths.filter { path ->
            path !in processedPaths
        }
        println("Found ${newPaths.size} new screenshots to process")

        // Update state with new paths
        newScreenShotPath = newPaths

        // Only process new screenshots if they haven't been processed already across app launches
        if (newPaths.isNotEmpty() && !hasProcessed) {
            println("Starting processing of new screenshots")
            snapLogViewModel.getDescriptionForAllImages(newPaths, context)
            // Mark as processed in SharedPreferences so the next app launch won't process them again
            prefs.edit().putBoolean("hasProcessed", true).apply()
        } else {
            println("No new screenshots to process or already processed")
        }
    }


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
             topBar = {
                 CenterAlignedTopAppBar(
                     title = {Text(text = "ScreenLog", fontWeight = FontWeight.SemiBold)},
                     scrollBehavior = scrollBehavior,
                     navigationIcon = {
                         FilledTonalIconButton(onClick = {}) {
                             Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                         }
                     },
                     actions = {
                         FilledTonalIconButton(onClick = {}) {
                             Icon(imageVector = Icons.Default.Menu, contentDescription = null)
                         }
                     }
                 )
             },
        contentWindowInsets = WindowInsets(0.dp)

         ){ paddingValues ->
             LazyVerticalGrid(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(paddingValues)
                     .consumeWindowInsets(paddingValues),
                 columns = GridCells.Fixed(2),
                 contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                 flingBehavior = ScrollableDefaults.flingBehavior()
             ) {
                 items(screenshots, key = {it.id}) {
                     val isVisible by remember { derivedStateOf { visibleItems[it.id] ?: false } }

                     LaunchedEffect(it.id * 50L) {
                         visibleItems[it.id] = true
                     }
                     AnimatedVisibility(
                         visible = isVisible,
                         enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                             initialOffsetY = { it * 10 }
                         ),
                         exit = fadeOut(animationSpec = tween(300)) + slideOutVertically()
                     ) {
                         ScreenShotView(it)
                     }

                 }
             }
         }
}

@Composable
fun ScreenShotView(imagePath: ScreenshotData, ) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {  }
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box {
            // Image with rounded corners
            AsyncImage(
                model = imagePath.screenshotPath?.toUri(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                  //  .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Gradient overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            ) {
                Text(
                    text = imagePath.title.orEmpty(),
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
