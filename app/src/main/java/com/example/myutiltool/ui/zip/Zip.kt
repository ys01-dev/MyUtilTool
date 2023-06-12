package com.example.myutiltool.ui.zip

import android.os.Bundle
import android.view.LayoutInflater
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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

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
            if (_common.getFileExt(FileInfo.selectedFileName) == ".zip") {
                _common.unZip(_main, FileInfo.selectedFile!!)
            } else {
                Snackbar.make(_main.findViewById(R.id.btn_unzip),"you must select a zip file", BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }
    }

}