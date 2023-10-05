package sample.crud.controller.dto.order

class OrderGetResponse (
    val id: Long,
    val userId: Long,
    val items: List<Item>,
) {
    class Item (
        val id: Long,
        val name: String,
        val price: Long,
        val count: Int,
    )
}