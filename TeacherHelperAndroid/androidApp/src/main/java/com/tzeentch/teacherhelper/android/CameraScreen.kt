package com.tzeentch.teacherhelper.android


import android.content.ContentValues
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.tzeentch.teacherhelper.android.utils.Helper.convertUriToFile
import com.tzeentch.teacherhelper.android.utils.getCameraProvider
import com.tzeentch.teacherhelper.presenters.CameraPresenter
import com.tzeentch.teacherhelper.utils.CameraUiState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun CameraScreen(navController: NavController, presenter: CameraPresenter = koinInject()) {
    when (presenter.cameraState.collectAsState().value) {
        CameraUiState.Initial -> {
            val files by remember {
                mutableStateOf(arrayListOf<ByteArray>())
            }
            val coroutineScope = rememberCoroutineScope()
            val lifecycleOwner = LocalLifecycleOwner.current
            val context = LocalContext.current
            val imageCapture by remember {
                mutableStateOf(ImageCapture.Builder().build())
            }
            val contentValues by remember {
                mutableStateOf(ContentValues())
            }
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NEW_IMAGE")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            val options by remember {
                mutableStateOf(
                    OutputFileOptions.Builder(
                        context.contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ).build()
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AndroidView(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxWidth(),
                        factory = { context ->
                            val previewView = PreviewView(context).apply {
                                this.scaleType = PreviewView.ScaleType.FIT_CENTER
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                            }

                            val previewUseCase = androidx.camera.core.Preview.Builder()
                                .build()
                                .also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                            coroutineScope.launch {
                                val cameraProvider = context.getCameraProvider()
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        previewUseCase,
                                        imageCapture,
                                    )
                                } catch (ex: Exception) {
                                    Log.e("CameraPreview", "Use case binding failed", ex)
                                }
                            }
                            previewView
                        }

                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Button(
                            onClick = { presenter.sendPhoto(files) },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF304040)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.back),
                                color = Color(0xFFC9D1C8),
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp
                            )
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF304040)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.clear),
                                color = Color(0xFFC9D1C8),
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { presenter.sendPhoto(files) },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF304040)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.send),
                                color = Color(0xFFC9D1C8),
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp
                            )
                        }
                        Button(
                            onClick = {
                                imageCapture.takePicture(
                                    options,
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onError(exc: ImageCaptureException) {
                                            Log.e(
                                                "err",
                                                "Photo capture failed: ${exc.message}",
                                                exc
                                            )
                                        }

                                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                            output.savedUri?.let { uri ->
                                                convertUriToFile(
                                                    uri,
                                                    context
                                                )?.readBytes()?.let { files.add(it) }
                                            }
                                        }
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF304040)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.take_a_picture),
                                color = Color(0xFFC9D1C8),
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }

        CameraUiState.GotoOptionalScreen -> {
            navController.popBackStack()
        }

        CameraUiState.Loading -> {
            RotatingProgressBar()
        }
    }

}

@Preview
@Composable
fun CameraScreenPreview() {
    CameraScreen(
        navController = rememberNavController()
    )
}
