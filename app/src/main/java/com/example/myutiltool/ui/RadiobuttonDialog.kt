package com.example.myutiltool.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R

class RadiobuttonDialog(context: MainActivity, title: String, items: Array<String>): DialogFragment() {
    private var _title = title
    private var _items = items
    private var _main = context

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
            .setTitle(_title)
            .setItems(_items) { dialog, index ->
                _main.findViewById<Button>(R.id.btn_charCode).text = _items[index]
            }
            .setPositiveButton("OK") { dialog, id ->
            }
        return builder.create()
    }
}