package com.eltonkola.nisi.ui.landing


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onLetsGetStartedClicked: () -> Unit,
    onExit : () -> Unit
) {
    var showTermsDialog by remember { mutableStateOf(false) }

    val letsGetStartedButtonFocusRequester = remember { FocusRequester() }


        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "NISI Launcher Logo",
                    modifier = Modifier.size(100.dp).padding(bottom = 24.dp)
                )
                Text(
                    text = "Welcome to NISI Launcher",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Let's get you started, there are a couple of things we could set up for a better experience. Start by reading our terms of service, and let's start when you are ready.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )




                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {


                    Button( // Standard Button, or you can use tv.material3.Button if you prefer its specific styling
                        onClick = { showTermsDialog = true },
                        modifier = Modifier
                        // .weight(0.2f)
                    ) {
                        Text("Terms of service")
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Button(
                        onClick = onLetsGetStartedClicked,
                        modifier = Modifier
                            //    .weight(0.2f)
                            .focusRequester(letsGetStartedButtonFocusRequester)
                    ) {
                        Text("Let's get started")
                    }

                }




        }

        TermsOfServiceDialog(
            showDialog = showTermsDialog,
            onDismissRequest = { showTermsDialog = false },
            onAccept = {
                showTermsDialog = false
                letsGetStartedButtonFocusRequester.requestFocus()
            },
            onDecline = {
                showTermsDialog = false
                onExit()
            }
        )
    }

    LaunchedEffect(Unit) {
            letsGetStartedButtonFocusRequester.requestFocus()
    }
}



