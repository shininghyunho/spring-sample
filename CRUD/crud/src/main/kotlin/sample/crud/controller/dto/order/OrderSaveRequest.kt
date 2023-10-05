package sample.crud.controller.dto.order

class OrderSaveRequest (
    val userId: Long,
    val items : List<Item>,
) {
    class Item (
        val id: Long,
        val count: Int,
    )
}