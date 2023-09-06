package com.tzeentch.teacherhelper.android.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tzeentch.teacherhelper.android.NavGraph

@Composable
fun TeacherHelperApp() {
    Scaffold { innerPaddingModifier ->
        Box {
            NavGraph(modifier = Modifier.padding(innerPaddingModifier))
        }
    }
}