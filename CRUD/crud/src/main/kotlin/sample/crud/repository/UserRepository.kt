package sample.crud.repository

import org.springframework.data.jpa.repository.JpaRepository
import sample.crud.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?
}