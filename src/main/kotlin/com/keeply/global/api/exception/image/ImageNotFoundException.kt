package com.keeply.global.api.exception.image

import com.keeply.global.api.exception.code.image.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class ImageNotFoundException: CommonException(ErrorCode.IMAGE_NOT_FOUND)