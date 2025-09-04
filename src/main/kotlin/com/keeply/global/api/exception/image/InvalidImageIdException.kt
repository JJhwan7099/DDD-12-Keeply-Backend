package com.keeply.global.api.exception.image

import com.keeply.global.api.exception.code.image.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class InvalidImageIdException: CommonException(ErrorCode.INVALID_IMAGE_ID)