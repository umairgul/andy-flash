package com.gul.flashlight

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import com.gul.flashlight.ui.theme.FlashLightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashLightTheme {
                FlashlightRoute(activity = this)
            }
        }
    }
}

@Composable
private fun FlashlightRoute(activity: ComponentActivity) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val torchController = remember(activity) { TorchController(activity.applicationContext) }
    val hasTorchCapability = remember(torchController) { torchController.isTorchAvailable() }

    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }
    var isPermissionGranted by rememberSaveable { mutableStateOf(activity.hasCameraPermission()) }
    var isTorchOn by rememberSaveable { mutableStateOf(false) }
    var statusMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isPermissionGranted = granted
        statusMessage = null
        if (!granted) {
            isTorchOn = false
        }
    }

    val isPermissionPermanentlyDenied =
        !isPermissionGranted &&
            hasRequestedPermission &&
            !activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
    val currentTorchOn by rememberUpdatedState(isTorchOn)

    fun setTorch(enabled: Boolean) {
        torchController
            .setTorchEnabled(enabled)
            .onSuccess {
                isTorchOn = enabled
                statusMessage = null
            }
            .onFailure {
                isTorchOn = false
                statusMessage = activity.getString(R.string.torch_failed_message)
            }
    }

    LaunchedEffect(isPermissionGranted, hasRequestedPermission) {
        if (!isPermissionGranted && !hasRequestedPermission) {
            hasRequestedPermission = true
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner, torchController) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val grantedNow = activity.hasCameraPermission()
                    isPermissionGranted = grantedNow
                    if (!grantedNow && currentTorchOn) {
                        torchController.setTorchEnabled(false)
                        isTorchOn = false
                    }
                    if (!grantedNow) {
                        statusMessage = null
                    }
                }

                Lifecycle.Event.ON_STOP -> {
                    if (currentTorchOn) {
                        torchController.setTorchEnabled(false)
                        isTorchOn = false
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            if (currentTorchOn) {
                torchController.setTorchEnabled(false)
                isTorchOn = false
            }
        }
    }

    FlashlightScreen(
        uiState = FlashlightUiState(
            hasRequestedPermission = hasRequestedPermission,
            isPermissionGranted = isPermissionGranted,
            isPermissionPermanentlyDenied = isPermissionPermanentlyDenied,
            hasTorchCapability = hasTorchCapability,
            isTorchOn = isTorchOn,
            statusMessage = statusMessage
        ),
        onPrimaryAction = {
            when {
                !hasTorchCapability -> Unit
                !isPermissionGranted && isPermissionPermanentlyDenied -> {
                    statusMessage = null
                    activity.openAppSettings()
                }
                !isPermissionGranted -> {
                    statusMessage = null
                    hasRequestedPermission = true
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }

                isTorchOn -> setTorch(false)
                else -> setTorch(true)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FlashlightRoutePreview() {
    FlashLightTheme {
        FlashlightScreen(
            uiState = FlashlightUiState(
                hasRequestedPermission = false,
                isPermissionGranted = true,
                isPermissionPermanentlyDenied = false,
                hasTorchCapability = true,
                isTorchOn = false
            ),
            onPrimaryAction = {}
        )
    }
}

private fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED
}

private fun Activity.openAppSettings() {
    startActivity(
        Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
    )
}
