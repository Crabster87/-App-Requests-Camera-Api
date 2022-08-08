package crabster.rudakov.requestscameraapi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import crabster.rudakov.requestscameraapi.databinding.FragmentCaptureBinding
import java.text.SimpleDateFormat
import java.util.*


class CaptureFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1888
        private const val REQUEST_VIDEO_CAPTURE = 1999
    }

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaptureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabCapturePhoto.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                REQUEST_IMAGE_CAPTURE
            )
        }

        binding.fabCaptureVideo.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_VIDEO_CAPTURE),
                REQUEST_VIDEO_CAPTURE
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val photo = data?.extras?.get("data") as Bitmap
                val savedImageUri: Uri? = saveImageInStorage(photo)
                displayMetaData(savedImageUri, MediaStore.Images.Media.DATA)
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                val savedVideoUri: Uri? = data?.data
                displayMetaData(savedVideoUri, MediaStore.Video.Media.DATA)
            }
        }
    }

    private fun saveImageInStorage(image: Bitmap): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val path = MediaStore.Images.Media.insertImage(
            context?.contentResolver,
            image,
            "IMG_${timeStamp}",
            null
        )
        return Uri.parse(path)
    }

    private fun getRealPathFromURI(mediaStoreData: String, contentUri: Uri?): String? {
        val projection = arrayOf(mediaStoreData)
        val cursorLoader = CursorLoader(
            requireActivity(),
            contentUri!!,
            projection,
            null,
            null,
            null
        )
        val cursor: Cursor? = cursorLoader.loadInBackground()
        return cursor.use { cursor1 ->
            val columnIndex = cursor1?.getColumnIndexOrThrow(mediaStoreData)
            cursor1?.moveToFirst()
            columnIndex?.let { cursor1.getString(it) }
        }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("SameParameterValue")
    private fun displayMetaData(uri: Uri?, mediaStoreData: String) {
        val realPath: String? = getRealPathFromURI(mediaStoreData, uri)
        binding.uriTextView.text = "URI:\n${uri.toString()}"
        binding.pathTextView.text = "PATH:\n$realPath"
    }

}