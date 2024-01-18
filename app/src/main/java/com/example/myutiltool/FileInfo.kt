package com.example.myutiltool

import android.net.Uri

class FileInfo {
    companion object {
        var selectedFile: Uri? = null
        var selectedFileName: String? = null
        var selectedFilePath: String? = null
        var selectedFileExt: String? = null
    }
}