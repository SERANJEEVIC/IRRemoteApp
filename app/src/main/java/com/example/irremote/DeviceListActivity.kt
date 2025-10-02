package com.example.irremote

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.irremote.db.DeviceEntity
import com.example.irremote.databinding.ActivityDeviceListBinding
import com.example.irremote.viewmodel.DeviceListViewModel

/**
 * Shows list of devices (Fan1, Fan2...) and allows add/delete.
 */
class DeviceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceListBinding
    private val vm: DeviceListViewModel by viewModels()
    private lateinit var adapter: DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DeviceListAdapter { device ->
            // open Device activity
            val it = Intent(this, DeviceActivity::class.java)
            it.putExtra("deviceId", device.id)
            startActivity(it)
        } { device ->
            // long-press or delete
            confirmDelete(device)
        }

        binding.recyclerDevices.layoutManager = LinearLayoutManager(this)
        binding.recyclerDevices.adapter = adapter

        binding.fabAddDevice.setOnClickListener {
            val dialog = AddDeviceDialog()
            dialog.onSave = { name ->
                vm.addDevice(name)
            }
            dialog.show(supportFragmentManager, "addDevice")
        }

        binding.btnImport.setOnClickListener {
            // simple import/export via file - uses helper in repository
            vm.importFromJson(this) { ok, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnExport.setOnClickListener {
            vm.exportToJson(this) { ok, path ->
                val m = if (ok) "Saved to $path" else "Export failed: $path"
                Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOpenIrFinder.setOnClickListener {
            startActivity(Intent(this, IRFinderActivity::class.java))
        }

        vm.devices.observe(this) { list ->
            adapter.submitList(list)
            binding.emptyView.text = if (list.isEmpty()) "No devices. Add one." else ""
        }
    }

    private fun confirmDelete(device: DeviceEntity) {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Delete device '${device.name}' and its buttons?")
            .setPositiveButton("Delete") { _, _ -> vm.deleteDevice(device) }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
