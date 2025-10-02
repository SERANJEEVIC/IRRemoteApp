package com.example.irremote

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.irremote.databinding.DialogAddButtonBinding

class AddButtonDialog : DialogFragment() {

    companion object {
        fun newInstance(deviceId: Long, button: Any?): AddButtonDialog {
            val d = AddButtonDialog()
            // you can set args if needed
            return d
        }
    }

    var onSave: ((label: String, freq: Int, patternJson: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddButtonBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireContext())
            .setTitle("Add / Edit Button")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val label = binding.etLabel.text.toString().trim()
                val freq = binding.etFrequency.text.toString().toIntOrNull() ?: 38000
                val patternCsv = binding.etPattern.text.toString().trim()
                val parsed = IRUtils.parsePatternCsvSafe(patternCsv)
                if (label.isBlank() || parsed == null || parsed.isEmpty()) {
                    // invalid
                    return@setPositiveButton
                }
                val json = IRUtils.intArrayToJson(parsed)
                onSave?.invoke(label, freq, json)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}
