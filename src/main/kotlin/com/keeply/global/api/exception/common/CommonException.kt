package com.keeply.global.api.exception.common

import com.keeply.global.api.exception.code.ErrorResultCode

abstract class CommonException(
    val errorResultCode: ErrorResultCode
): RuntimeException(errorResultCode.message)