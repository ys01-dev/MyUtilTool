package com.example.myutiltool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.example.myutiltool.ui.memo.MemoViewModel
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class Common {
    val FLAG_READFILE = 1
    val FLAG_WRITEFILE = 2
    val FLAG_UNZIP = 3
    val FLAG_SELECTZIP = 4
    val REQUEST_CODE_EXSTORAGE = 101

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

    fun writeFile(context: Context, str: String?, uri: Uri?) {
        try {
            BufferedWriter(OutputStreamWriter(context.contentResolver.openOutputStream(uri!!))).use {
                it.write(str)
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    @SuppressLint("Range")
    fun getFileName(context: Context, uri: Uri?): String? {
        var filename: String? = null

        try {
            context.contentResolver.query(uri!!, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DOCUMENT_ID), null, null, null)?.use {
                if (it.moveToFirst()) {
                    filename = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }

        return filename
    }

    @SuppressLint("Range")
    fun getFilePath(context: Context, uri: Uri): String {
        var filePath: String = ""

        try {
            context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DOCUMENT_ID), null, null, null)?.use {
                if (it.moveToFirst()) {
                    val storageName = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DOCUMENT_ID)).split(":")[0]
                    val docID = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DOCUMENT_ID)).split(":")[1]

                    filePath = if (storageName == "primary") Environment.getExternalStorageDirectory().absolutePath else Environment.getStorageDirectory().absolutePath
                    filePath += File.separator + docID
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }

        return filePath
    }

    fun browse(activity: MainActivity, mode: Int) {
        var intent: Intent? = null

        when (mode) {
            FLAG_READFILE,
            FLAG_SELECTZIP -> {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = if (mode == FLAG_SELECTZIP) "application/zip" else "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            }
            FLAG_WRITEFILE -> {
                intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, MemoViewModel.selectedFileName ?: "newText.txt")
                }
            }
            else -> {}
        }
        if (intent != null) activity.startActivityForResult(intent, mode, null)
    }
}