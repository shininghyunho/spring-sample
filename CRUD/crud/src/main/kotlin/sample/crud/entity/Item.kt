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

    fun update(name: String?, price: Long?, quantity: Int?) {
        name?.let { this.name = it }
        price?.let { this.price = it }
        quantity?.let { this.quantity = it }
    }
}