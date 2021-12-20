package com.example.flowdemo

import android.util.Log
import com.example.flowdemo.bean.LoginResponse
import com.example.flowdemo.bean.TokenResponse
import com.example.flowdemo.bean.UserInfoResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

/**
 * @description
 * @author: created by peihao.feng
 * @date: 2021/11/16
 */
class ApiConnection {


    suspend fun getToken(): TokenResponse {
        delay(1000)
        return TokenResponse("token")
    }

    suspend fun login(token: String): LoginResponse {
        delay(1000)
        return LoginResponse("userId")
    }

    fun getTokenReturnedFlow(): Flow<TokenResponse> {
        return flow {
            Log.d("***", "call getToken api")
            delay(1000)
            Log.d("***", "callback getToken api")
            emit(TokenResponse("token"))
        }
    }

    suspend fun loginReturnedFlow(token: String): Flow<LoginResponse> {
        return flow {
            Log.d("***", "call login api")
            delay(1000)
            Log.d("***", "callback login api")
            emit(LoginResponse("token"))
        }
    }

    suspend fun getUserInfo(userId: String): UserInfoResponse {
        delay(1000)
        return UserInfoResponse("Tom", 20)
    }

    suspend fun getUserInfoReturnedFlow(userId: String): Flow<UserInfoResponse> {
        return flow {
            Log.d("***", "call getUserInfo api")
            delay(1000)
            Log.d("***", "callback getUserInfo api")
            emit(UserInfoResponse("Tom", 20))
        }
    }

    suspend fun getUserList(): List<UserInfoResponse> {
        delay(3000)
        return arrayListOf(
            UserInfoResponse("Tom", 20),
            UserInfoResponse("Jim", 19),
            UserInfoResponse("Jerry", 22),
        )
    }
}