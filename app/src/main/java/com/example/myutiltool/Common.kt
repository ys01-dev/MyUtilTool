package com.example.myutiltool

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.net.toFile
import kotlinx.coroutines.flow.asFlow
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URI
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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

    fun writeFile(context: Context, str: String?, uri: Uri?) {
        try {
            this.getFilename(context, uri).let {
                if (it != null && it.startsWith("(invalid")) {
                    throw Exception("unnamed file was saved as \"invalid\"")
                }
            }
        } catch(e: Exception) {
            throw Exception(e.message)
        }

        BufferedWriter(OutputStreamWriter(context.contentResolver.openOutputStream(uri!!))).use {
            try {
                it.write(str)
            } catch (e: Exception) {
                throw Exception(e.message)
            }
        }
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

    fun compressToZip(uri: Uri) {
        try {
            ZipOutputStream(FileOutputStream(uri.toFile())).use {

            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    fun unZip(uri: Uri) {
        try {
            ZipInputStream(FileInputStream(uri.toFile())).use {zis ->
                var len = 0
                var buff = ByteArray(1024)

                while(true) {
                    val ze = zis.nextEntry ?: break
                    val unZipFile = File(ze.name)

                    if(unZipFile.isDirectory){
                        unZipFile.mkdirs()
                    } else {
                        BufferedOutputStream(FileOutputStream(unZipFile)).use {bos ->
                            while(run {len = zis.read(buff); len} != -1) {
                                bos.write(buff, 0, len)
                            }
                        }
                    }
                }
            }
        } catch(e: Exception) {
            throw Exception(e.message)
        }
    }
}