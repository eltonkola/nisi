package com.eltonkola.nisi.ui


// import androidx.compose.foundation.clickable // Not directly used on Surface anymore, onClick is
// import androidx.compose.material.icons.filled.Lock // Will be loaded via painterResource
// import androidx.compose.ui.graphics.Color // Will use MaterialTheme colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.eltonkola.nisi.R
import com.eltonkola.nisi.ui.theme.NisiTheme
import kotlinx.coroutines.delay

const val DEFAULT_PIN_LENGTH = 4 // Standardized constant name

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PinEntryDialog( // Renamed and signature adjusted
    showDialog: Boolean,
    pinLength: Int = DEFAULT_PIN_LENGTH,
    title: String = "Enter PIN", // This will be used as the left pane title
    onDismissRequest: () -> Unit,
    onPinEntered: (String) -> Boolean, // Returns true if PIN is correct, false otherwise
) {
    if (!showDialog) return

    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val numpadFocusRequesters = remember { List(12) { FocusRequester() } }
    val dialogRootFocusRequester = remember { FocusRequester() }

    // Default values for description and icon, as they are not in the new signature
    val defaultLeftPaneDescription = stringResource(R.string.pin_dialog_default_description)
    val defaultLeftPaneIconPainter: Painter = painterResource(id = R.drawable.tv_banner) // Use your default lock icon

    fun submitPin() {
        if (enteredPin.length == pinLength) {
            val isCorrect = onPinEntered(enteredPin)
            if (isCorrect) {
                // Caller handles dismissal or next actions
            } else {
                errorMessage = "Incorrect PIN. Try again."
                enteredPin = "" // Clear PIN on error
                numpadFocusRequesters.firstOrNull()?.requestFocus()
            }
        }
    }

    Dialog(
        onDismissRequest = {
            enteredPin = ""
            errorMessage = null
            onDismissRequest()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(dialogRootFocusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyUp) {
                        when (keyEvent.key) {
                            in listOf(Key.Zero, Key.One, Key.Two, Key.Three, Key.Four, Key.Five, Key.Six, Key.Seven, Key.Eight, Key.Nine) -> {
                                if (enteredPin.length < pinLength) {
                                    enteredPin += (keyEvent.key.nativeKeyCode - Key.Zero.nativeKeyCode).toString()
                                    errorMessage = null
                                    if (enteredPin.length == pinLength) submitPin()
                                }
                                return@onKeyEvent true
                            }
                            Key.Backspace, Key.Delete -> {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                    errorMessage = null
                                }
                                return@onKeyEvent true
                            }
                            Key.Enter, Key.NumPadEnter -> {
                                if (enteredPin.length == pinLength) submitPin()
                                else if (enteredPin.isEmpty() && errorMessage != null) {
                                    errorMessage = null
                                    numpadFocusRequesters.firstOrNull()?.requestFocus()
                                }
                                return@onKeyEvent true
                            }
                        }
                    }
                    false
                },
           // colors = MaterialTheme.colorScheme.background.copy(alpha = 0.97f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 32.dp)
            ) {
                // Left Pane: Icon and Text
                Column(
                    modifier = Modifier
                        .weight(0.40f)
                        .fillMaxHeight()
                        .padding(end = 32.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = defaultLeftPaneIconPainter,
                        contentDescription = "Security Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 24.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = title, // Use the 'title' parameter here
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = defaultLeftPaneDescription, // Use the default description
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Right Pane: PIN Entry and Numpad
                Column(
                    modifier = Modifier
                        .weight(0.60f)
                        .fillMaxHeight()
                        .padding(start = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        repeat(pinLength) { index ->
                            PinDot(
                                isFilled = index < enteredPin.length,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    val errorTextHeight = MaterialTheme.typography.bodyMedium.lineHeight.value.dp + 16.dp
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.height(errorTextHeight).padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Spacer(modifier = Modifier.height(errorTextHeight))
                    }

                    NumpadGrid(
                        pinLength = pinLength,
                        enteredPin = enteredPin,
                        focusRequesters = numpadFocusRequesters,
                        onNumpadClick = { key ->
                            errorMessage = null
                            if (key == "BACKSPACE") {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                }
                            } else if (enteredPin.length < pinLength) {
                                enteredPin += key
                                if (enteredPin.length == pinLength) {
                                    submitPin()
                                }
                            }
                        }
                    )

                    OutlinedButton(
                        onClick = {
                            enteredPin = ""
                            errorMessage = null
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(top = 24.dp),
                      //  shape = ButtonDefaults.outlinedButtonShape(RoundedCornerShape(8.dp))
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    LaunchedEffect(showDialog, numpadFocusRequesters) {
        if (showDialog) {
            delay(100)
            dialogRootFocusRequester.requestFocus()
            delay(50)
            numpadFocusRequesters.firstOrNull()?.requestFocus()
        }
    }
}

// NumpadGrid, PinDot, NumpadButton, NumpadButtonDefaults remain the same as in the previous two-pane version.
// Make sure they are included in this file or accessible. I'll include them here for completeness.

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun NumpadGrid(
    pinLength: Int,
    enteredPin: String,
    focusRequesters: List<FocusRequester>,
    onNumpadClick: (String) -> Unit
) {
    val numpadKeys = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        " ", "0", "BACKSPACE"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        numpadKeys.chunked(3).forEachIndexed { rowIndex, rowKeys ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowKeys.forEachIndexed { colIndex, key ->
                    val flatIndex = rowIndex * 3 + colIndex
                    if (key == " ") {
                        Spacer(Modifier.size(42.dp))
                    } else {
                        NumpadButton(
                            text = if (key == "BACKSPACE") "" else key,
                            icon = if (key == "BACKSPACE") Icons.Filled.Delete else null,
                            contentDescription = when (key) {
                                "BACKSPACE" -> "Delete last digit"
                                else -> "Enter digit $key"
                            },
                            onClick = { onNumpadClick(key) },
                            modifier = Modifier
                                .focusRequester(focusRequesters[flatIndex])
                                .focusProperties {
                                    val upIndex = flatIndex - 3
                                    up = if (upIndex >= 0) focusRequesters.getOrNull(upIndex) ?: FocusRequester.Cancel else FocusRequester.Cancel

                                    val downIndex = flatIndex + 3
                                    down = if (downIndex < focusRequesters.size && numpadKeys.getOrNull(downIndex) != " ") focusRequesters.getOrNull(downIndex) ?: FocusRequester.Cancel else FocusRequester.Cancel

                                    val leftIndex = flatIndex - 1
                                    left = if (colIndex > 0 && numpadKeys.getOrNull(leftIndex) != " ") focusRequesters.getOrNull(leftIndex) ?: FocusRequester.Cancel else FocusRequester.Cancel

                                    val rightIndex = flatIndex + 1
                                    right = if (colIndex < 2 && rightIndex < focusRequesters.size && numpadKeys.getOrNull(rightIndex) != " ") focusRequesters.getOrNull(rightIndex) ?: FocusRequester.Cancel else FocusRequester.Cancel
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinDot(isFilled: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = CircleShape
            )
            .border(
                width = 1.5.dp,
                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                shape = CircleShape
            )
    )
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NumpadButton(
    text: String,
    icon: ImageVector? = null,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Button( // Using androidx.tv.material3.Button
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(42.dp),
        shape = ButtonDefaults.shape(shape =  RoundedCornerShape(12.dp)), // Use ButtonDefaults.shape
        colors = ButtonDefaults.colors( // Standard Button colors, customize if needed
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            focusedContentColor = MaterialTheme.colorScheme.inverseOnSurface
            // You can customize pressed and disabled colors here too
        ),
        // scale = ButtonDefaults.scale(), // Button handles its own scale
        // border = ButtonDefaults.border(), // Button handles its own border or lack thereof by default
        interactionSource = interactionSource,
        contentPadding = PaddingValues(0.dp) // Remove default button padding if icon/text needs to fill
    ) {
        // Content of the button
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription, // Content description for icon itself
                modifier = Modifier.size(32.dp)
                // Tint is usually handled by Button's contentColor
            )
        } else {
            Text(
                text = text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
                // Color is handled by Button's contentColor
            )
        }
    }
}

// --- Add a placeholder lock icon to your drawables (e.g., res/drawable/ic_lock_tv.xml) ---
// (Same as before)
/*
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M18,8h-1V6c0,-2.76 -2.24,-5 -5,-5S7,3.24 7,6v2H6c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2V10C20,9.1 19.1,8 18,8zM9,6c0,-1.66 1.34,-3 3,-3s3,1.34 3,3v2H9V6zM18,20H6V10h12V20z"/>
  <path
      android:fillColor="@android:color/white"
      android:pathData="M12,17c1.1,0 2,-0.9 2,-2s-0.9,-2 -2,-2 -2,0.9 -2,2S10.9,17 12,17z"/>
</vector>
*/

// --- Add a default description string to res/values/strings.xml ---
/*
<resources>
    // ... other strings
    <string name="pin_dialog_default_description">Please enter your PIN to continue.</string>
</resources>
*/


// --- Preview ---
@OptIn(ExperimentalTvMaterial3Api::class)
@Preview(device = Devices.TV_1080p, showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun PinEntryDialogPreview() { // Renamed preview function
    var showDialog by remember { mutableStateOf(true) }
    val correctPin = "1234"

    NisiTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { showDialog = true }) {
                Text("Show PIN Dialog")
            }

            if (showDialog) {
                PinEntryDialog( // Calling the renamed dialog
                    showDialog = true,
                    title = "Unlock Secure Area", // Using the 'title' parameter
                    // pinLength = 6, // Example of overriding default pinLength
                    onDismissRequest = { showDialog = false },
                    onPinEntered = { pin ->
                        println("Preview PIN Entered: $pin")
                        if (pin == correctPin) {
                            println("Preview PIN Correct!")
                            showDialog = false
                            true
                        } else {
                            println("Preview PIN Incorrect!")
                            false
                        }
                    }
                )
            }
        }
    }
}
