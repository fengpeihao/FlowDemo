package com.example.flowdemo.bean

/**
 * @description
 * @author: created by peihao.feng
 * @date: 2021/12/20
 */
data class LoginResponse(val userId: String? = null) : BaseResponse() {
    override fun toString(): String {
        return if (errorCode != -1)
            "LoginResponse{ errorCode: $errorCode, errorMsg: $errorMsg }"
        else
            "LoginResponse{ userId: $userId }"
    }
}
