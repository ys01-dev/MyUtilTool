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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _common: Common

    companion object {
        var topMenu: Menu? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _common = Common()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        val navController = findNavController(R.id.nav_host_fragment_container)

        NavigationUI.setupActionBarWithNavController(this, navController, findViewById(R.id.drawerLayout))
        _binding.navView.setupWithNavController(navController)

        if(!Environment.isExternalStorageManager()){
            StoragePermissionDialog("storage permission required","Zip tool requires storage permission", Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).show(supportFragmentManager,"main")
        } else {
            if(intent?.data != null) {
                _common.setFileData(this, intent)

                if(FileInfo.selectedFileExt == ".zip") {
                    navController.navigate(R.id.zip)
                } else {
                    val str = try {
                        findViewById<EditText>(R.id.EditText1).setText(_common.readFile(this, intent?.data))
                        this.supportActionBar?.title = FileInfo.selectedFileName
                        "opened ${FileInfo.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
            }
        }
        supportActionBar?.title = if(FileInfo.selectedFileName == null) "new text" else FileInfo.selectedFileName
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
                this.browse(_common.FLAG_WRITEFILE)
            }
            R.id.menu_action_open -> {
                this.browse(_common.FLAG_READFILE)
            }
            R.id.menu_action_overwrite -> {
                if(FileInfo.selectedFile != null){
                    var str = try {
                        _common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), FileInfo.selectedFile)
                        "overwritten ${FileInfo.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(findViewById(R.id.EditText1), "any file hasn't been opened yet", BaseTransientBottomBar.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                _common.FLAG_READFILE -> {
                    _common.setFileData(this, data)
                    var str = try {
                        findViewById<EditText>(R.id.EditText1).setText(_common.readFile(this, data?.data))
                        this.supportActionBar?.title = FileInfo.selectedFileName
                        "opened ${FileInfo.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
                _common.FLAG_WRITEFILE -> {
                    var str = try {
                        _common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), data?.data)
                        "file saved as ${FileInfo.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
                _common.FLAG_SELECTZIP -> {
                    _common.setFileData(this, data)
                    findViewById<TextView>(R.id.zipName).text = _common.getFilePath(this, data?.data!!)
                }

                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            _common.REQUEST_CODE_EXSTORAGE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {

                } else {
                    Snackbar.make(findViewById(R.id.EditText1), "permission of ex-storage didn't granted", BaseTransientBottomBar.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }
    }

    fun browse(mode: Int) {
        var intent: Intent? = null

        when (mode) {
            _common.FLAG_READFILE,
            _common.FLAG_SELECTZIP -> {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = if (mode == _common.FLAG_SELECTZIP) "application/zip" else "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            }
            _common.FLAG_WRITEFILE -> {
                intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, FileInfo.selectedFileName ?: "newText.txt")
                }
            }
            else -> {}
        }
        if (intent != null) startActivityForResult(intent, mode, null)
    }
}
