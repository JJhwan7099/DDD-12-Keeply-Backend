package com.keeply.global.api.exception.user

import com.keeply.global.api.exception.code.user.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class InvalidUserIdException: CommonException(ErrorCode.INVALID_USER_ID)