package com.example.myutiltool.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class PermissionDialog constructor(title: String, message: String, permission: String, buttons: Array<String> = arrayOf("OK", "CANCEL")): DialogFragment() {
    private var _title = title
    private var _message = message
    private var _buttons = buttons
    private var _permission = permission
    private var _selectedButton: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(_title)
            .setMessage(_message)
            .setPositiveButton(_buttons[0]) { dialog, id ->
                this.startActivity(Intent(_permission))
                _selectedButton = 0
            }
            .setNegativeButton(_buttons[1]) { dialog, id ->
                //SimpleDialog("notice", "storage permission is required").show(activity.getSupportFragmentManager(), "")
                _selectedButton = 1
            }
        return builder.create()
    }

    fun showWithResult(manager: FragmentManager, tag: String?): Int? {
        super.show(manager, tag)
        return _selectedButton
    }
}