package com.example.imatah.data.repository

import com.example.imatah.data.model.Profile
import com.example.imatah.data.remote.SupabaseConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

interface ProfileRepository {
    suspend fun getProfile(userId: UUID): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Profile>
    suspend fun getCurrentUserProfile(): Result<Profile>
}

class ProfileRepositoryImpl : ProfileRepository {
    private val client = SupabaseConfig.client

    override suspend fun getProfile(userId: UUID): Result<Profile> = runCatching {
        client.postgrest["profiles"]
            .select {
                eq("id", userId)
            }
            .decodeSingle()
    }

    override suspend fun updateProfile(profile: Profile): Result<Profile> = runCatching {
        client.postgrest["profiles"]
            .update(profile) {
                eq("id", profile.id)
            }
            .decodeSingle()
    }

    override suspend fun getCurrentUserProfile(): Result<Profile> = runCatching {
        val userId = client.gotrue.currentUserOrNull()?.id ?: throw IllegalStateException("No user logged in")
        getProfile(UUID.fromString(userId)).getOrThrow()
    }
} 