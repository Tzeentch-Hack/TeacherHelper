package com.tzeentch.teacherhelper.android.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tzeentch.teacherhelper.android.CameraScreen
import com.tzeentch.teacherhelper.android.MyApplicationTheme
import com.tzeentch.teacherhelper.android.TeacherHelperApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TeacherHelperApp()
                }
            }
        }
    }
}
