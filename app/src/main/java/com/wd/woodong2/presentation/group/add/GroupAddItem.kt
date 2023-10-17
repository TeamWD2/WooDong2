package com.wd.woodong2.presentation.group.add

sealed class GroupAddItem(
) {
    data class Title(
        val title: String?
    ): GroupAddItem()

    data class Description(
        val description: String?
    ): GroupAddItem()

    data class ChipGroup(
        val isSingleSelection: Boolean?,
        val chipButtons: List<String>?
    ): GroupAddItem()

    data class EditText(
        val id: Long,
        val maxLines: Int?,
        val minLines: Int?,
        val hint: String?
    ): GroupAddItem()

    data class Password(
        val id: Long,
        val passwordHint: String?,
        val chkBoxText: String?,
        val isChecked: Boolean?
    ): GroupAddItem()

    data class Image(
        val id: Long,
        val image: Int?
    ): GroupAddItem()

    object Divider: GroupAddItem()
}