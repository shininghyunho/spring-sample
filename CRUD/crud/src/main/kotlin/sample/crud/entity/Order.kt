package sample.crud.entity

import javax.persistence.*

@Table(name = "ORDERS")
@Entity
class Order(
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    // Order 삭제시 OrderItem 도 같이 삭제
    @OneToMany(mappedBy = "order", cascade = [CascadeType.REMOVE])
    val orderItems: MutableSet<OrderItem> = hashSetOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}