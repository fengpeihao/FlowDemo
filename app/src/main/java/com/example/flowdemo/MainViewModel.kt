package com.example.flowdemo

import android.util.Log
import androidx.lifecycle.*
import com.example.flowdemo.bean.BaseResponse
import com.example.flowdemo.bean.LoginResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @description
 * @author: created by peihao.feng
 * @date: 2021/11/16
 */
class MainViewModel : ViewModel() {
    val apiConnection = ApiConnection()

    val errorStateFlow = MutableStateFlow(BaseResponse())

    fun loginAndGetUserInfo() {
        viewModelScope.launch {
            apiConnection.getTokenReturnedFlow().onStart {
                Log.d("***", "onStart")
            }.flatMapConcat {
                apiConnection.loginReturnedFlow(it.token ?: "")
            }.flatMapConcat {
                apiConnection.getUserInfoReturnedFlow(it.userId ?: "")
            }.catch {
                Log.d("***", "catch ${it.message}")
            }.onCompletion {
                Log.d("***", "onCompletion")
            }.collect()
        }
    }

    fun loginAndGetUserInfo2() {
        viewModelScope.launch {
            flow {
                val tokenResponse = apiConnection.getToken()
                emit(tokenResponse)
            }.onStart {
                Log.d("***", "onStart")
            }.onCompletion {
                Log.d("***", "onCompletion")
            }.onEach { tokenResponseFlow ->
                Log.d("***", tokenResponseFlow.toString())
            }.transform {
                val loginResponse = apiConnection.login(it.token ?: "")
                emit(loginResponse)
            }.onEach { loginResponseFlow ->
                Log.d("***", loginResponseFlow.toString())
            }.transform {
                val userInfoResponse = apiConnection.getUserInfo(it.userId ?: "")
                emit(userInfoResponse)
            }.onEach { userInfoResponseFlow ->
                Log.d("***", userInfoResponseFlow.toString())
            }.collect()
        }
    }
}