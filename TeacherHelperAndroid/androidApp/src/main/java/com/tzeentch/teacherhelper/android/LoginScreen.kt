package com.tzeentch.teacherhelper.android

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tzeentch.teacherhelper.presenters.AuthPresenter
import com.tzeentch.teacherhelper.utils.AuthUiState
import org.koin.compose.koinInject

@Composable
fun LoginScreen(
    navController: NavController,
    presenter: AuthPresenter = koinInject()
) {
    var iPValue by rememberSaveable { mutableStateOf("") }
    var emailValue by rememberSaveable { mutableStateOf("") }
    var passwordValue by rememberSaveable { mutableStateOf("") }
    var confirmPassValue by rememberSaveable { mutableStateOf("") }
    var isLogin by rememberSaveable { mutableStateOf(true) }
    when (presenter.authState.collectAsState().value) {
        AuthUiState.Init -> {
            LoginScreen(
                iPValue = iPValue,
                emailValue = emailValue,
                passwordValue = passwordValue,
                confirmPassValue = confirmPassValue,
                isLogin = isLogin,
                onIPValueChange = { newIPValue ->
                    iPValue = newIPValue
                },
                onEmailValueChange = { newEmail ->
                    emailValue = newEmail
                },
                onPasswordValueChange = { newPassword ->
                    passwordValue = newPassword
                },
                onConfirmationPasswordValueChange = { newConfirmPassword ->
                    confirmPassValue = newConfirmPassword
                },
                onLoginClick = {
                    presenter.loginUser(ip = iPValue, name = emailValue, password = passwordValue)
                },
                onRegistrationClick = {
                    if (isLogin) {
                        isLogin = false
                    } else {
                        presenter.registerUser(
                            ip = iPValue,
                            name = emailValue,
                            password = passwordValue
                        )
                    }
                }
            )
        }

        AuthUiState.ToOptionalScreen -> {
            navController.navigate(MainSection.OPTION_ROUTE)
        }

        else -> {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun LoginScreen(
    iPValue: String,
    emailValue: String,
    passwordValue: String,
    confirmPassValue: String,
    isLogin: Boolean,
    onIPValueChange: (newIPValue: String) -> Unit,
    onEmailValueChange: (newEmail: String) -> Unit,
    onPasswordValueChange: (newPassword: String) -> Unit,
    onConfirmationPasswordValueChange: (newConfirmPassword: String) -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.login_title),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W600,
                            color = Color(0xFFC9D1C8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF304040)
                )
            )
        },
        containerColor = Color(0xFF5B7065)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    value = iPValue,
                    onValueChange = { onIPValueChange(it) },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.login_authentication_ip_placeholder),
                        )
                    },
                    label = {
                        Text(text = stringResource(id = R.string.login_authentication_ip_title))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF04202C),
                        focusedTextColor = Color(0xFFFFFFFF),
                        focusedPlaceholderColor = Color(0xFFC9D1C8),
                        focusedLabelColor = Color(0xFFC9D1C8),
                        unfocusedBorderColor = Color(0xFF04202C),
                        unfocusedTextColor = Color(0xFFC9D1C8),
                        unfocusedPlaceholderColor = Color(0xFFC9D1C8),
                        unfocusedLabelColor = Color(0xFFC9D1C8),
                        cursorColor = Color(0xFFC9D1C8),
                        focusedContainerColor = Color(0xFF7A8F84),
                        unfocusedContainerColor = Color(0xFF7A8F84)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    maxLines = 1,
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    value = emailValue,
                    onValueChange = { onEmailValueChange(it) },
                    placeholder = {
                        Text(
                            text = stringResource(
                                id = if (isLogin)
                                    R.string.login_email_placeholder
                                else
                                    R.string.registration_email_placeholder
                            )
                        )
                    },
                    label = {
                        Text(text = stringResource(id = R.string.login_title))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF04202C),
                        focusedTextColor = Color(0xFFFFFFFF),
                        focusedPlaceholderColor = Color(0xFFC9D1C8),
                        focusedLabelColor = Color(0xFFC9D1C8),
                        unfocusedBorderColor = Color(0xFF04202C),
                        unfocusedTextColor = Color(0xFFC9D1C8),
                        unfocusedPlaceholderColor = Color(0xFFC9D1C8),
                        unfocusedLabelColor = Color(0xFFC9D1C8),
                        cursorColor = Color(0xFFC9D1C8),
                        focusedContainerColor = Color(0xFF7A8F84),
                        unfocusedContainerColor = Color(0xFF7A8F84)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    maxLines = 1,
                    singleLine = true
                )
                OutlinedTextField(
                    modifier = Modifier.padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    value = passwordValue,
                    onValueChange = { onPasswordValueChange(it) },
                    placeholder = {
                        Text(
                            text = stringResource(
                                id = if (isLogin)
                                    R.string.login_password_placeholder
                                else
                                    R.string.registration_password_placeholder
                            )
                        )
                    },
                    label = {
                        Text(text = stringResource(id = R.string.login_password_title))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF04202C),
                        focusedTextColor = Color(0xFFFFFFFF),
                        focusedPlaceholderColor = Color(0xFFC9D1C8),
                        focusedLabelColor = Color(0xFFC9D1C8),
                        unfocusedBorderColor = Color(0xFF04202C),
                        unfocusedTextColor = Color(0xFFC9D1C8),
                        unfocusedPlaceholderColor = Color(0xFFC9D1C8),
                        unfocusedLabelColor = Color(0xFFC9D1C8),
                        cursorColor = Color(0xFFC9D1C8),
                        focusedContainerColor = Color(0xFF7A8F84),
                        unfocusedContainerColor = Color(0xFF7A8F84)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    maxLines = 1,
                    singleLine = true
                )
                AnimatedVisibility(visible = !isLogin) {
                    OutlinedTextField(
                        modifier = Modifier.padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        value = confirmPassValue,
                        onValueChange = { onConfirmationPasswordValueChange(it) },
                        placeholder = {
                            Text(text = stringResource(id = R.string.registration_confirmation_password_placeholder))
                        },
                        label = {
                            Text(text = stringResource(id = R.string.registration_confirmation_password_title))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF04202C),
                            focusedTextColor = Color(0xFFFFFFFF),
                            focusedPlaceholderColor = Color(0xFFC9D1C8),
                            focusedLabelColor = Color(0xFFC9D1C8),
                            unfocusedBorderColor = Color(0xFF04202C),
                            unfocusedTextColor = Color(0xFFC9D1C8),
                            unfocusedPlaceholderColor = Color(0xFFC9D1C8),
                            unfocusedLabelColor = Color(0xFFC9D1C8),
                            cursorColor = Color(0xFFC9D1C8),
                            focusedContainerColor = Color(0xFF7A8F84),
                            unfocusedContainerColor = Color(0xFF7A8F84)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        maxLines = 1,
                        singleLine = true
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(visible = isLogin) {
                        Button(
                            modifier = Modifier.padding(end = 8.dp),
                            onClick = { onLoginClick() },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF304040)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                text = stringResource(id = R.string.login_title),
                                color = Color(0xFFC9D1C8),
                                fontWeight = FontWeight.W500,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Button(
                        onClick = { onRegistrationClick() },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF304040)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            text = stringResource(id = if (isLogin) R.string.login_registration_button_text else R.string.login_register_button_text),
                            color = Color(0xFFC9D1C8),
                            fontWeight = FontWeight.W500,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
