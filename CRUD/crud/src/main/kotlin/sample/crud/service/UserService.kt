package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sample.crud.common.response.ErrorCode
import sample.crud.common.response.error.CustomException
import sample.crud.controller.dto.user.UserGetResponse
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.controller.dto.user.UserUpdateRequest
import sample.crud.entity.User
import sample.crud.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    // TODO : 로그인 기능 구현은 따로 LOGIN 디렉토리에서 구현
    @Transactional
    fun save(request: UserSaveRequest) : Long {
        // validation
        validateUserSave(request.email, request.nickname)

        return userRepository.save(User(
            email = request.email,
            nickname = request.nickname?: getRandomNickname(),
            password = request.password,
        )).id
    }


    @Transactional(readOnly = true)
    fun getEntity(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    @Transactional(readOnly = true)
    fun get(id: Long): UserGetResponse? {
        return getEntity(id)?.let { user ->
            UserGetResponse(
                id = user.id,
                email = user.email,
                nickname = user.nickname,
            )
        }
    }

    @Transactional
    fun update(id: Long, request: UserUpdateRequest) {
        validateUserUpdate(request.email, request.nickname)
        val user = getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_USER)
        user.update(
            email = request.email,
            nickname = request.nickname,
            password = request.password
        )
    }

    @Transactional
    fun delete(id: Long) {
        userRepository.delete(getEntity(id) ?: throw CustomException(ErrorCode.NOT_EXISTED_USER))
    }
    private fun validateUserSave(email: String, nickname: String?) {
        // email
        if (userRepository.findByEmail(email) != null) {
            throw CustomException(ErrorCode.DUPLICATED_EMAIL)
        }
        // nickname
        if (nickname !=null && userRepository.findByNickname(nickname) != null) {
            throw CustomException(ErrorCode.DUPLICATED_NICKNAME)
        }
    }

    private fun validateUserUpdate(email: String?, nickname: String?) {
        // email
        if (email != null && userRepository.findByEmail(email) != null) {
            throw CustomException(ErrorCode.DUPLICATED_EMAIL)
        }
        // nickname
        if (nickname !=null && userRepository.findByNickname(nickname) != null) {
            throw CustomException(ErrorCode.DUPLICATED_NICKNAME)
        }
    }

    private fun getRandomNickname() : String {
        // 중복 없는 USER_랜덤(숫자+영어 대소문자) 5자리
        // 가능한 조합의 경우의 수는 62^6-1 = 56,800,235,584
        val length = 5
        while(true) {
            val randomNickname = "USER_"+(0 until length).map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }.joinToString("")
            if (userRepository.findByNickname(randomNickname) == null) {
                return randomNickname
            }
        }
    }
}
