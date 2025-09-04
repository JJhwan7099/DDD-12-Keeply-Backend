package com.keeply.global.api.exception.user

import com.keeply.global.api.exception.code.user.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class UserNotFoundException: CommonException(ErrorCode.USER_NOT_FOUND)