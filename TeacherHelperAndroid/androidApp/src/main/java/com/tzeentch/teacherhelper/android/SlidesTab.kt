package com.tzeentch.teacherhelper.android

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun SlidesTab(
    imagesUrls: List<String>,
    downloadingUrl: String
) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(downloadingUrl)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF7A8F84)),
        contentAlignment = Alignment.TopStart
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(imagesUrls) { index, item ->
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .border(
                            border = BorderStroke(width = 2.dp, color = Color(0xFF04202C)),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    model = item,
                    contentDescription = stringResource(id = R.string.image_description),
                    contentScale = ContentScale.FillBounds
                )
            }
            item {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    onClick = { context.startActivity(intent) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF304040)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = stringResource(id = R.string.download_pptx_text),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500,
                        color = Color(0xFFC9D1C8)
                    )
                }
            }
        }
    }
}