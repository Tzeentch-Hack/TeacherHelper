package com.tzeentch.teacherhelper.android

import android.text.style.BulletSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecommendationsTab(
    recommendations: List<String>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF7A8F84)),
        contentAlignment = Alignment.TopStart
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(recommendations) { index, item ->
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = String("\u00E0\u00A6\u00A6\u00E0\u00A7\u0080 $item".toByteArray(Charsets.ISO_8859_1)),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFFC9D1C8)
                )
            }
        }
    }
}