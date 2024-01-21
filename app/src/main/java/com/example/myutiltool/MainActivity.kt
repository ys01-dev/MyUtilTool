package com.example.myutiltool

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.myutiltool.databinding.ActivityMainBinding
import com.example.myutiltool.ui.StoragePermissionDialog
import com.example.myutiltool.ui.memo.MemoViewModel
import com.example.myutiltool.ui.zip.ZipViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _common: Common
    private lateinit var _vmZip: ZipViewModel
    private lateinit var _vmMemo: MemoViewModel

    companion object {
        var topMenu: Menu? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _common = Common()
        _vmZip = ZipViewModel(this, _common)
        _vmMemo = MemoViewModel(_common)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        val navController = findNavController(R.id.nav_host_fragment_container)
        NavigationUI.setupActionBarWithNavController(this, navController, findViewById(R.id.drawerLayout))
        _binding.navView.setupWithNavController(navController)

        if(!Environment.isExternalStorageManager()) {
            StoragePermissionDialog("storage permission required","Zip tool requires storage permission", Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).show(supportFragmentManager,"main")
        } else {
            if(intent?.data != null) {
                _vmZip.setFileData(this, intent)

                if(ZipViewModel.selectedFileExt == ".zip") {
                    navController.navigate(R.id.zip)
                } else {
                    val message = try {
                        findViewById<EditText>(R.id.EditText1).setText(_common.readFile(this, intent?.data))
                        this.supportActionBar?.title = ZipViewModel.selectedFileName
                        "opened ${ZipViewModel.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), message, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
            }
        }
        //ActivityCompat.shouldShowRequestPermissionRationale(_main, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), _common.REQUEST_CODE_EXSTORAGE)
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), _common.REQUEST_CODE_EXSTORAGE)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)
        MainActivity.topMenu = menu
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_container)
        return NavigationUI.navigateUp(navController, findViewById(R.id.drawerLayout))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_action_save -> {
                _common.browse(this, _common.FLAG_WRITEFILE)
            }
            R.id.menu_action_open -> {
                _common.browse(this, _common.FLAG_READFILE)
            }
            R.id.menu_action_overwrite -> {
                var message = ""
                if(MemoViewModel.selectedFile != null) {
                    message = try {
                        MemoViewModel.selectedFileName = _common.getFileName(this, MemoViewModel.selectedFile)
                        _common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), MemoViewModel.selectedFile)
                        "overwritten ${MemoViewModel.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                } else {
                    message = "any file hasn't been opened yet"
                }
                Snackbar.make(findViewById(R.id.EditText1), message, BaseTransientBottomBar.LENGTH_SHORT).show()
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                _common.FLAG_READFILE -> {
                    _vmMemo.setFileData(this, data)
                    val str = try {
                        findViewById<EditText>(R.id.EditText1).setText(_common.readFile(this, data?.data))
                        this.supportActionBar?.title = MemoViewModel.selectedFileName
                        "opened ${MemoViewModel.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
                _common.FLAG_WRITEFILE -> {
                    val str = try {
                        _common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), data?.data)
                        this.supportActionBar?.title = MemoViewModel.selectedFileName
                        "file saved as ${MemoViewModel.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
                _common.FLAG_SELECTZIP -> {
                    _vmZip.setFileData(this, data)
                    findViewById<TextView>(R.id.zipName).text = _common.getFilePath(this, data?.data!!)
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        when(requestCode) {
//            _common.REQUEST_CODE_EXSTORAGE -> {
//                if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
//
//                } else {
//                    Snackbar.make(findViewById(R.id.EditText1), "permission of ex-storage didn't granted", BaseTransientBottomBar.LENGTH_SHORT).show()
//                }
//            }
//            else -> {}
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
}
