package com.example.myutiltool

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.myutiltool.databinding.ActivityMainBinding
import com.example.myutiltool.ui.PermissionDialog
import com.example.myutiltool.ui.SimpleDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _common: Common

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _common = Common()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
        } else {
            //ActivityCompat.shouldShowRequestPermissionRationale(_main, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), _common.REQUEST_CODE_EXSTORAGE)
            this.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        }

        //PermissionDialog("1","2", ).show(supportFragmentManager,"")
        //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), _common.REQUEST_CODE_EXSTORAGE)

        val navController = findNavController(R.id.nav_host_fragment_container)

        NavigationUI.setupActionBarWithNavController(this, navController, findViewById(R.id.drawerLayout))
        _binding.navView.setupWithNavController(navController)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)
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
                    FileInfo.selectedFile = data?.data
                    FileInfo.selectedFileName = _common.getFileName(this, data?.data)
                    var str = try {
                        findViewById<EditText>(R.id.EditText1).setText(_common.readFile(this, data?.data))
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
                    FileInfo.selectedFile = data?.data
                    FileInfo.selectedFileName = _common.getFileName(this, data?.data)
                    findViewById<TextView>(R.id.zipName).text = FileInfo.selectedFileName
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

        when(mode) {
            _common.FLAG_READFILE,
            _common.FLAG_SELECTZIP -> {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = if(mode == _common.FLAG_SELECTZIP) "application/zip" else "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            }
            _common.FLAG_WRITEFILE -> {
                intent =  Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, FileInfo.selectedFileName ?: "newText.txt")
                }
            }
            else -> {}
        }
        if(intent != null) startActivityForResult(intent, mode, null)
    }
}
