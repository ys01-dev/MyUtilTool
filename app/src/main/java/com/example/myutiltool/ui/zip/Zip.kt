package com.example.myutiltool.ui.zip

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myutiltool.R

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
        requireActivity().actionBar?.hide()
        viewModel = ViewModelProvider(this).get(ZipViewModel::class.java)
        // TODO: Use the ViewModel
    }

}