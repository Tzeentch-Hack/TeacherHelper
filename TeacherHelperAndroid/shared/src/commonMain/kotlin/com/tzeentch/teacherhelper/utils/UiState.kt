package com.tzeentch.teacherhelper.utils

sealed class AuthUiState {

    object Init : AuthUiState()

    object Loading : AuthUiState()
    
    object ToOptionalScreen : AuthUiState()
}

sealed class MainUiState {
    object Loading : MainUiState()

    data class ReceiveListOfTask(val smt: String) : MainUiState()

    data class Error(val error: String) : MainUiState()
}

sealed class DetailsUiState {
    object Loading : DetailsUiState()

    data class ReceiveTask(val smt: String) : DetailsUiState()

    data class Error(val error: String) : DetailsUiState()
}

