package com.example.myutiltool.ui.zip

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.myutiltool.Common
import com.example.myutiltool.FileInfo
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class Zip : Fragment() {

    companion object {
        fun newInstance() = Zip()
    }

    private lateinit var viewModel: ZipViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_zip, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ZipViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btn_unzip)?.setOnClickListener {
            MainActivity().browse(Common().FLAG_UNZIP)

            if(Common().getFileExt(FileInfo.selectedFileName) == "zip"){
                Common().unZip(FileInfo.selectedFile!!)
            } else {
                Snackbar.make(requireActivity().findViewById(R.id.EditText1), "you must select a zip file", BaseTransientBottomBar.LENGTH_SHORT).show()
            }
        }
    }

}