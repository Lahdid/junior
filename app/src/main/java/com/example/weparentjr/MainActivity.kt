package com.example.weparentjr

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.weparentjr.databinding.ActivityMainBinding
import com.example.weparentjr.utils.BackgroundService
import com.example.weparentjr.views.SettingsFragment
import com.example.weparentjr.views.homefragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;





    fun areLocationPermissionsGranted(context: Context?): Boolean {
        val fineLocationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED

        // Return true if all three permissions are granted
        return fineLocationPermission && coarseLocationPermission && backgroundLocationPermission
    }

    fun areNotificationsPermissionsGranted(context: Context?): Boolean {
        val notificationPermission = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } == PackageManager.PERMISSION_GRANTED

        // Return true if all three permissions are granted
        return notificationPermission
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences?.getBoolean("DARKMODE", false)
     /*
        if (isDarkMode != null) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

       */

        val intent = Intent(this, BackgroundService::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Demarrage mta service lel android eli baad android OREO khater mch kif
            startForegroundService(intent)
        }
        else {
            //Demarrage mta service lel android eli kbal android OREO
            startService(intent)
        }

        val handler = Handler(Looper.getMainLooper())
        handler.removeCallbacksAndMessages(null)
        if (!areLocationPermissionsGranted(this) || !areNotificationsPermissionsGranted(this)) {
            handler.postDelayed({
                showLocationPermissionDialog(this)
            }, 5000)
        }

        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)
        replacefragment(homefragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replacefragment(homefragment())
                R.id.settings -> replacefragment(SettingsFragment())

                else -> {

                }
            }
            true
        }

    }

    private fun replacefragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.framelayout,fragment)
        fragmentTransaction.commit()
    }

    fun showLocationPermissionDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("We parent Jr requires certain permissions")
            .setMessage("This app requires location and notification permissions to function properly. Please go to the settings tab and activate these permissions by following the mentionned steps.")
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()

    }



}