package crabster.rudakov.requestscameraapi

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    @ApplicationContext private var context: Context?
) : ViewModel() {

    var mediaFileUri: Uri? = null

    override fun onCleared() {
        super.onCleared()
        context = null
    }

    @Suppress("DEPRECATION")
    fun createPictureTempFile(intent: (Intent) -> Unit): String? {
        val filesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = createTempFile("JPEG_${timeStamp}_", ".jpeg", filesDir)
            mediaFileUri = createUri(file)
            Log.d("PICTURE URI VM", "$mediaFileUri")
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri)
            intent(captureIntent)
            file.absolutePath
        } catch (ex: Exception) {
            null
        }
    }

    @Suppress("DEPRECATION")
    fun createVideoTempFile(intent: (Intent) -> Unit): String? {
        val filesDir = context?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = createTempFile("VID_${timeStamp}_", ".mp4", filesDir)
            mediaFileUri = createUri(file)
            Log.d("VIDEO URI VM", "$mediaFileUri")
            val captureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mediaFileUri)
            intent(captureIntent)
            file.absolutePath
        } catch (ex: Exception) {
            null
        }
    }

    private fun createUri(file: File): Uri? {
        mediaFileUri = FileProvider.getUriForFile(
            context!!,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        return mediaFileUri
    }

}