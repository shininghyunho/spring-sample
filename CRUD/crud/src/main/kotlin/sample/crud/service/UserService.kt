package sample.crud.service

import org.springframework.stereotype.Service
import sample.crud.controller.dto.user.UserSaveRequest
import sample.crud.entity.User
import sample.crud.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    // TODO : 로그인 기능 구현은 따로 LOGIN 디렉토리에서 구현
    fun signUp(request: UserSaveRequest) {
        userRepository.save(User(
            email = request.email,
            password = request.password,
        ))
    }
}