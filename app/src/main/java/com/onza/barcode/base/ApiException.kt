package com.onza.barcode.base

class ApiException(val error: String, val code: Int): RuntimeException(error)