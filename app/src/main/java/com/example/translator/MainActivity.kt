package com.example.translator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.translator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TranslatorViewModel by viewModels()
    private lateinit var speechService: SpeechRecognitionService

    private val RECORD_AUDIO_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupSpeechService()
        setupButtons()
        observeViewModel()
    }

    private fun setupSpinner() {
        val displayLanguages = arrayOf("Vietnamese", "Spanish", "French", "German", "Japanese")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, displayLanguages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter
    }

    private fun getTargetLanguageCode(): String {
        return when(binding.languageSpinner.selectedItem.toString()) {
            "Vietnamese" -> "vi"
            "Spanish" -> "es"
            "French" -> "fr"
            "German" -> "de"
            "Japanese" -> "ja"
            else -> "en"
        }
    }

    private fun setupSpeechService() {
        speechService = SpeechRecognitionService(this)
        
        speechService.onResults = { recognizedText ->
            binding.originalEditText.setText(recognizedText)
            stopRecordingUI() // Auto stop when results arrive
            viewModel.translateText(recognizedText, "en", getTargetLanguageCode())
        }
        
        speechService.onError = { errorMsg ->
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            stopRecordingUI()
        }
        
        speechService.onPartialResults = { partialText ->
            binding.originalEditText.setText(partialText)
        }
    }

    private fun setupButtons() {
        binding.startRecordingButton.setOnClickListener {
            if (checkMicrophonePermission()) {
                startRecordingUI()
                binding.originalEditText.setText("")
                binding.translatedTextView.text = ""
                speechService.startListening()
            } else {
                requestMicrophonePermission()
            }
        }

        binding.stopRecordingButton.setOnClickListener {
            stopRecordingUI()
            speechService.stopListening()
        }

        binding.translateButton.setOnClickListener {
            val text = binding.originalEditText.text.toString()
            if (text.isNotEmpty()) {
                viewModel.translateText(text, "en", getTargetLanguageCode())
            } else {
                Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.translatedText.observe(this) { translated ->
            binding.translatedTextView.text = translated
        }
        
        viewModel.errorMsg.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            binding.translatedTextView.text = "Error: $error"
        }
    }

    private fun startRecordingUI() {
        binding.recordingProgress.visibility = View.VISIBLE
        binding.startRecordingButton.isEnabled = false
        binding.stopRecordingButton.isEnabled = true
    }

    private fun stopRecordingUI() {
        binding.recordingProgress.visibility = View.INVISIBLE
        binding.startRecordingButton.isEnabled = true
        binding.stopRecordingButton.isEnabled = false
    }

    private fun checkMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMicrophonePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechService.destroy()
    }
}
