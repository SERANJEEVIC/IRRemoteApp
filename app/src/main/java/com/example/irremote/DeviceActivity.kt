package com.example.irremote

import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.irremote.databinding.ActivityDeviceBinding
import com.example.irremote.db.ButtonEntity
import com.example.irremote.viewmodel.DeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Shows buttons for a device. Tap a button to transmit its IR code.
 */
class DeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceBinding
    private val vm: DeviceViewModel by viewModels()
    private var irManager: ConsumerIrManager? = null
    private var hasIr = false
    private var deviceId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deviceId = intent.getLongExtra("deviceId", 0)

        // IR detect
        hasIr = packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_CONSUMER_IR)
        if (hasIr) {
            irManager = getSystemService(ConsumerIrManager::class.java)
        } else {
            Toast.makeText(this, "No IR blaster detected. Transmit disabled.", Toast.LENGTH_LONG).show()
        }

        val grid = GridLayoutManager(this, 3)
        binding.recyclerButtons.layoutManager = grid

        val adapter = ButtonGridAdapter { button ->
            transmitButton(button)
        } { button ->
            // edit button
            val dialog = AddButtonDialog.newInstance(deviceId, button)
            dialog.onSave = { label, freq, patternStr ->
                vm.updateButton(button.copy(label = label, freq = freq, patternJson = patternStr))
            }
            dialog.show(supportFragmentManager, "editButton")
        }

        binding.recyclerButtons.adapter = adapter

        binding.fabAddButton.setOnClickListener {
            val dialog = AddButtonDialog.newInstance(deviceId, null)
            dialog.onSave = { label, freq, patternStr ->
                vm.addButton(deviceId, label, freq, patternStr)
            }
            dialog.show(supportFragmentManager, "addButton")
        }

        vm.buttons.observe(this) { list ->
            adapter.submitList(list)
            binding.emptyButtons.text = if (list.isEmpty()) "No buttons. Add one." else ""
        }

        vm.load(deviceId)
    }

    private fun transmitButton(button: ButtonEntity) {
        val pattern = IRUtils.parsePatternJson(button.patternJson)
        if (pattern.isEmpty()) {
            Toast.makeText(this, "Pattern empty or invalid", Toast.LENGTH_SHORT).show()
            return
        }
        if (!hasIr || irManager == null) {
            Toast.makeText(this, "No IR available", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            // transmit
            irManager!!.transmit(button.freq, pattern)
            Toast.makeText(this, "Sent ${button.label}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Transmit failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
