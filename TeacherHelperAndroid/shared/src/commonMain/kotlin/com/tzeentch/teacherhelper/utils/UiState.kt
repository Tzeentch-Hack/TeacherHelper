package com.tzeentch.teacherhelper.utils

import com.tzeentch.teacherhelper.dto.DetailsDto
import com.tzeentch.teacherhelper.dto.RequestDto

sealed class AuthUiState {

    object Init : AuthUiState()

    object Loading : AuthUiState()

    object ToOptionalScreen : AuthUiState()
}

sealed class MainUiState {
    object Loading : MainUiState()

    data class ReceiveListOfTask(val requestList: List<RequestDto>) : MainUiState()

    data class Error(val error: String) : MainUiState()
}

sealed class DetailsUiState {
    object Loading : DetailsUiState()

    data class ReceiveTask(val detailsDto: DetailsDto) : DetailsUiState()

    data class Error(val error: String) : DetailsUiState()
}

sealed class CameraUiState {
    object Initial : CameraUiState()

    object GotoOptionalScreen : CameraUiState()

    object Loading : CameraUiState()

}

