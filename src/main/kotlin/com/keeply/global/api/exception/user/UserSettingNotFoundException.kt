package com.keeply.global.api.exception.user

import com.keeply.global.api.exception.code.user.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class UserSettingNotFoundException: CommonException(ErrorCode.USER_SETTING_NOT_FOUND)