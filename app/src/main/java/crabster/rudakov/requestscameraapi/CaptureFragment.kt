package crabster.rudakov.requestscameraapi

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import crabster.rudakov.requestscameraapi.databinding.FragmentCaptureBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CaptureFragment : Fragment() {

    private var _binding: FragmentCaptureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CaptureViewModel by viewModels()
    private var mediaFilePath: String? = null
    private var mediaFileUri: Uri? = null
    private var job: Job? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            Log.d("URI FROM INTENT", "${result.data!!.data}")
            Log.d("URI FROM FRAGMENT", "$mediaFileUri")
            binding.uriTextView.text = mediaFileUri.toString()
            binding.pathTextView.text = mediaFilePath
        }
    }

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

        binding.fabCapturePhoto.setOnClickListener { takeImage() }
        binding.fabCaptureVideo.setOnClickListener { takeVideo() }
    }

    override fun onDestroy() {
        super.onDestroy()
        job = null
        _binding = null
    }

    private fun takeImage() {
        if (job?.isActive == true) return
        job = lifecycleScope.launch {
            mediaFilePath = viewModel.createPictureTempFile {
                cameraLauncher.launch(it)
            }
            mediaFileUri = viewModel.mediaFileUri
        }
    }

    private fun takeVideo() {
        if (job?.isActive == true) return
        job = lifecycleScope.launch {
            mediaFilePath = viewModel.createVideoTempFile {
                cameraLauncher.launch(it)
            }
            mediaFileUri = viewModel.mediaFileUri
        }
    }

}