package com.example.myutiltool.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class SimpleDialog constructor(title: String, message: String): DialogFragment()  {
    private var _title = title
    private var _message = message

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(_title)
            .setMessage(_message)
            .setPositiveButton("OK") { dialog, id ->
            }
        return builder.create()
    }
}