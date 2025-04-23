package com.example.imatah.data.repository

import com.example.imatah.data.model.Report
import com.example.imatah.data.remote.SupabaseConfig
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import java.util.UUID

interface ReportRepository {
    suspend fun getReports(): Result<List<Report>>
    suspend fun getReport(id: UUID): Result<Report>
    suspend fun addReport(report: Report): Result<Report>
    suspend fun updateReport(report: Report): Result<Report>
    suspend fun deleteReport(id: UUID): Result<Unit>
    suspend fun searchReports(query: String): Result<List<Report>>
    suspend fun filterReportsByStatus(status: String): Result<List<Report>>
    suspend fun filterReportsByCategory(categoryId: UUID): Result<List<Report>>
}

class ReportRepositoryImpl : ReportRepository {
    private val client = SupabaseConfig.client

    override suspend fun getReports(): Result<List<Report>> = runCatching {
        client.postgrest["reports"]
            .select()
            .decodeList()
    }

    override suspend fun getReport(id: UUID): Result<Report> = runCatching {
        client.postgrest["reports"]
            .select {
                eq("id", id)
            }
            .decodeSingle()
    }

    override suspend fun addReport(report: Report): Result<Report> = runCatching {
        client.postgrest["reports"]
            .insert(report)
            .decodeSingle()
    }

    override suspend fun updateReport(report: Report): Result<Report> = runCatching {
        client.postgrest["reports"]
            .update(report) {
                eq("id", report.id)
            }
            .decodeSingle()
    }

    override suspend fun deleteReport(id: UUID): Result<Unit> = runCatching {
        client.postgrest["reports"]
            .delete {
                eq("id", id)
            }
    }

    override suspend fun searchReports(query: String): Result<List<Report>> = runCatching {
        client.postgrest["reports"]
            .select {
                or {
                    textSearch("title", query)
                    textSearch("description", query)
                }
            }
            .decodeList()
    }

    override suspend fun filterReportsByStatus(status: String): Result<List<Report>> = runCatching {
        client.postgrest["reports"]
            .select {
                eq("status", status)
            }
            .decodeList()
    }

    override suspend fun filterReportsByCategory(categoryId: UUID): Result<List<Report>> = runCatching {
        client.postgrest["reports"]
            .select {
                eq("category_id", categoryId)
            }
            .decodeList()
    }
}
