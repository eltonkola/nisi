package com.eltonkola.nisi.ui.landing


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import com.eltonkola.nisi.R
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TermsOfServiceDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit, // Called when user wants to dismiss (e.g., back press)
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    if (showDialog) {
        val acceptButtonFocusRequester = remember { FocusRequester() }
        val scrollState = rememberScrollState()

        AlertDialog(
            onDismissRequest = onDismissRequest, // Or tie to onDecline if no explicit dismiss outside buttons
            confirmButton = {
                Button(
                    onClick = {
                        onAccept()
                        onDismissRequest() // Also dismiss after action
                    },
                    modifier = Modifier.focusRequester(acceptButtonFocusRequester)
                ) {
                    Text("Accept & Continue")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDecline()
                        onDismissRequest() // Also dismiss after action
                    }
                ) {
                    Text("Decline")
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.nisi_terms_dialog_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .heightIn(max = 300.dp) // Constrain height to make it scrollable
                        .verticalScroll(scrollState)
                ) {
                    Text(
                        text = stringResource(id = R.string.nisi_terms_dialog_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            modifier = Modifier.widthIn(max = 600.dp) // Constrain dialog width
        )

        LaunchedEffect(Unit) {
            acceptButtonFocusRequester.requestFocus()
        }
    }
}
