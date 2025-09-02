package com.keeply.global.exception.user

import com.keeply.global.exception.code.user.ErrorCode
import com.keeply.global.exception.common.CommonException

class UserNotFoundException: CommonException(ErrorCode.USER_NOT_FOUND)