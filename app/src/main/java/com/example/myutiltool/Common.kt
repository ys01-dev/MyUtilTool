package com.example.myutiltool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
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

    fun setFileData(context: Context, intent: Intent?) {
        FileInfo.selectedFile = intent?.data
        FileInfo.selectedFileName = this.getFileName(context, intent?.data)
        FileInfo.selectedFilePath = this.getFilePath(context, intent?.data!!)
        FileInfo.selectedFileExt = FileInfo.selectedFileName?.substring(FileInfo.selectedFileName!!.indexOf("."))
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

    fun writeFile(context: Context, str: String?, uri: Uri?) {
        FileInfo.selectedFileName = this.getFileName(context, uri)

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
}