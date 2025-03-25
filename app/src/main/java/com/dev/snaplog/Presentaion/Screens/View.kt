package com.dev.snaplog.Presentaion.Screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dev.snaplog.Db.ScreenshotData
import com.dev.snaplog.Presentaion.Viewmodel.ScreenshotFetchViewmodel
import com.dev.snaplog.Presentaion.Viewmodel.SnapLogViewModel
import com.dev.snaplog.Presentaion.navigation.Routes
import com.dev.snaplog.service.Core
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun View(
    screenshotFetchViewmodel: ScreenshotFetchViewmodel,
    snapLogViewModel: SnapLogViewModel,
    navController: NavHostController,
    //onClick: () -> Unit
) {
    val listState = rememberLazyGridState()
    val visibleItems = rememberSaveable { mutableStateOf(mutableMapOf<Int, Boolean>()) }
    //var screenshots by remember { mutableStateOf(emptyList<String>()) }
    val context = LocalContext.current
    // Observe LiveData from ViewModel
    val screenshots by snapLogViewModel.screenshot.observeAsState(emptyList())

    var newScreenShotPath by remember { mutableStateOf<List<String>>(emptyList()) }

    var hasProcessed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            val processedPathsSet = prefs.getStringSet("processedScreenshots", emptySet()) ?: emptySet()

            val allScreenshotPaths = screenshotFetchViewmodel.getAllScreenshot(context)
            println("Found ${allScreenshotPaths.size} screenshots on device")

            val currentScreenshots = screenshots.toList()
            val dbProcessedPaths = currentScreenshots.map { it.screenshotPath }.toSet()

            val allProcessedPaths = dbProcessedPaths + processedPathsSet
            println("Found ${allProcessedPaths.size} processed screenshots in database + prefs")

            val newPaths = allScreenshotPaths.filter { path -> path !in allProcessedPaths }
            println("Found ${newPaths.size} new screenshots to process")

            newScreenShotPath = newPaths

            if (newPaths.isNotEmpty()) {
                println("Starting processing of new screenshots")
                val intent = Intent(context, Core::class.java).apply {
                    putStringArrayListExtra("imagelist", ArrayList(newPaths))
                }
                // all the function runs on the foregroundservice
                ContextCompat.startForegroundService(context, intent)

                val updatedProcessedPaths = processedPathsSet.toMutableSet().apply { addAll(newPaths) }
                prefs.edit()
                    .putStringSet("processedScreenshots", updatedProcessedPaths)
                    .putBoolean("isFirstLaunch", false) // Set flag to false after running
                    .apply()
            } else {
                println("No new screenshots to process or already processed")
                prefs.edit().putBoolean("isFirstLaunch", false).apply() // Set flag even if no new images
            }
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
                 state = listState,
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(paddingValues)
                     .consumeWindowInsets(paddingValues),
                 columns = GridCells.Fixed(2),
                 contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                 flingBehavior = ScrollableDefaults.flingBehavior()
             ) {
                 items(screenshots, key = {it.id}) {
                     val isVisible by remember { derivedStateOf { visibleItems.value[it.id] ?: false } }

                     LaunchedEffect(Unit) {
                         if (visibleItems.value[it.id] == null) { // Only trigger animation if not already visible
                             delay(50L * it.id.coerceAtMost(10))
                             visibleItems.value = visibleItems.value.toMutableMap().apply { put(it.id, true) }
                         }
                     }

                     AnimatedVisibility(
                         visible = isVisible,
                         enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
                             initialOffsetY = { it * 10 }
                         ),
                         exit = fadeOut(animationSpec = tween(300)) + slideOutVertically()
                     ) {
                         ScreenShotView(it, onClick = {
                             val screenshotDataJson = Json.encodeToString(it)
                             val encodeData = Uri.encode(screenshotDataJson)
                          navController.navigate("${Routes.FullImageScreen.route}/$encodeData")
                         })
                     }

                 }
             }
         }
}

@Composable
fun ScreenShotView(imagePath: ScreenshotData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick.invoke()
            }
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
                 .aspectRatio(9f / 16f)
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
