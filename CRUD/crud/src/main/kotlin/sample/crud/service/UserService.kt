package sample.crud.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    fun save(request: UserSaveRequest) {
        userRepository.save(User(
            email = request.email,
            nickname = request.nickname?: getRandomNickname(),
            password = request.password,
        ))
    }

    private fun getRandomNickname() : String {
        // 중복 없는 USER_랜덤(숫자+영어 대소문자) 5자리
        // 가능한 조합의 경우의 수는 62^6-1 = 56,800,235,584
        while(true) {
            val randomNickname = "USER_"+(0..4).map { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random() }.joinToString("")
            if (userRepository.findByNickname(randomNickname) == null) {
                return randomNickname
            }
        }
    }

    @Transactional(readOnly = true)
    fun get(id: Long): User {
        return userRepository.findById(id).orElseThrow()
    }

    @Transactional
    fun update(id: Long, request: UserUpdateRequest) {
        val user = get(id)
        user.update(
            email = request.email,
            nickname = request.nickname,
            password = request.password
        )
    }

    @Transactional
    fun delete(id: Long) {
        userRepository.deleteById(id)
    }
}