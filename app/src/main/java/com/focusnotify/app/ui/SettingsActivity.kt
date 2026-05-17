package com.focusnotify.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.focusnotify.app.databinding.ActivitySettingsBinding
import com.focusnotify.app.util.PrefsHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Settings"

        // Load current values
        binding.etFomoScore.setText(PrefsHelper.getFomoScore(this).toString())
        binding.etParticipantId.setText(PrefsHelper.getParticipantId(this))

        when (PrefsHelper.getMode(this)) {
            PrefsHelper.MODE_BASELINE -> binding.rbBaseline.isChecked = true
            PrefsHelper.MODE_BATCHING -> binding.rbBatching.isChecked = true
            else                      -> binding.rbAdaptive.isChecked  = true
        }

        binding.btnSave.setOnClickListener { saveSettings() }
    }

    private fun saveSettings() {
        val fomoText = binding.etFomoScore.text.toString()
        val fomoScore = fomoText.toIntOrNull()

        if (fomoScore == null || fomoScore < 10 || fomoScore > 50) {
            Toast.makeText(this, "Enter FoMO score between 10 and 50", Toast.LENGTH_SHORT).show()
            return
        }

        val pid = binding.etParticipantId.text.toString().trim()
        if (pid.isBlank()) {
            Toast.makeText(this, "Enter a participant ID", Toast.LENGTH_SHORT).show()
            return
        }

        val mode = when (binding.rgMode.checkedRadioButtonId) {
            binding.rbBaseline.id -> PrefsHelper.MODE_BASELINE
            binding.rbBatching.id -> PrefsHelper.MODE_BATCHING
            else                  -> PrefsHelper.MODE_ADAPTIVE
        }

        PrefsHelper.setFomoScore(this, fomoScore)
        PrefsHelper.setMode(this, mode)
        PrefsHelper.setParticipantId(this, pid)

        Toast.makeText(this, "✅ Settings saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
