package com.keeply.global.api.exception.image

import com.keeply.global.api.exception.code.image.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class ImageIllegalArgumentException: CommonException(ErrorCode.IMAGE_ILLEGAL_ARGUMENT)