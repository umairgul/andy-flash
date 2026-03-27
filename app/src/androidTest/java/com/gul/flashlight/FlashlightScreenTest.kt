package com.gul.flashlight

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gul.flashlight.ui.theme.FlashLightTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlashlightScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsTurnOnStateWhenTorchIsOff() {
        composeRule.setContent {
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

        composeRule.onNodeWithText("Turn on").assertIsDisplayed()
        composeRule.onNodeWithText("Flashlight is off").assertIsDisplayed()
    }

    @Test
    fun showsTurnOffStateWhenTorchIsOn() {
        composeRule.setContent {
            FlashLightTheme {
                FlashlightScreen(
                    uiState = FlashlightUiState(
                        hasRequestedPermission = false,
                        isPermissionGranted = true,
                        isPermissionPermanentlyDenied = false,
                        hasTorchCapability = true,
                        isTorchOn = true
                    ),
                    onPrimaryAction = {}
                )
            }
        }

        composeRule.onNodeWithText("Turn off").assertIsDisplayed()
        composeRule.onNodeWithText("Flashlight is on").assertIsDisplayed()
    }

    @Test
    fun showsSettingsActionWhenPermissionIsPermanentlyDenied() {
        composeRule.setContent {
            FlashLightTheme {
                FlashlightScreen(
                    uiState = FlashlightUiState(
                        hasRequestedPermission = true,
                        isPermissionGranted = false,
                        isPermissionPermanentlyDenied = true,
                        hasTorchCapability = true,
                        isTorchOn = false
                    ),
                    onPrimaryAction = {}
                )
            }
        }

        composeRule.onNodeWithText("Open settings").assertIsDisplayed()
        composeRule.onNodeWithText("Permission denied").assertIsDisplayed()
    }
}

