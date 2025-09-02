package com.keeply.global.exception.image

import com.keeply.global.exception.code.image.ErrorCode
import com.keeply.global.exception.common.CommonException

class InvalidImageIdException: CommonException(ErrorCode.INVALID_IMAGE_ID)