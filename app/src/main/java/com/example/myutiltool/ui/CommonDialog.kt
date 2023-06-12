package com.example.myutiltool.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.myutiltool.Common
import kotlin.jvm.internal.FunctionReference

class CommonDialog constructor(title: String = "dialogue", message: String = "no text", buttons: Array<String> = arrayOf("OK", "CANCEL"), function: FunctionReference): DialogFragment() {
    private var _title = title
    private var _message = message
    private var _buttons = buttons

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(_title)
            .setMessage(_message)
            .setPositiveButton(_buttons[0]) { dialog, id ->
                println("dialog:$dialog which:$id")
            }
            .setNegativeButton(_buttons[1]) { dialog, id ->
                println("dialog:$dialog which:$id")
            }

        return builder.create()
    }
}