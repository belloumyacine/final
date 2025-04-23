package com.example.imatah.data.model

import java.time.Instant
import java.util.UUID

data class Profile(
    val id: UUID,
    val name: String?,
    val avatarUrl: String?,
    val role: UserRole = UserRole.USER,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

enum class UserRole {
    USER,
    ADMIN
} 