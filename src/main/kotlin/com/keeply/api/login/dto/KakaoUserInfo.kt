package com.keeply.api.login.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class KakaoUserInfo(
    val id: Long,
    val connected_at: LocalDateTime,
    val kakao_account: KakaoAccount,
    @NotBlank
    val fcmToken: String
)

data class KakaoAccount (
    val profile_needs_agreement: Boolean,
    val profile_nickname_needs_agreement: Boolean,
    val profile_image_needs_agreement: Boolean,
    val email_needs_agreement: Boolean,
    val profile: Profile,
    val email: String?
)

data class Profile(
    val nickname: String,
    val thumbnail_image_url: String,
    val profile_image_url: String
)