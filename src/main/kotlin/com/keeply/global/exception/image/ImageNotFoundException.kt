package com.keeply.global.exception.image

import com.keeply.global.exception.code.image.ErrorCode
import com.keeply.global.exception.common.CommonException

class ImageNotFoundException: CommonException(ErrorCode.IMAGE_NOT_FOUND)