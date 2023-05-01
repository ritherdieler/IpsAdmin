package com.example.data2.data.response

data class BaseResponse<T>(
    var status: Int,
    var data: T? = null,
    var message: String? = null,
    var error: String? = null
)