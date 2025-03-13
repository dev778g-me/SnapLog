package com.dev.snaplog.Presentaion.Viewmodel

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.snaplog.constants.Constants
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SnapLogViewModel(context: Context) : ViewModel() {
    val Const = Constants()
    val  context = context

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var _recognizedText = MutableStateFlow("")
    val recognizedText = _recognizedText.asStateFlow()


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


    fun getDescriptionForAllImages(imagePathList: List<String>) {
        viewModelScope.launch {

            imagePathList.forEach {
                paths->
              val imageUri = getImageContentUri(context,paths) ?: paths.toUri()
                val mlText = withContext(Dispatchers.IO) {
                    println(Thread.currentThread().name)
                    recognizeText(imageUri,context)
                }
                val mk = async {
                    println(Thread.currentThread().name)
                }.await()
                if (mlText.isEmpty() ) {
                    return@forEach
                }

                val response = withContext(Dispatchers.IO) {
                    generateDesc(mlText)
                }

                println(mlText)
                println(response)

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