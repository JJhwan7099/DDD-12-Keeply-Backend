package com.keeply.global.api.exception.image

import com.keeply.global.api.exception.code.image.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class ImageSizeTooLargeException: CommonException(ErrorCode.IMAGE_SIZE_TOO_LARGE)