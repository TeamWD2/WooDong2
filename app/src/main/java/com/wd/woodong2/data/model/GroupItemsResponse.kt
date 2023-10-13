package com.wd.woodong2.data.model

import com.google.gson.annotations.SerializedName

data class GroupItemsResponse(
    val groupItems: List<GroupResponse>?
)

data class GroupResponse(
    val id: String?, //Firebase Realtime Database에서 자동 생성되는 고유 키
    @SerializedName("imgGroupProfile") val groupProfile: String?,
    @SerializedName("txtTitle") val title: String?,
    @SerializedName("imgMemberProfile1") val memberProfile1: String?,
    @SerializedName("imgMemberProfile2") val memberProfile2: String?,
    @SerializedName("imgMemberProfile3") val memberProfile3: String?,
    @SerializedName("txtMemberCount") val memberCount: Int?,
    @SerializedName("txtTagLocation") val tagLocation: String?,
    @SerializedName("txtTagCategory") val tagCategory: String?,
    @SerializedName("txtTagCapacity") val tagCapacity: Int?,
//    @SerializedName("group_profile") val groupProfile: String?,
//    @SerializedName("title") val title: String?,
//    @SerializedName("member_profile_1") val memberProfile1: String?,
//    @SerializedName("member_profile_2") val memberProfile2: String?,
//    @SerializedName("member_profile_3") val memberProfile3: String?,
//    @SerializedName("member_count") val memberCount: Int?,
//    @SerializedName("tag_location") val tagLocation: String?,
//    @SerializedName("tag_category") val tagCategory: String?,
//    @SerializedName("tag_capacity") val tagCapacity: Int?,
)