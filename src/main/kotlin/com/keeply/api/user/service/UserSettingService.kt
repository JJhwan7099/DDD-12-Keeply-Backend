package com.keeply.api.user.service

import com.keeply.api.user.dto.UserSettingRequestDTO
import com.keeply.api.user.dto.UserSettingResponseDTO
import com.keeply.domain.user.entity.User
import com.keeply.domain.user.entity.UserSetting
import com.keeply.domain.user.repository.UserRepository
import com.keeply.domain.user.repository.UserSettingRepository
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.exception.user.UserNotFoundException
import com.keeply.global.api.exception.user.UserSettingNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserSettingService(
    private val userRepository: UserRepository,
    private val userSettingRepository: UserSettingRepository
) {
    /**
     * Retrieves the notification settings for the user with the given ID.
     *
     * @param userId ID of the user whose settings are being retrieved.
     * @return ApiResponse wrapping a UserSettingResponseDTO with `allowStorageNotification` and
     *         `allowMarketingNotification` reflecting the stored user settings (HTTP 200 on success).
     * @throws UserNotFoundException if no user exists for the given `userId`.
     * @throws UserSettingNotFoundException if the user has no associated settings record.
     */
    fun getUserSetting(userId: Long) : ApiResponse<UserSettingResponseDTO> {
        val user = getUser(userId)
        val userSetting = getUserSetting(user)
        return ApiResponse.success(
            HttpStatus.OK,
            UserSettingResponseDTO(
                allowStorageNotification = userSetting.storageNotificationEnabled,
                allowMarketingNotification = userSetting.marketingNotificationEnabled
            )
        )
    }

    /**
     * Updates a user's notification settings and returns the updated values.
     *
     * If a field in [requestDTO] is non-null, that setting is updated; null fields are left unchanged.
     *
     * @param userId ID of the user whose settings will be updated.
     * @param requestDTO DTO containing optional updated values for storage and marketing notifications.
     * @return An ApiResponse with HTTP 200 containing the user's current `UserSettingResponseDTO`.
     */
    fun setUserSetting(userId: Long, requestDTO: UserSettingRequestDTO):  ApiResponse<UserSettingResponseDTO> {
        val user = getUser(userId)
        val userSetting = getUserSetting(user)
        if (requestDTO.allowStorageNotification != null) {
            userSetting.storageNotificationEnabled = requestDTO.allowStorageNotification
        }
        if (requestDTO.allowMarketingNotification != null) {
            userSetting.marketingNotificationEnabled = requestDTO.allowMarketingNotification
        }
        return ApiResponse.success(
            HttpStatus.OK,
            UserSettingResponseDTO(
                allowStorageNotification = userSetting.storageNotificationEnabled,
                allowMarketingNotification = userSetting.marketingNotificationEnabled
            )
        )
    }

    /**
         * Retrieves the UserSetting for the given user.
         *
         * @param user The user whose settings are requested.
         * @return The UserSetting associated with the provided user.
         * @throws UserSettingNotFoundException If no UserSetting exists for the user.
         */
        private fun getUserSetting(user: User): UserSetting = userSettingRepository.findByUser(user)
        ?: throw UserSettingNotFoundException()

    /**
         * Fetches a User by its ID or throws when not found.
         *
         * @param userId The ID of the user to retrieve.
         * @throws UserNotFoundException if no user exists with the given ID.
         */
        private fun getUser(userId: Long): User = userRepository.findUserById(userId)
        ?: throw UserNotFoundException()

}