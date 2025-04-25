package com.eltonkola.nisi.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Use @Serializable for Ktor/Kotlinx Serialization

@Serializable
data class PexelsCuratedResponse(
    val photos: List<PexelsPhoto> = emptyList(),
    val page: Int? = null,
    @SerialName("per_page") val perPage: Int? = null,
    @SerialName("next_page") val nextPage: String? = null
)

@Serializable
data class PexelsPhoto(
    val id: Long,
    val width: Int? = null,
    val height: Int? = null,
    val url: String? = null, // Link to Pexels page
    val photographer: String? = null,
    @SerialName("photographer_url") val photographerUrl: String? = null,
    @SerialName("photographer_id") val photographerId: Long? = null,
    @SerialName("avg_color") val avgColor: String? = null,
    val src: PexelsPhotoSource,
    val liked: Boolean? = false,
    val alt: String? = null // Good for content description
)

@Serializable
data class PexelsPhotoSource(
    val original: String,
    @SerialName("large2x") val large2x: String, // Good for full image preview/setting
    val large: String? = null,
    val medium: String? = null,
    val small: String,         // Good for thumbnail
    val portrait: String? = null,
    val landscape: String? = null,
    val tiny: String           // Alternative thumbnail
)