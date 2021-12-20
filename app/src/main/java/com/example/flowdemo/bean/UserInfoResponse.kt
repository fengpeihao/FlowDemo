package com.example.flowdemo.bean

/**
 * @description
 * @author: created by peihao.feng
 * @date: 2021/12/20
 */
data class UserInfoResponse(val userName: String? = null, val userAge: Int? = null) :
    BaseResponse() {
    override fun toString(): String {
        return if (errorCode != -1)
            "UserInfoResponse{ errorCode: $errorCode, errorMsg: $errorMsg }"
        else
            "UserInfoResponse{ name: $userName, age: $userAge }"
    }
}