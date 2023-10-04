package sample.crud.entity

import javax.persistence.*

@Table(name = "ORDERS")
@Entity
class Order(
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @OneToMany(mappedBy = "order")
    val orderItems: MutableSet<OrderItem> = hashSetOf(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}