package sample.crud.entity

import javax.persistence.*

@Entity
class User(
    var email: String,
    var nickname: String,
    var password: String,

    // User 삭제시 Order 도 같이 삭제
    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    val orders: MutableSet<Order> = hashSetOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    fun update(email: String?, nickname: String?, password: String?) {
        email?.let { this.email = it }
        nickname?.let { this.nickname = it }
        password?.let { this.password = it }
    }
}