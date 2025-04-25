package com.eltonkola.nisi.ui.launcher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import com.eltonkola.nisi.data.model.App
import com.eltonkola.nisi.data.model.AppSettingItem


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppItemUi(
    app: AppSettingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(120.dp)
            .aspectRatio(CardDefaults.HorizontalImageAspectRatio)

        ,
        border =
            CardDefaults.border(
                focusedBorder =
                    Border(
                        border = BorderStroke(width = 2.dp, color = Color.White),
                        shape = RoundedCornerShape(8),
                    ),
            ),
        colors =
            CardDefaults.colors(containerColor = Color.Gray, focusedContainerColor = Color.DarkGray),
        scale =
            CardDefaults.scale(
                focusedScale = 1.1f,
            )
    ) {

        if (app.icon != null) {
            Image(
                bitmap = app.icon,
                contentDescription = app.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = app.name,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

    }
}
