package com.example.myutiltool.ui.memo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.myutiltool.Common
import com.example.myutiltool.iFileInfo

class MemoViewModel(private val _common: Common = Common()) : ViewModel(), iFileInfo {
    // TODO: Implement the ViewModel
    companion object {
        var selectedFile: Uri? = null
        var selectedFileName: String? = null
        var selectedFilePath: String? = null
        var selectedFileExt: String? = null
    }

    override fun setFileData(context: Context, intent: Intent?) {
        MemoViewModel.selectedFile = intent?.data
        MemoViewModel.selectedFileName = _common.getFileName(context, intent?.data)
        MemoViewModel.selectedFilePath = _common.getFilePath(context, intent?.data!!)
        MemoViewModel.selectedFileExt = MemoViewModel.selectedFileName?.substring(MemoViewModel.selectedFileName!!.indexOf("."))
    }
}