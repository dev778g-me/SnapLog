package com.dev.snaplog.repo

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.net.toUri
import com.dev.snaplog.BuildConfig
import com.dev.snaplog.Db.ScreenShotDb
import com.dev.snaplog.Db.ScreenshotData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SnapLogRepo {

    // the ml text extractor
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // configuring the gen ai Gemini 2.0 flashhhhhhhhhhhhhhhhhhhhhhhhh
      private val model = GenerativeModel(
          "gemini-2.0-flash",
          apiKey = BuildConfig.API_KEY,
          generationConfig  {
              temperature = 1f
              topK = 40
              topP = 0.95f
              maxOutputTokens = 8192
          },
          systemInstruction = content { text(
              "Given a text from a screenshot, generate a concise and informative title along with a detailed description in simple language"
          ) }
      )
    private val processingQueue = Channel<Pair<String, Context>>(capacity = Channel.UNLIMITED)
    private val processingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val chat = model.startChat()
init {
    processingScope.launch {
processQueue()
    }
}
       // function to generate image URi
       fun getImageContentUri(
           context: Context,
           filepath : String
       ): Uri?{
           val projection = arrayOf(MediaStore.Images.Media._ID)
           val selection = "${MediaStore.Images.Media.DATA} = ?"
              val selectionArgs = arrayOf(filepath)
           context.contentResolver.query(
               MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
               projection,selection,selectionArgs,null
           )?.use{
               if (it.moveToFirst()){
                   val id = it.getLong(0)
                   return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)
               }

           }
           return null
       }

   suspend fun recognizeText(imageUri: Uri, context: Context): String{
         val image = InputImage.fromFilePath(context, imageUri)
        return suspendCoroutine {
            continuation ->
            textRecognizer.process(image).addOnSuccessListener {
                continuation.resume(it.text ?: "")
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
   }

    // ai generated title and description
    fun extractTitle(response: String): Pair<String, String> {
        val cleanedResponse = response.replace(
            Regex("""\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2}\.\d{3}\s\d+-\d+\sSystem\.out\s+com\.dev\.snaplog\s+I\s+"""),
            ""
        )
        val titleRegex = Regex("""\*\*Title:\*\*\s*([^\n]+)""")
        val descriptionRegex = Regex("""\*\*Description:\*\*\s*([\s\S]*)$""")
        val titleMatch = titleRegex.find(cleanedResponse)
        val title = titleMatch?.groupValues?.get(1)?.trim() ?: "No Title"
        val descriptionMatch = descriptionRegex.find(cleanedResponse)
        val description = descriptionMatch?.groupValues?.get(1)?.trim() ?: "No Description"
        return Pair(title, description)
    }


    suspend fun getDescriptionForAllImages(imagePathList: List<String> , context: Context){
      imagePathList.forEach {
          imagePath->
          processingQueue.send(imagePath to context)
      }
    }
    private suspend fun processQueue (){
      while (true) {
          val (path, context) = processingQueue.receive()
          val database = ScreenShotDb.getDatabase(context)
          val screenshotDao = database.getScreenshotDao()

          val imageUri = getImageContentUri(context,path) ?: path.toUri()

          val extractedMlText = withContext(Dispatchers.IO) {
              recognizeText(imageUri,context)
          }
            if (extractedMlText.isEmpty()) continue

          val aiResponse = try {
              withContext(Dispatchers.IO) {
                  chat.sendMessage(extractedMlText).text.toString()
              }
          }catch (e: Exception){
              println(e.localizedMessage)
              delay(5000L)
              continue
          }

          val (title, desc) = extractTitle(aiResponse)


              // saving in database
          withContext(Dispatchers.IO) {
              val screenshotdata = ScreenshotData(
                  title = title,
                  description = desc,
                  note = "",
                  id = 0,
                  screenshotPath = path
              )
             try {
                 screenshotDao.insertScreenshotData(screenshotdata)
             }catch (e: Exception){
                 println("error inserting data${e.localizedMessage}")
             }
          }
      }
        delay(10000L)
    }
}