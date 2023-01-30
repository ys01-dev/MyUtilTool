package com.example.myutiltool

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class Common {
    val FLAG_READFILE = 1
    val FLAG_WRITEFILE = 2

    fun browse(activity: Activity, mode: Int) {
        var intent: Intent? = null

        when(mode) {
            this.FLAG_READFILE -> {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            }
            this.FLAG_WRITEFILE -> {
                intent =  Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, FileInfo.selectedFileName ?: "newText.txt")
                }
            }
            else -> {}
        }
        if(intent != null) startActivityForResult(activity, intent, mode, null)
    }

    fun readFile(context: Context, uri: Uri?): String? {
        var retStr: String? = null

        BufferedReader(InputStreamReader(context.contentResolver.openInputStream(uri!!))).use {
            try {
                val list = it.readLines()
                retStr = if(list.any()) list.joinToString("\n") else null
            } catch (e: Exception) {
                throw Exception(e.message)
            }
        }
        return retStr
    }

    fun writeFile(context: Context, str: String?, uri: Uri?): Pair<Boolean, String> {
        var err = false
        var retStr = ""

        this.getFilename(context, uri).let {
            err = it == null
            if(!err && it!!.startsWith("(invalid")) {
                err = true
                retStr = "unnamed file was saved as \"invalid\""
            }
        }

        if(!err) {
            BufferedWriter(OutputStreamWriter(context.contentResolver.openOutputStream(uri!!))).use {
                retStr = try {
                    it.write(str)
                    "saved"
                } catch (e: Exception) {
                    err = true
                    e.message.toString()
                }
            }
        }
        return Pair(!err, retStr)
    }

    fun getFileExt(fileName: String?): String? {
        return fileName?.substring(fileName.indexOf("."))
    }

    @SuppressLint("Range")
    fun getFilename(context: Context, uri: Uri?): String? {
        var filename: String? = null

        try {
            context.contentResolver.query(uri!!, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use {
                if (it.moveToFirst()) {
                    filename = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
        return filename
    }
}