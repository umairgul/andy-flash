package com.gul.flashlight

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

class TorchController(context: Context) {
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private val torchCameraId: String? by lazy {
        cameraManager?.cameraIdList?.firstOrNull { cameraId ->
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            val hasFlashUnit = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING)
            hasFlashUnit && lensFacing != CameraCharacteristics.LENS_FACING_FRONT
        } ?: cameraManager?.cameraIdList?.firstOrNull { cameraId ->
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    fun isTorchAvailable(): Boolean = torchCameraId != null

    fun setTorchEnabled(enabled: Boolean): Result<Unit> {
        val cameraId = torchCameraId
            ?: return Result.failure(IllegalStateException("No flash-capable camera found."))

        return runCatching {
            cameraManager?.setTorchMode(cameraId, enabled)
                ?: error("CameraManager is unavailable.")
        }.recoverCatching { throwable ->
            when (throwable) {
                is CameraAccessException -> throw IllegalStateException("Camera access failed.", throwable)
                is SecurityException -> throw IllegalStateException("Camera permission denied.", throwable)
                else -> throw throwable
            }
        }
    }
}

