package com.example.irremote

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.irremote.databinding.DialogAddDeviceBinding

class AddDeviceDialog : DialogFragment() {

    var onSave: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddDeviceBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireContext())
            .setTitle("Add Device")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val name = binding.etDeviceName.text.toString().trim()
                if (name.isNotBlank()) onSave?.invoke(name)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
