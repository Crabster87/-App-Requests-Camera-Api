package crabster.rudakov.requestscameraapi

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val permissionManager =
        registerForActivityResult(
            ActivityResultContracts
                .RequestMultiplePermissions()
        ) { permissions ->
            val granted = !permissions.entries.any { !it.value }
            if (granted) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, CaptureFragment()).commit()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        checkPermissions { permissionManager.launch(it) }
    }

    private fun checkPermissions(needPermissions: (permissions: Array<String>) -> Unit): Boolean {
        val permission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        )

        val result = PermissionChecker.checkSelfPermission(
            this,
            permission.toString()
        ) == PermissionChecker.PERMISSION_GRANTED
        if (!result) {
            needPermissions(permission)
        } else {
            onStart()
        }
        return result
    }

}