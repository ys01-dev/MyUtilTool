package com.example.myutiltool.ui.zip

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myutiltool.Common
import com.example.myutiltool.FileInfo
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R
import com.example.myutiltool.ui.PasswordInputDialog
import com.example.myutiltool.ui.StoragePermissionDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import net.lingala.zip4j.ZipFile
import java.io.File
import java.lang.Exception

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

        view.findViewById<Button>(R.id.btn_selectZip)?.setOnClickListener {
            _main.browse(_common.FLAG_SELECTZIP)
        }

        view.findViewById<Button>(R.id.btn_unzip)?.setOnClickListener {
            if(!Environment.isExternalStorageManager()){
                StoragePermissionDialog("storage permission required","Zip tool requires storage permission", Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).show(_main.supportFragmentManager,"main")
                return@setOnClickListener
            }

            if (_common.getFileExt(FileInfo.selectedFileName) == ".zip") {
                try {
                    _common.unZip(_main, FileInfo.selectedFile!!)
                } catch(e: Exception) {
                    Snackbar.make(_main.findViewById(R.id.btn_unzip), e.message.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(_main.findViewById(R.id.btn_unzip),"only zip file is available", BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }
    }
}