package com.keeply.api.user.service

import com.keeply.api.user.dto.UserInfoDTO
import com.keeply.domain.image.service.ImageDomainService
import com.keeply.domain.user.entity.User
import com.keeply.domain.user.repository.UserRepository
import com.keeply.global.api.ApiResponse
import com.keeply.global.api.dto.Message
import com.keeply.global.api.exception.user.UserNotFoundException
import com.keeply.global.aws.lambda.LambdaService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val lambdaService: LambdaService,
    private val imageDomainService: ImageDomainService

) {
    /**
     * Retrieves basic public information for the specified user.
     *
     * Returns an ApiResponse with HTTP 200 containing a UserInfoDTO (profile image URL, nickname, and email).
     *
     * @param userId The ID of the user to retrieve.
     * @return ApiResponse wrapping a UserInfoDTO on success (HttpStatus.OK).
     * @throws com.keeply.api.user.exception.UserNotFoundException if no user exists with the given ID.
     */
    fun getUserInfo(userId: Long): ApiResponse<UserInfoDTO> {
        val user = getUser(userId)
        return ApiResponse.success(
            HttpStatus.OK,
            UserInfoDTO(
                profileImageUrl = user.profileImageUrl,
                nickname = user.nickname,
                email = user.email
            )
        )
    }

    /**
     * Converts the specified user account to a dormant (deleted) state.
     *
     * Backs up the user's images, marks the account as deleted and schedules permanent deletion.
     *
     * @param userId ID of the user to convert to a dormant account.
     * @return ApiResponse containing a confirmation Message (HTTP 200) that includes the user ID.
     */
    fun deleteUser(userId: Long): ApiResponse<Message> {
        val user = getUser(userId)
        lambdaService.backupUserImagesBeforeDeletion(user.id)
        updateUserStatusToDeleted(user)

        return ApiResponse.success(
            HttpStatus.OK,
            Message(
                message = "휴면계정으로 변환되었습니다. (유저 ID = ${user.id})"
            )
        )
    }

    /**
     * Logs out a user by deleting their images and removing the user record.
     *
     * Deletes all images in the user's folders (via ImageDomainService) and then removes the user from the repository.
     *
     * @param userId ID of the user to log out and remove.
     * @return An ApiResponse containing a confirmation Message (HTTP 200) on successful logout and cleanup.
     */
    fun logout(userId: Long): ApiResponse<Message> {
        val user = getUser(userId)
        user.folders.forEach { folder ->
            folder.images.forEach {
                    image -> imageDomainService.deleteImage(user, image)
            }
        }
        userRepository.delete(user)
        return ApiResponse.success(
            HttpStatus.OK,
            Message("로그아웃 및 DB, S3 초기화")
        )
    }

    /**
     * Marks the given user as deleted and schedules permanent deletion.
     *
     * Sets the user's deletion flag, records the deletion timestamp as now, and sets
     * scheduledDeleteAt to 30 days from now.
     *
     * @param user The user entity to update (mutated in-place).
     */
    private fun updateUserStatusToDeleted(user: User) {
        user.isDeleted = true
        user.deletedAt = LocalDateTime.now()
        user.scheduledDeleteAt = LocalDateTime.now() + Duration.ofDays(30)
    }

    /**
         * Retrieves a User by its ID.
         *
         * @param userId The ID of the user to fetch.
         * @return The found User.
         * @throws UserNotFoundException if no user exists with the given ID.
         */
        private fun getUser(userId: Long): User = userRepository.findUserById(userId)
        ?: throw UserNotFoundException()
}