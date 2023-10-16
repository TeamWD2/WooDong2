package com.wd.woodong2.presentation.group.add

sealed class GroupAddItem {
    data class Title(
        val title: String?
    ): GroupAddItem()

    data class Description(
        val description: String?
    ): GroupAddItem()

    data class ChipGroup(
        val chipButtons: List<String>?
    ): GroupAddItem()

    data class EditText(
        val editText: String?
    ): GroupAddItem()

    data class Image(
        val image: Int?
    ): GroupAddItem()

    object Divider: GroupAddItem()
}