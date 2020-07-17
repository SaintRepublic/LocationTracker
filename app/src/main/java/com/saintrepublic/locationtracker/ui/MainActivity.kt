package com.saintrepublic.locationtracker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.saintrepublic.locationtracker.R
import com.saintrepublic.locationtracker.databinding.ActivityMainBinding
import com.saintrepublic.locationtracker.vm.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(), Toolbar.OnMenuItemClickListener {

    companion object {
        const val REQUEST_PERMISSION = 1
    }

    private val viewModel by viewModel<MainActivityViewModel>()

    override fun createBindings(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
            vm = viewModel

            toolbar.inflateMenu(R.menu.history_menu)
            toolbar.setOnMenuItemClickListener(this@MainActivity)

            startButton.setOnClickListener {
                if (hasPermission()) {
                    startTracking()
                } else
                    requestPermission()
            }
            viewModel.providerDisabledEvent.observe(this@MainActivity) {
                it.let {
                    Toast.makeText(this@MainActivity,
                        R.string.location_disabled_error, Toast.LENGTH_LONG).show()
                }
            }
            viewModel.isProcessing.observe(this@MainActivity) {
                viewModel.enableSensorsListener(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isProcessing.value == true)
            viewModel.enableSensorsListener(true)
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isProcessing.value != true)
            viewModel.enableSensorsListener(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.databaseService.close()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_history) {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (hasPermission())
                startTracking()
        }
    }

    private fun startTracking() {
        if (viewModel.isProviderEnabled())
            viewModel.startTracking()
        else
            showEnableLocationDialog()
    }

    private fun hasPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)
    }

    private fun requestPermission() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showLocationPermissionRationalDialog()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission),
                REQUEST_PERMISSION
            )
        }
    }

    private fun showLocationPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.permission_request_dialog_text)
            .setPositiveButton(getString(R.string.ok), null)
            .setNegativeButton(getString(R.string.settings)) { _, _ ->
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .create()
            .show()
    }

    private fun showEnableLocationDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.enable_location_dialog_text)
            .setPositiveButton(getString(R.string.enable)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
    }

}