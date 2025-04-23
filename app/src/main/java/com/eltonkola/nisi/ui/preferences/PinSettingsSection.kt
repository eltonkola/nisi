package com.eltonkola.nisi.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import iconEye
import iconEyeOff
import lockIcon

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PinSettingsSection() {

    var pin by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Pin", style = MaterialTheme.typography.headlineMedium)

        Text("Top open settings and locked apps, you should set a pin. Particularly useful if you have kids, and want to limit apps like youtube.", style = MaterialTheme.typography.bodyLarge)

        OutlinedTextField(
            value = pin,
            onValueChange = { newValue ->
                if (newValue.length <= 4) { // Example: Limit to 4 digits
                    pin = newValue
                }
            },
            label = { Text("Enter Pin") },
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) iconEye else iconEyeOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            leadingIcon = {
                Icon(imageVector = lockIcon, contentDescription = null)
            }
        )

    }

}
