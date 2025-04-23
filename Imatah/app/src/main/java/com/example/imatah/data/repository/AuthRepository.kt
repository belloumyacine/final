package com.example.imatah.data.repository

import com.example.imatah.data.model.User
import com.example.imatah.data.remote.SupabaseConfig
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
}

class AuthRepositoryImpl : AuthRepository {
    private val client = SupabaseConfig.client

    override suspend fun signUp(email: String, password: String): Result<Unit> = runCatching {
        client.gotrue.signUpWith(io.github.jan.supabase.gotrue.providers.Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        client.gotrue.loginWith(io.github.jan.supabase.gotrue.providers.Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        client.gotrue.logout()
    }

    override suspend fun resetPassword(email: String): Result<Unit> = runCatching {
        client.gotrue.resetPasswordForEmail(email)
    }

    override fun isUserLoggedIn(): Boolean {
        return client.gotrue.currentUserOrNull() != null
    }

    suspend fun updateProfile(name: String?, avatarUrl: String?): Result<User> = try {
        val response = client.auth.updateUser {
            data = mapOf(
                "name" to name,
                "avatar_url" to avatarUrl
            )
        }
        Result.success(
            User(
                id = UUID.fromString(response.user?.id),
                email = response.user?.email ?: "",
                name = name,
                avatarUrl = avatarUrl
            )
        )
    } catch (e: Exception) {
        Result.failure(e)
    }
} 