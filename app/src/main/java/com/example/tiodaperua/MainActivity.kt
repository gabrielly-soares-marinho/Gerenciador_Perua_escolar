package com.example.tiodaperua

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tiodaperua.databinding.ActivityMainBinding
import com.example.tiodaperua.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        val drawerLayout: DrawerLayout? = binding.drawerLayout

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_transform, R.id.nav_reflow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView?.setupWithNavController(navController)

        updateNavHeader()

        binding.navView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    logout()
                    true
                }
                else -> {
                    // Deixa o comportamento padrão para os outros itens do menu
                    navController.navigate(menuItem.itemId)
                    drawerLayout?.closeDrawers()
                    true
                }
            }
        }
    }

    private fun updateNavHeader() {
        val headerView = binding.navView?.getHeaderView(0)
        val userNameTextView = headerView?.findViewById<TextView>(R.id.nav_header_user_name)
        val userEmailTextView = headerView?.findViewById<TextView>(R.id.nav_header_user_email)

        val userName = intent.getStringExtra("USER_NAME")
        val userEmail = intent.getStringExtra("USER_EMAIL")

        userNameTextView?.text = userName
        userEmailTextView?.text = userEmail
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}