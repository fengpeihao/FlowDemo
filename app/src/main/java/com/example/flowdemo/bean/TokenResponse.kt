package com.example.flowdemo.bean

/**
 * @description
 * @author: created by peihao.feng
 * @date: 2021/12/20
 */
data class TokenResponse(val token: String? = null) : BaseResponse() {

    override fun toString(): String {
        return if (errorCode != -1)
            "TokenResponse{ errorCode: $errorCode, errorMsg: $errorMsg }"
        else
            "TokenResponse{ token: $token }"
    }
}