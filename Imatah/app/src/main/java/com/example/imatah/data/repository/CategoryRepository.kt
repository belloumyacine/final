package com.example.imatah.data.repository

import com.example.imatah.data.model.Category
import com.example.imatah.data.remote.SupabaseConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getCategory(id: UUID): Result<Category>
    suspend fun addCategory(category: Category): Result<Category>
    suspend fun updateCategory(category: Category): Result<Category>
    suspend fun deleteCategory(id: UUID): Result<Unit>
}

class CategoryRepositoryImpl : CategoryRepository {
    private val client = SupabaseConfig.client

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        client.postgrest["categories"]
            .select()
            .decodeList()
    }

    override suspend fun getCategory(id: UUID): Result<Category> = runCatching {
        client.postgrest["categories"]
            .select {
                eq("id", id)
            }
            .decodeSingle()
    }

    override suspend fun addCategory(category: Category): Result<Category> = runCatching {
        client.postgrest["categories"]
            .insert(category)
            .decodeSingle()
    }

    override suspend fun updateCategory(category: Category): Result<Category> = runCatching {
        client.postgrest["categories"]
            .update(category) {
                eq("id", category.id)
            }
            .decodeSingle()
    }

    override suspend fun deleteCategory(id: UUID): Result<Unit> = runCatching {
        client.postgrest["categories"]
            .delete {
                eq("id", id)
            }
    }
}
