package com.example.myutiltool

import android.content.Context
import android.net.Uri

interface iFileInfo {
    fun setFileData(context: Context, uri: Uri?)
}