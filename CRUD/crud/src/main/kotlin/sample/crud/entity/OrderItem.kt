package sample.crud.entity

import javax.persistence.*

@Entity
class OrderItem(
    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: Order,

    @ManyToOne
    @JoinColumn(name = "item_id")
    val item: Item,

    val count: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}