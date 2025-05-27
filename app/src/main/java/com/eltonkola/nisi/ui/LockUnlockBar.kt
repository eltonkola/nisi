package com.eltonkola.nisi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.repository.LockState
import com.eltonkola.nisi.data.repository.UnlockManager
import com.eltonkola.nisi.data.repository.formatSecondsToMinSec
import dagger.hilt.android.lifecycle.HiltViewModel
import iconLock
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@Composable
fun LockUnlockBar(
    modifier: Modifier = Modifier,
    viewModel: LockUnlockBarViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    if(uiState.locked !=null) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {

            Row(
                modifier = Modifier.padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = iconLock,
                    contentDescription = "Lock",
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = if (uiState.locked == true) "Locked" else "Unlocked for ${uiState.timerSecondsRemaining.formatSecondsToMinSec()}",
                )
            }

        }
    }

    PinEntryDialog(
      //  title = "PIN IS ${uiState.pin}",
        showDialog = uiState.showUnlockScreen,
        onDismissRequest = { viewModel.hideUnlockScreen() },
        onPinEntered = { enteredPin ->
            val isCorrect = viewModel.checkPin(enteredPin)
            if (isCorrect) {
                viewModel.unlock()
                viewModel.hideUnlockScreen()
            }
            isCorrect
        }
    )


}



@HiltViewModel
class LockUnlockBarViewModel @Inject constructor(
    val unlockManager: UnlockManager
) : ViewModel(){

    val uiState: MutableStateFlow<LockState> = unlockManager.uiState

    fun unlock() {
        unlockManager.unlock()
    }

    fun lockManually() {
        unlockManager.lockManually()
    }

    fun checkPin(pin: String): Boolean {
        return unlockManager.checkPin(pin)
    }

    fun hideUnlockScreen() {
        unlockManager.hideUnlockScreen()
    }

}