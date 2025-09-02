package com.keeply.global.exception.user

import com.keeply.global.exception.code.user.ErrorCode
import com.keeply.global.exception.common.CommonException

class UserSettingNotFoundException: CommonException(ErrorCode.USER_SETTING_NOT_FOUND)