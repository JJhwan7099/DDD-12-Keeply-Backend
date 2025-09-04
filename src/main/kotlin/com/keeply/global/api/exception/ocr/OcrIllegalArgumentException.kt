package com.keeply.global.api.exception.ocr

import com.keeply.global.api.exception.code.ocr.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class OcrIllegalArgumentException: CommonException(ErrorCode.OCR_ILLEGAL_ARGUMENT)