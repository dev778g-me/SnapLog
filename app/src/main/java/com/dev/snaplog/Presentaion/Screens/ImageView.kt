package com.dev.snaplog.Presentaion.Screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dev.snaplog.Db.ScreenShotDb
import com.dev.snaplog.Db.ScreenshotData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullImage(data: ScreenshotData, navController: NavController) {
var noteText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val database = ScreenShotDb.getDatabase(context)
    val screenshotDao = database.getScreenshotDao()
    var isNotenull by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Details")
                },
                navigationIcon = {
                    FilledTonalIconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
                , actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    }
                    FilledTonalIconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }
                }

            )
        }, bottomBar = {
            BottomAppBar(
                actions = {

                    OutlinedTextField(
                        colors = OutlinedTextFieldDefaults.colors(

                            focusedBorderColor =Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        maxLines = 1,
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        value = noteText,
                        onValueChange = {
                            it-> noteText = it
                        },
                        placeholder = {Text("Add Your Note Here")}
                        , leadingIcon = {
                            Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))

                }
                , floatingActionButton = {
                    FloatingActionButton(onClick = {
                       if (noteText.isNotEmpty()) {
                           scope.launch {
                               screenshotDao.insertScreenshotData(data.copy(note = noteText))
                           }
                       } else{
                           Toast.makeText(context, "Enter the Note First", Toast.LENGTH_SHORT).show()

                       }
                        if (data.note== "") {
                            isNotenull = false
                        } else{
                            isNotenull = true
                        }
                    })   {
                        Text(text = "Save")
                       // Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    }
                }
            )

        },

        contentWindowInsets = WindowInsets(0.dp)
    ){
        paddingValues->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())

        ) {
            Card(
                elevation = CardDefaults.cardElevation(20.dp),
                modifier = Modifier
                    .padding(16.dp) // Proper padding outside the Card
                    .clip(RoundedCornerShape(20.dp)) // Apply rounded corners to the Card
            ) {
                data.screenshotPath?.let { path ->
                    AsyncImage(
                        model = path.toUri(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth() // Ensures it fills the Card properly
                            .aspectRatio(1f) // Maintains a square shape
                    )
                }
            }
            AnimatedVisibility(
                visible = isNotenull
            ) {

            }
            CardText(
                title = "Note",
                description = data.note?:"Add note"
            )
            CardText(
               title = "Title",
               description = data.title.toString()
           )

           CardText(
               title = "Description",
               description = data.description.toString()
           )


        }
    }

}

@Composable
fun CardText(title: String, description: String) {
    Card (
        modifier = Modifier.padding(16.dp)
    ){
        ListItem(
            tonalElevation = 10.dp,
            overlineContent = {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            },
            headlineContent = {
                Text(description)
            }
        )
    }

}