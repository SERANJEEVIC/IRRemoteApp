package com.example.irremote

import android.app.Activity
import android.content.Intent
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.irremote.databinding.ActivityIrfinderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * IR Finder:
 * - Mode 1: Manual paste pattern (user pastes pattern array)
 * - Mode 2: Optional: If you use Arduino/ESP connected via USB-Serial,
 *   you can send the captured pattern as CSV and paste it here.
 *
 * NOTE: Most phones cannot receive IR. To auto-capture you need an external IR receiver + MCU.
 */
class IRFinderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIrfinderBinding
    private var irManager: ConsumerIrManager? = null
    private var hasIr = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIrfinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hasIr = packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CONSUMER_IR)
        if (hasIr) {
            irManager = getSystemService(ConsumerIrManager::class.java)
        } else {
            Toast.makeText(this, "No IR blaster detected on this phone", Toast.LENGTH_LONG).show()
        }

        binding.btnPastePattern.setOnClickListener {
            val text = binding.etPatternInput.text.toString()
            val parsed = IRUtils.parsePatternCsvSafe(text)
            if (parsed == null || parsed.isEmpty()) {
                Toast.makeText(this, "Invalid pattern", Toast.LENGTH_SHORT).show()
            } else {
                binding.tvPatternPreview.text = parsed.joinToString(",")
            }
        }

        binding.btnSimulateSend.setOnClickListener {
            val freq = binding.etFrequency.text.toString().toIntOrNull() ?: 38000
            val text = binding.etPatternInput.text.toString()
            val parsed = IRUtils.parsePatternCsvSafe(text)
            if (parsed == null || parsed.isEmpty()) {
                Toast.makeText(this, "Invalid pattern", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!hasIr || irManager == null) {
                Toast.makeText(this, "No IR available to send", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    irManager!!.transmit(freq, parsed)
                    Toast.makeText(this@IRFinderActivity, "Transmitted pattern", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@IRFinderActivity, "Transmit failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnCopyToClipboard.setOnClickListener {
            val txt = binding.tvPatternPreview.text.toString()
            if (txt.isNotBlank()) {
                val ci = getSystemService(Activity.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("irpattern", txt)
                ci.setPrimaryClip(clip)
                Toast.makeText(this, "Copied pattern", Toast.LENGTH_SHORT).show()
            }
        }

        // NOTE: Auto-capture via Arduino: show instructions
        binding.tvArduinoInstructions.text = getArduinoInstructions()
    }

    private fun getArduinoInstructions(): String {
        return "Auto-capture instructions:\n\n" +
                "1) Use TSOP38238 connected to Arduino and run the included sketch (see README).\n" +
                "2) Make Arduino print CSV array (e.g. 9000,4500,560,560,... ) via Serial.\n" +
                "3) Connect Arduino to phone via USB-OTG and use a USB-Serial bridge app OR implement USB host reading on Android and paste the serial output here.\n\n" +
                "This app does not include a generalized serial driver — please paste the printed CSV in the text box above."
    }
}
