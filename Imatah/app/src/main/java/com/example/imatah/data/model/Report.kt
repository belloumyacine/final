package com.example.imatah.data.model

import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Serializable
data class Report(
    val id: UUID,
    val title: String,
    val description: String,
    val status: String,
    val createdAt: Date,
    val updatedAt: Date,
    val userId: String
)
