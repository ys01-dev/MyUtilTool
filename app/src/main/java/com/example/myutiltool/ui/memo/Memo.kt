package com.example.myutiltool.ui.memo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myutiltool.Common
import com.example.myutiltool.FileInfo
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R

class Memo : Fragment() {
    private lateinit var viewModel: MemoViewModel
    private lateinit var _main: MainActivity

    companion object {
        fun newInstance() = Memo()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _main = activity as MainActivity
        return inflater.inflate(R.layout.fragment_memo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MemoViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(MainActivity.topMenu != null) {
            MainActivity.topMenu!!.findItem(R.id.menu_action_open)!!.isEnabled = true
            MainActivity.topMenu!!.findItem(R.id.menu_action_overwrite)!!.isEnabled = true
            MainActivity.topMenu!!.findItem(R.id.menu_action_save)!!.isEnabled = true
        }
        _main.supportActionBar?.title = if(FileInfo.selectedFileName == null) "new text" else FileInfo.selectedFileName
    }
}