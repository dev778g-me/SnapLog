package com.dev.snaplog.Presentaion.Viewmodel

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max

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

    fun extractTitle(response: String): Pair<String, String> {
        // Clean up the input by removing log timestamps and identifiers
        val cleanedResponse = response.replace(
            Regex("""\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2}\.\d{3}\s\d+-\d+\sSystem\.out\s+com\.dev\.snaplog\s+I\s+"""),
            ""
        )

        // This regex captures everything after "**Title:**" until a newline.
        val titleRegex = Regex("""\*\*Title:\*\*\s*([^\n]+)""")

        // This regex captures everything after "**Description:**" until the end or another marker
        val descriptionRegex = Regex("""\*\*Description:\*\*\s*([\s\S]*)$""")

        val titleMatch = titleRegex.find(cleanedResponse)
        val title = titleMatch?.groupValues?.get(1)?.trim() ?: "No Title"

        val descriptionMatch = descriptionRegex.find(cleanedResponse)
        val description = descriptionMatch?.groupValues?.get(1)?.trim() ?: "No Description"

        return Pair(title, description)
    }


    
    //function for getting description (all function implementation)
    fun getDescriptionForAllImages(imagePathList: List<String>,context: Context) {
        val maxRequest = 15
        val delayBetweenRequests = (60 * 1000) / maxRequest
        viewModelScope.launch {
            for (path in imagePathList) {
                //getURi
                val imageUri = getImageContentUri(context,path) ?: path.toUri()

                // extracting the text
                val mlText = withContext (Dispatchers.IO){
                    println("Extraction on thread ${Thread.currentThread().name}")
                    recognizeText(imageUri,context)
                }
                if (mlText.isEmpty()) continue

                val response = withContext (Dispatchers.IO){
                    println("ai Thread ${Thread.currentThread().name}")
                    delay(1000L)
                    generateDesc(mlText)
                }

                val  (title,description) = withContext (Dispatchers.IO){
                    extractTitle(response)
                }

                withContext(Dispatchers.IO) {
                    try {
                        screenshotDao.insertScreenshotData(
                            ScreenshotData(
                                id = 0,
                                title = title,
                                screenshotPath = path,
                                note = "",
                                description = description
                            )
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                    }


                }
                println(path)

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
                    continuation.resume(text.text ?: "") // Resume with recognized text
                }
                .addOnFailureListener { error ->
                    println("MLKit Error: $error")
                    continuation.resumeWithException(error) // Resume with error
                }
        }
    }

}