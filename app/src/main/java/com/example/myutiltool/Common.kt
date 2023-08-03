package com.example.myutiltool

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.Path

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
        FileInfo.selectedFileName = this.getFileName(context, uri)

        try {
            BufferedWriter(OutputStreamWriter(context.contentResolver.openOutputStream(uri!!))).use {
                it.write(str)
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    fun getFileExt(fileName: String?): String? {
        return fileName?.substring(fileName.indexOf("."))
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

    fun compressToZip(uri: Uri) {
        try {
            ZipOutputStream(FileOutputStream(uri.toFile())).use {

            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    fun unZip(context: Context, uri: Uri) {
        val targetPath = getFilePath(context, uri)
        val targetFile = File(targetPath)
        val unZipDir = targetPath.substring(0, targetPath.length - 4)

        File(unZipDir).let {
            if(!it.exists()) it.mkdir()
        }

        try {
            ZipFile(targetFile, Charset.forName("MS932")).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val entryPath = unZipDir + File.separator + entry.name

                    if(entry.isDirectory) {
                        File(entryPath).mkdirs()
                    } else {
                        zip.getInputStream(entry).use { input ->
                            var len = 0
                            var buff = ByteArray(4096)

                            BufferedOutputStream(FileOutputStream(entryPath)).use { bos ->
                                while(run {len = input.read(buff); len} != -1) {
                                    bos.write(buff, 0, len)
                                }
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