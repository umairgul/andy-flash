package com.gul.flashlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gul.flashlight.ui.theme.FlashLightTheme

data class FlashlightUiState(
    val hasRequestedPermission: Boolean,
    val isPermissionGranted: Boolean,
    val isPermissionPermanentlyDenied: Boolean,
    val hasTorchCapability: Boolean,
    val isTorchOn: Boolean,
    val statusMessage: String? = null
)

@Composable
fun FlashlightScreen(
    uiState: FlashlightUiState,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = if (uiState.isTorchOn) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF231400),
                Color(0xFF4F3200),
                Color(0xFF0C1019)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF08111F),
                Color(0xFF101B30),
                Color(0xFF17243B)
            )
        )
    }

    val glowBrush = if (uiState.isTorchOn) {
        Brush.radialGradient(
            colors = listOf(
                Color(0x66FFD54F),
                Color.Transparent
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                Color(0x223C7DFF),
                Color.Transparent
            )
        )
    }

    val title = when {
        !uiState.hasTorchCapability -> stringResource(R.string.flashlight_unavailable_title)
        !uiState.isPermissionGranted && uiState.isPermissionPermanentlyDenied -> stringResource(R.string.camera_permission_denied_title)
        !uiState.isPermissionGranted && uiState.hasRequestedPermission -> stringResource(R.string.camera_permission_denied_title)
        !uiState.isPermissionGranted -> stringResource(R.string.camera_permission_title)
        uiState.isTorchOn -> stringResource(R.string.flashlight_on_title)
        else -> stringResource(R.string.flashlight_off_title)
    }

    val subtitle = uiState.statusMessage ?: when {
        !uiState.hasTorchCapability -> stringResource(R.string.flashlight_unavailable_subtitle)
        !uiState.isPermissionGranted && uiState.isPermissionPermanentlyDenied -> stringResource(R.string.camera_permission_blocked_subtitle)
        !uiState.isPermissionGranted && uiState.hasRequestedPermission -> stringResource(R.string.camera_permission_denied_subtitle)
        !uiState.isPermissionGranted -> stringResource(R.string.camera_permission_subtitle)
        uiState.isTorchOn -> stringResource(R.string.flashlight_on_subtitle)
        else -> stringResource(R.string.flashlight_off_subtitle)
    }

    val buttonLabel = when {
        !uiState.hasTorchCapability -> stringResource(R.string.unavailable)
        !uiState.isPermissionGranted && uiState.isPermissionPermanentlyDenied -> stringResource(R.string.open_settings)
        !uiState.isPermissionGranted -> stringResource(R.string.grant_access)
        uiState.isTorchOn -> stringResource(R.string.turn_off)
        else -> stringResource(R.string.turn_on)
    }

    val buttonEnabled = uiState.hasTorchCapability
    val buttonContainerColor = when {
        !buttonEnabled -> Color(0xFF3E4656)
        uiState.isTorchOn -> Color(0xFFFFC107)
        else -> Color(0xFF1E2B43)
    }
    val buttonContentColor = if (uiState.isTorchOn) Color(0xFF332500) else Color.White

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(glowBrush)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.flashlight_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.flashlight_ready_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFFD2D8E2),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = onPrimaryAction,
                enabled = buttonEnabled,
                modifier = Modifier
                    .size(208.dp)
                    .testTag("flashlight_primary_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonContainerColor,
                    contentColor = buttonContentColor,
                    disabledContainerColor = buttonContainerColor,
                    disabledContentColor = Color(0xFFC8CED8)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 14.dp,
                    pressedElevation = 8.dp,
                    disabledElevation = 0.dp
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    FlashlightButtonIcon()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = buttonLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("flashlight_status_card"),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x33243146)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFD6DBE4)
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashlightButtonIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_power_button),
        contentDescription = stringResource(R.string.flashlight_toggle_content_description),
        modifier = Modifier.size(72.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun FlashlightScreenOffPreview() {
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

@Preview(showBackground = true)
@Composable
private fun FlashlightScreenOnPreview() {
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

@Preview(showBackground = true)
@Composable
private fun FlashlightPermissionPreview() {
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




