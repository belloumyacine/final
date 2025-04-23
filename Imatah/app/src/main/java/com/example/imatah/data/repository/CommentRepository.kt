package com.example.imatah.data.repository

import com.example.imatah.data.model.Comment
import com.example.imatah.data.remote.SupabaseConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

interface CommentRepository {
    suspend fun getComments(reportId: UUID): Result<List<Comment>>
    suspend fun addComment(comment: Comment): Result<Comment>
    suspend fun updateComment(comment: Comment): Result<Comment>
    suspend fun deleteComment(id: UUID): Result<Unit>
}

class CommentRepositoryImpl : CommentRepository {
    private val client = SupabaseConfig.client

    override suspend fun getComments(reportId: UUID): Result<List<Comment>> = runCatching {
        client.postgrest["comments"]
            .select {
                eq("report_id", reportId)
            }
            .decodeList()
    }

    override suspend fun addComment(comment: Comment): Result<Comment> = runCatching {
        client.postgrest["comments"]
            .insert(comment)
            .decodeSingle()
    }

    override suspend fun updateComment(comment: Comment): Result<Comment> = runCatching {
        client.postgrest["comments"]
            .update(comment) {
                eq("id", comment.id)
            }
            .decodeSingle()
    }

    override suspend fun deleteComment(id: UUID): Result<Unit> = runCatching {
        client.postgrest["comments"]
            .delete {
                eq("id", id)
            }
    }
} 