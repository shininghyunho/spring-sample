package sample.crud.entity

import javax.persistence.*

@Entity
class Item(
    @Column(unique = true)
    var name: String,
    var price: Long,
    var quantity: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    fun update(name: String? = null, price: Long? = null, quantity: Int? = null) {
        name?.let { this.name = it }
        price?.let { this.price = it }
        quantity?.let { this.quantity = it }
    }
}