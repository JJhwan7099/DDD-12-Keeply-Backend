package com.keeply.api.login.service

import com.keeply.api.login.dto.KakaoUserInfo
import com.keeply.api.login.dto.LoginResponseDTO
import com.keeply.domain.user.entity.User
import com.keeply.domain.user.entity.UserSetting
import com.keeply.domain.user.repository.UserRepository
import com.keeply.domain.user.repository.UserSettingRepository
import com.keeply.global.api.dto.ApiResponse
import com.keeply.global.aws.lambda.LambdaService
import com.keeply.global.security.JwtProvider
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LoginService (
    private val userRepository: UserRepository,
    private val userSettingRepository: UserSettingRepository,
    private val lambdaService: LambdaService,
    private val jwtProvider: JwtProvider
) {
    fun loginAndRegister(requestDTO: KakaoUserInfo): ApiResponse<LoginResponseDTO> {
        val user: User = findOrSaveUserWithKakaoId(requestDTO)
        restoreDeletedUser(user)

        user.fcmToken = requestDTO.fcmToken
        val userSetting = findOrSaveUserSetting(user)
        user.userSetting = userSetting

        val jwtAccessToken = jwtProvider.generateAccessToken(user)
        val jwtRefreshToken = jwtProvider.generateRefreshToken(user)
        return ApiResponse.success(
            HttpStatus.OK,
            LoginResponseDTO(
                accessToken = jwtAccessToken,
                refreshToken = jwtRefreshToken
            )
        )
    }

    private fun restoreDeletedUser(user: User) {
        if (user.isDeleted) {
            user.isDeleted = false
            user.deletedAt = null
            user.scheduledDeleteAt = null
            lambdaService.restoreDeletedUserImages(user.id)
        }
    }

    private fun findOrSaveUserSetting(user: User): UserSetting = (userSettingRepository.findByUser(user)
        ?: userSettingRepository.save(
            UserSetting.builder()
                .user(user)
                .build()
        ))

    private fun findOrSaveUserWithKakaoId(userInfo: KakaoUserInfo): User = (
            userRepository.findUserById(userInfo.id)
                ?: userRepository.save(
                    User.builder()
                        .id(userInfo.id)
                        .nickname(userInfo.kakao_account.profile.nickname)
                        .email(userInfo.kakao_account.email)
                        .profileImageUrl(userInfo.kakao_account.profile.profile_image_url)
                        .thumbnailImageUrl(userInfo.kakao_account.profile.thumbnail_image_url)
                        .build()
                )
            )
}