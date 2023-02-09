package com.example.myutiltool

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.myutiltool.databinding.ActivityMainBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private val common: Common = Common()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

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
                this.browse(common.FLAG_WRITEFILE)
            }
            R.id.menu_action_open -> {
                this.browse(common.FLAG_READFILE)
            }
            R.id.menu_action_overwrite -> {
                if(FileInfo.selectedFile != null){
                    var str = try {
                        common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), FileInfo.selectedFile)
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
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                common.FLAG_READFILE -> {
                    FileInfo.selectedFile = data?.data
                    FileInfo.selectedFileName = common.getFilename(this, data?.data)
                    var str = try {
                        findViewById<EditText>(R.id.EditText1).setText(common.readFile(this, data?.data))
                        "opened ${FileInfo.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
                common.FLAG_WRITEFILE -> {
                    var str = try {
                        common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), data?.data)
                        "file saved as ${FileInfo.selectedFileName}"
                    } catch (e: Exception) {
                        e.message.toString()
                    }
                    Snackbar.make(findViewById(R.id.EditText1), str, BaseTransientBottomBar.LENGTH_SHORT).show()
                }
                common.FLAG_UNZIP -> {
                    FileInfo.selectedFile = data?.data
                    FileInfo.selectedFileName = common.getFilename(this, data?.data)
                }
                else -> {}
            }
        }
    }

    fun browse(mode: Int) {
        var intent: Intent? = null

        when(mode) {
            common.FLAG_READFILE -> {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            }
            common.FLAG_WRITEFILE -> {
                intent =  Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, FileInfo.selectedFileName ?: "newText.txt")
                }
            }
            common.FLAG_UNZIP -> {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "application/zip"
                    addCategory(Intent.CATEGORY_OPENABLE)
                    //putExtra(Intent.EXTRA_MIME_TYPES, "application/zip")
                }
            }
            else -> {}
        }
        if(intent != null) startActivityForResult(intent, mode, null)
    }
}
