package com.keeply.global.exception.ocr

import com.keeply.global.exception.code.ocr.ErrorCode
import com.keeply.global.exception.common.CommonException

class OcrIllegalArgumentException: CommonException(ErrorCode.OCR_ILLEGAL_ARGUMENT)