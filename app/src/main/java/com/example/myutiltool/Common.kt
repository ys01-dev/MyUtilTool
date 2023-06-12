package com.example.myutiltool

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toFile
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class Common {
    val FLAG_READFILE = 1
    val FLAG_WRITEFILE = 2
    val FLAG_UNZIP = 4
    val FLAG_SELECTZIP = 8

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
            this.getFileName(context, uri).let {
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
    fun getFileName(context: Context, uri: Uri?): String? {
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
    @SuppressLint("Range")
    fun getFilePath(context: Context, uri: Uri?): String? {
        var filePath: String? = null

        try {
            context.contentResolver.query(uri!!, arrayOf(MediaStore.MediaColumns.DOCUMENT_ID), null, null, null)?.use {
                if (it.moveToFirst()) {
                    filePath = Environment.getExternalStorageDirectory().path.plus("/") + it.getString(it.getColumnIndex(MediaStore.MediaColumns.DOCUMENT_ID)).split(":")[1]
                }
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }

        return filePath
    }

    fun compressToZip(uri: Uri) {
        try {
            ZipOutputStream(FileOutputStream(uri.toFile())).use {

            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    fun unZip(context: Context, uri: Uri) {
        try {
            ZipInputStream(FileInputStream(getFilePath(context, uri))).use { zis ->
                var len = 0
                var buff = ByteArray(1024)

                while(true) {
                    val ze = zis.nextEntry ?: break
                    val unZipFile = File(ze.name)

                    if(unZipFile.isDirectory) {
                        unZipFile.mkdirs()
                    } else {
                        BufferedOutputStream(FileOutputStream(unZipFile)).use { bos ->
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