package com.example.myutiltool.ui.memo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R

class Memo : Fragment() {
    private lateinit var _viewModel: MemoViewModel
    private lateinit var _main: MainActivity

    companion object {
        fun newInstance() = Memo()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _main = activity as MainActivity
        return inflater.inflate(R.layout.fragment_memo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //if(MainActivity.topMenu != null) MainActivity.topMenu!!.setGroupEnabled(R.id.menu_group, true)
        setHasOptionsMenu(true)
        _main.supportActionBar?.title = if(MemoViewModel.selectedFileName == null) "new text" else MemoViewModel.selectedFileName
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        _viewModel = ViewModelProvider(this).get(MemoViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.setGroupVisible(R.id.menu_group, true)
        super.onCreateOptionsMenu(menu, inflater)
    }
}