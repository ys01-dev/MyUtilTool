package com.example.myutiltool.ui.zip

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myutiltool.Common
import com.example.myutiltool.FileInfo
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R
import com.example.myutiltool.ui.RadiobuttonDialog
import com.example.myutiltool.ui.StoragePermissionDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.io.inputstream.ZipInputStream
import net.lingala.zip4j.model.LocalFileHeader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.charset.Charset

class Zip : Fragment() {
    private lateinit var _common: Common
    private lateinit var _viewModel: ZipViewModel
    private lateinit var _main: MainActivity

    companion object {
        fun newInstance() = Zip()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _common = Common()
        _main = activity as MainActivity
        return inflater.inflate(R.layout.fragment_zip, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _viewModel = ViewModelProvider(this).get(ZipViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(MainActivity.topMenu != null) {
            MainActivity.topMenu!!.findItem(R.id.menu_action_open)!!.isEnabled = false
            MainActivity.topMenu!!.findItem(R.id.menu_action_overwrite)!!.isEnabled = false
            MainActivity.topMenu!!.findItem(R.id.menu_action_save)!!.isEnabled = false
        }

        view.findViewById<TextView>(R.id.zipName).text = FileInfo.selectedFilePath

        view.findViewById<Button>(R.id.btn_charCode).setOnClickListener {
            RadiobuttonDialog(_main,"select charCode", arrayOf("MS932", "UTF8")).show(_main.supportFragmentManager,"main")
        }

        view.findViewById<Button>(R.id.btn_selectZip)?.setOnClickListener {
            _main.browse(_common.FLAG_SELECTZIP)
        }

        view.findViewById<Button>(R.id.btn_unzip)?.setOnClickListener {
            if(!Environment.isExternalStorageManager()){
                StoragePermissionDialog("storage permission is required","Zip tool requires storage permission", Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).show(_main.supportFragmentManager,"main")
                return@setOnClickListener
            }

            if (FileInfo.selectedFileExt == ".zip") {
                try {
                    unZip(FileInfo.selectedFile!!)
                } catch(e: Exception) {
                    Snackbar.make(_main.findViewById(R.id.btn_unzip), e.message.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(_main.findViewById(R.id.btn_unzip),"only zip file is available", BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }
    }

    private fun unZip(uri: Uri) {
        val targetPath = _common.getFilePath(_main, uri)
        val targetFile = File(targetPath)
        val unZipDir = targetPath.substring(0, targetPath.length - 4)

        File(unZipDir).let {
            if(!it.exists()) it.mkdir()
        }

        ZipFile(targetFile).use { zip ->
            if(zip.isEncrypted && _main.findViewById<TextInputLayout>(R.id.layout_edit_Password).visibility == TextInputLayout.INVISIBLE) {
                _main.findViewById<TextInputLayout>(R.id.layout_edit_Password).visibility = TextInputLayout.VISIBLE
                Snackbar.make(_main.findViewById(R.id.btn_unzip),"encrypted zip was selected. password is required", BaseTransientBottomBar.LENGTH_SHORT).show()
                return
            }
        }

        try {
            ZipInputStream(FileInputStream(targetFile)
                           , _main.findViewById<TextInputEditText>(R.id.edit_Password).text.toString().toCharArray()
                           , Charset.forName(_main.findViewById<Button>(R.id.btn_charCode).text.toString())).use { zis ->
                var entry: LocalFileHeader?
                var len = 0
                var buff = ByteArray(4096)

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
                _main.findViewById<TextInputEditText>(R.id.edit_Password).setText("")
                _main.findViewById<TextInputLayout>(R.id.layout_edit_Password).visibility = TextInputLayout.INVISIBLE
                Snackbar.make(_main.findViewById(R.id.btn_unzip),"successfully extracted", BaseTransientBottomBar.LENGTH_SHORT).show()
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