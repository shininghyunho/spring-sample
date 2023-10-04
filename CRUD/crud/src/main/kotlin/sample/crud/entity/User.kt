package sample.crud.entity

import javax.persistence.*

@Entity
class User(
    var email: String,
    var password: String,

    @OneToMany(mappedBy = "user")
    val orders: MutableSet<Order> = hashSetOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}