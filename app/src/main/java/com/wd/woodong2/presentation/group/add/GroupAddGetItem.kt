package com.wd.woodong2.presentation.group.add

sealed class GroupAddGetItem(
    open val id: String
) {
    data class Title(
        override val id: String,
        val title: String
    ): GroupAddGetItem(id)

    data class Description(
        override val id: String,
        val description: String
    ): GroupAddGetItem(id)

    data class ChipGroup(
        override val id: String,
        val chipButtons: List<String>
    ): GroupAddGetItem(id)

    data class EditText(
        override val id: String,
        val maxLines: Int?,
        val minLines: Int?,
        val hint: String?
    ): GroupAddGetItem(id)

    data class Password(
        override val id: String,
        val passwordHint: String?,
        val chkBoxText: String,
        val isChecked: Boolean = false
    ): GroupAddGetItem(id)

    data class Image(
        override val id: String,
        val image: Int?
    ): GroupAddGetItem(id)

    data class Divider(
        override val id: String
    ): GroupAddGetItem(id)
}