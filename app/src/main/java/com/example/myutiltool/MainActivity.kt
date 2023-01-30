package com.example.myutiltool

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                common.browse(this, common.FLAG_WRITEFILE)
            }
            R.id.menu_action_open -> {
                common.browse(this, common.FLAG_READFILE)
            }
            R.id.menu_action_overwrite -> {
                if(FileInfo.selectedFile != null){
                    try {
                        common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), FileInfo.selectedFile)
                    } catch (e: Exception) {
                        Snackbar.make(findViewById(R.id.EditText1), e.message.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
                    }
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
                    try {
                        common.readFile(this, data?.data)
                    } catch (e: Exception) {
                        Snackbar.make(findViewById(R.id.EditText1), e.message.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
                    }
                }
                common.FLAG_WRITEFILE -> {
                    try {
                        common.writeFile(this, findViewById<EditText>(R.id.EditText1).text.toString(), data?.data)
                    } catch (e: Exception) {
                        Snackbar.make(findViewById(R.id.EditText1), e.message.toString(), BaseTransientBottomBar.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
    }
}
