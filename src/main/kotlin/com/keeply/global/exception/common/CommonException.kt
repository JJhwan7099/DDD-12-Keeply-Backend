package com.keeply.global.exception.common

import com.keeply.global.exception.code.ErrorResultCode

abstract class CommonException(
    val errorResultCode: ErrorResultCode
): RuntimeException(errorResultCode.message)