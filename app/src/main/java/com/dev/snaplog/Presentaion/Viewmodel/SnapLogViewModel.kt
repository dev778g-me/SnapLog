package com.dev.snaplog.Presentaion.Viewmodel

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.snaplog.Db.ScreenShotDb
import com.dev.snaplog.Db.ScreenshotData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SnapLogViewModel(context: Context) : ViewModel() {

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var _recognizedText = MutableStateFlow("")
    val recognizedText = _recognizedText.asStateFlow()


    val database = ScreenShotDb.getDatabase(context)
    val screenshotDao = database.getScreenshotDao()

    val screenshot : LiveData<List<ScreenshotData>> = screenshotDao.getAllScreenShotData()

    @SuppressLint("SuspiciousIndentation")
    val model = GenerativeModel(
        "gemini-2.0-flash",
        apiKey = com.dev.snaplog.BuildConfig.API_KEY,
        generationConfig {
            temperature = 1f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 8192
        },
        systemInstruction = content { text("Given a text from a screenshot, generate a concise and informative title along with a detailed description in simple language.") })
        val chat = model.startChat()




    suspend fun generateDesc(text: String) : String{
       return withContext (Dispatchers.IO){
           val response = chat.sendMessage(text)
           response.text.toString()
       }

    }

    fun extractTitle(response: String) : Pair<String, String> {
        var title = "No Title"
        var description = StringBuilder()
        val lines = response.split("\n").map {
            it.trim()
        }
        for (i in response.indices) {
            val line = lines[i]
            if (line.startsWith("**Title:**")) {
                title = line.removePrefix("**Title:**").trim()
            } else if (line.startsWith("**Description:**")) {
                for (j in i until lines.size) {
                    description.append(lines[j]).append("\n")
                }
                break
            }
        }
        return Pair(title,description.toString().trim())
    }

 //function for getting description (all function implementation)
    fun getDescriptionForAllImages(imagePathList: List<String>,context: Context) {
        viewModelScope.launch {
            imagePathList.forEach {
                paths->
              val imageUri = getImageContentUri(context,paths) ?: paths.toUri()
                val mlText = withContext(Dispatchers.IO) {
                    println("the ml extracting text : ${Thread.currentThread().name}")
                    recognizeText(imageUri,context)
                }
                if (mlText.isEmpty() ) {
                    return@forEach
                }

                val response = withContext(Dispatchers.IO) {
                    println("the ai response thread ${Thread.currentThread().name}")
                    generateDesc(mlText)
           }
            val (title,description,) =  withContext (Dispatchers.IO){
                extractTitle(response)
            }
                withContext (Dispatchers.IO){
                    try {
                        screenshotDao.insertScreenshotData(ScreenshotData(
                            id = 0,
                            title = title,
                            screenshotPath = paths,
                            note = "",
                            description = description
                        ))

                    }catch (e: Exception) {
                        println(e.localizedMessage)
                    }
                }

                println(mlText)
              //  println(response)
               // println(title)
                println(description)

            }
        }
    }



    fun getImageContentUri(context: Context, filePath: String): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DATA}=?"
        val selectionArgs = arrayOf(filePath)

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(0)
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
        }
        return null
    }

    // function to get the text from the screenshot via ML KIt

    suspend fun recognizeText(uri: Uri, context: Context): String {
        val image = InputImage.fromFilePath(context, uri)
        return suspendCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { text ->
                    println("MLKit: Recognized text: ${text.text}")
                    continuation.resume(text.text ?: "") // Resume with recognized text
                }
                .addOnFailureListener { error ->
                    println("MLKit Error: $error")
                    continuation.resumeWithException(error) // Resume with error
                }
        }
    }

}