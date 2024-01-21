package com.example.myutiltool.ui.zip

import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myutiltool.Common
import com.example.myutiltool.MainActivity
import com.example.myutiltool.R
import com.example.myutiltool.ui.RadiobuttonDialog
import com.example.myutiltool.ui.StoragePermissionDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.lingala.zip4j.ZipFile
import java.io.File
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(MainActivity.topMenu != null) {
            MainActivity.topMenu!!.findItem(R.id.menu_action_open)!!.isEnabled = false
            MainActivity.topMenu!!.findItem(R.id.menu_action_overwrite)!!.isEnabled = false
            MainActivity.topMenu!!.findItem(R.id.menu_action_save)!!.isEnabled = false
        }

        view.findViewById<TextView>(R.id.zipName)?.text = ZipViewModel.selectedFilePath

        view.findViewById<Button>(R.id.btn_charCode).setOnClickListener {
            RadiobuttonDialog(_main,"select charCode", arrayOf("MS932", "UTF8")).show(_main.supportFragmentManager,"main")
        }

        view.findViewById<Button>(R.id.btn_selectZip)?.setOnClickListener {
            _common.browse(_main, _common.FLAG_SELECTZIP)
        }

        view.findViewById<Button>(R.id.btn_unzip)?.setOnClickListener {
            if(!Environment.isExternalStorageManager()) {
                StoragePermissionDialog("storage permission is required","Zip tool requires storage permission", Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).show(_main.supportFragmentManager,"main")
                return@setOnClickListener
            }

            var message = ""
            if (ZipViewModel.selectedFileExt == ".zip") {
                view.findViewById<Button>(R.id.btn_unzip).isEnabled = false
                try {
                    ZipFile(File(ZipViewModel.selectedFilePath!!)).use { zip ->
                        if(zip.isEncrypted && view.findViewById<TextInputLayout>(R.id.layout_edit_Password).visibility == TextInputLayout.INVISIBLE) {
                            view.findViewById<TextInputLayout>(R.id.layout_edit_Password).visibility = TextInputLayout.VISIBLE
                            message = "encrypted zip was selected. password is required"
                            return@setOnClickListener
                        }
                    }
                    _viewModel.unZip(view.findViewById<TextInputEditText>(R.id.edit_Password).text.toString(), Charset.forName(view.findViewById<Button>(R.id.btn_charCode).text.toString()))
                    view.findViewById<TextInputEditText>(R.id.edit_Password).setText("")
                    view.findViewById<TextInputLayout>(R.id.layout_edit_Password).visibility = TextInputLayout.INVISIBLE
                    message = "successfully extracted"
                } catch(e: Exception) {
                    message = e.message.toString()
                } finally {
                    view.findViewById<Button>(R.id.btn_unzip).isEnabled = true
                }
            } else {
                message = "only zip file is available"
            }
            Snackbar.make(view.findViewById(R.id.btn_unzip), message, BaseTransientBottomBar.LENGTH_SHORT).show()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        _viewModel = ViewModelProvider(this, ZipViewModel.Factory(_main, _common)).get(ZipViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }
}