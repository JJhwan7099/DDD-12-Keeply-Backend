package com.keeply.global.api.exception.folder

import com.keeply.global.api.exception.code.folder.ErrorCode
import com.keeply.global.api.exception.common.CommonException

class FolderNotFoundException: CommonException(ErrorCode.FOLDER_NOT_FOUND)