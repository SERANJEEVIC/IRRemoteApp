package com.example.irremote

import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var irManager: ConsumerIrManager? = null
    private var hasIr: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pm = packageManager
        hasIr = pm.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR)
        if (!hasIr) {
            Toast.makeText(this, "No IR blaster detected. Transmit disabled.", Toast.LENGTH_LONG).show()
        } else {
            irManager = getSystemService(ConsumerIrManager::class.java)
        }

        // TODO: Setup RecyclerView to show Devices from Room, FAB to add Device
    }

    fun transmit(freq: Int, pattern: IntArray) {
        if (!hasIr || irManager == null) {
            Toast.makeText(this, "IR not available", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            irManager!!.transmit(freq, pattern)
            Toast.makeText(this, "IR sent (freq ${freq}Hz)", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Transmit failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
