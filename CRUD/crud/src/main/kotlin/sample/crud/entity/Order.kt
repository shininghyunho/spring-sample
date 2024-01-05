package sample.crud.entity

import javax.persistence.*

@Table(name = "ORDERS")
@Entity
class Order(
    user: User,
    // Order 삭제시 OrderItem 도 같이 삭제
    @OneToMany(mappedBy = "order", cascade = [CascadeType.REMOVE])
    val orderItems: MutableSet<OrderItem> = hashSetOf(),
) {
    init {
        _setUser(user)
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    lateinit var user: User

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    private fun _setUser(user: User) {
        this.user = user
        user.orders.add(this)
    }
}