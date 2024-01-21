package com.example.myutiltool.ui.zip

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myutiltool.Common
import com.example.myutiltool.MainActivity
import com.example.myutiltool.iFileInfo
import net.lingala.zip4j.io.inputstream.ZipInputStream
import net.lingala.zip4j.model.LocalFileHeader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.charset.Charset

class ZipViewModel(private val _main: MainActivity, private val _common: Common = Common()): ViewModel(), iFileInfo {
    companion object {
        var selectedFile: Uri? = null
        var selectedFileName: String? = null
        var selectedFilePath: String? = null
        var selectedFileExt: String? = null
    }

    class Factory(private val _mainActivity: MainActivity, private val _common: Common): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ZipViewModel(_mainActivity, _common) as T
        }
    }

    override fun setFileData(context: Context, intent: Intent?) {
        ZipViewModel.selectedFile = intent?.data
        ZipViewModel.selectedFileName = _common.getFileName(context, intent?.data)
        ZipViewModel.selectedFilePath = _common.getFilePath(context, intent?.data!!)
        ZipViewModel.selectedFileExt = ZipViewModel.selectedFileName?.substring(ZipViewModel.selectedFileName!!.indexOf("."))
    }

    fun unZip(password: String, charSet: Charset) {
        val targetFile = File(ZipViewModel.selectedFilePath!!)
        val unZipDir = ZipViewModel.selectedFilePath?.substring(0, ZipViewModel.selectedFilePath!!.length - 4)

        File(unZipDir!!).let {
            if(!it.exists()) it.mkdir()
        }

        try {
            ZipInputStream(FileInputStream(targetFile) , password.toCharArray() , charSet).use { zis ->
                var entry: LocalFileHeader?
                var len = 0
                val buff = ByteArray(4096)

                while (run {entry = zis.nextEntry; entry} != null) {
                    val newEntryPath = unZipDir + File.separator + entry!!.fileName

                    if(entry!!.isDirectory) {
                        File(newEntryPath).mkdirs()
                    } else {
                        FileOutputStream(File(newEntryPath)).use { fos ->
                            while (run { len = zis.read(buff); len } != -1) {
                                fos.write(buff, 0, len)
                            }
                        }
                    }
                }
            }
        } catch(e: Exception) {
            throw Exception(e.message)
        }
    }

    /*    private fun unZip2(uri: Uri) {
            val targetPath = _common.getFilePath(_main, uri)
            val targetFile = File(targetPath)
            val unZipDir = targetPath.substring(0, targetPath.length - 4)

            File(unZipDir).let {
                if(!it.exists()) it.mkdir()
            }

            try {
                java.util.zip.ZipFile(targetFile, Charset.forName("MS932")).use { zip ->
                    zip.entries().asSequence().forEach { entry ->
                        val newEntryPath = unZipDir + File.separator + entry.name

                        if(entry.isDirectory) {
                            File(newEntryPath).mkdirs()
                        } else {
                            zip.getInputStream(entry).use { input ->
                                var len = 0
                                var buff = ByteArray(4096)

                                BufferedOutputStream(FileOutputStream(newEntryPath)).use { bos ->
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
        }*/
}