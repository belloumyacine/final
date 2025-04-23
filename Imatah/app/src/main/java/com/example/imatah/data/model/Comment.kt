package com.example.imatah.data.model

import java.time.Instant
import java.util.UUID

data class Comment(
    val id: UUID = UUID.randomUUID(),
    val reportId: UUID,
    val userId: UUID,
    val content: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
) 