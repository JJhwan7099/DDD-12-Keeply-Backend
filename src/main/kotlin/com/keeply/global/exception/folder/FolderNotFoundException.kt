package com.keeply.global.exception.folder

import com.keeply.global.exception.code.folder.ErrorCode
import com.keeply.global.exception.common.CommonException

class FolderNotFoundException: CommonException(ErrorCode.FOLDER_NOT_FOUND)