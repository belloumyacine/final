package com.example.imatah.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val avatarUrl: String? = null
) 