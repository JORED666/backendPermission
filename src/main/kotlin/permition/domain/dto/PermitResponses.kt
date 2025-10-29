package com.example.permition.domain.dto

import kotlinx.serialization.Serializable
import com.example.permition.domain.entitie.Permit

@Serializable
data class PermitResponse(
    val success: Boolean,
    val message: String,
    val data: Permit? = null
)

@Serializable
data class PermitsListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Permit> = emptyList()
)

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String
)