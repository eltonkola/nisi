package com.eltonkola.nisi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eltonkola.nisi.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreen() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        Image(
            painter = painterResource(id = R.drawable.offline_wallpaper_0),
            contentDescription = "NISI Launcher Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

//        CircularProgressIndicator(
//            modifier = Modifier.size(48.dp)
//        )

        LoadingIndicator(
//            color = Color.Blue,
//            polygons = listOf(
//                 MaterialShapes.SoftBurst,
//                 MaterialShapes.Cookie9Sided,
//                 MaterialShapes.Pentagon,
//                MaterialShapes.Pill,
//                 MaterialShapes.Sunny,
//                 MaterialShapes.Cookie4Sided,
//                MaterialShapes.Oval
//            )
        )


    }

}