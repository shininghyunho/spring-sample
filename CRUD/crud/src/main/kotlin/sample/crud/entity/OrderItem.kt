package sample.crud.entity

import javax.persistence.*

@Entity
class OrderItem(
    order: Order,
    @ManyToOne
    @JoinColumn(name = "item_id") val item: Item,
    val count: Int,
) {
    init {
        _setOrder(order)
    }

    @ManyToOne
    @JoinColumn(name = "order_id")
    lateinit var order: Order

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    private fun _setOrder(order: Order) {
        this.order = order
        order.orderItems.add(this)
    }
}