package com.onza.barcode.base

class BaseResponse<T>(
        val data: T?,
        val error: BaseError?)