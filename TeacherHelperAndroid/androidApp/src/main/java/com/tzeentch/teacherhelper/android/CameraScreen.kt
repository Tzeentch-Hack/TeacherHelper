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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.googlecode.tesseract.android.TessBaseAPI
import com.tzeentch.teacherhelper.android.utils.Assets
import com.tzeentch.teacherhelper.android.utils.Helper.convertUriToBitmap
import com.tzeentch.teacherhelper.android.utils.getCameraProvider
import com.tzeentch.teacherhelper.presenters.CameraPresenter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject


@Composable
fun CameraScreen(navController: NavController, presenter: CameraPresenter = koinInject()) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val imageCapture by remember {
        mutableStateOf(ImageCapture.Builder().build())
    }
    val textToUpload = remember {
        mutableStateOf("")
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
        Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter),   horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
            Column {
                Button(onClick = {
                    imageCapture.takePicture(
                        options,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                Log.e("err", "Photo capture failed: ${exc.message}", exc)
                            }

                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                Assets.extractAssets(context)
                                val dataPath: String = Assets.getTessDataPath(context)
                                val tess = TessBaseAPI()
                                if (!tess.init(
                                        dataPath,
                                        "eng"
                                    )
                                ) {
                                    tess.recycle()
                                    return
                                }
                                tess.setImage(output.savedUri?.let {
                                    convertUriToBitmap(
                                        it,
                                        context
                                    )
                                })
                                textToUpload.value += tess.utF8Text
                                Log.d("rec", textToUpload.value)
                                tess.recycle()
                            }
                        })
                })
                {
                    Text("Take Photo")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Send")
                }
            }
            Button(onClick = { }) {
                Text(text = "clear")
            }
        }
    }
}

