package com.keeply.global.exception.folder

import com.keeply.global.exception.code.folder.ErrorCode
import com.keeply.global.exception.common.CommonException

class InvalidFolderIdException: CommonException(ErrorCode.INVALID_FOLDER_ID)