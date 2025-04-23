package com.example.imatah.data.remote

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import java.util.Properties
import kotlinx.coroutines.runBlocking

object SupabaseClient {
    private lateinit var properties: Properties

    fun initialize(context: Context) {
        properties = Properties().apply {
            load(context.assets.open("local.properties"))
        }
    }

    private val supabaseUrl: String
        get() = "https://senhirqlyungjmpngdqn.supabase.co"

    private val supabaseKey: String
        get() = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNlbmhpcnFseXVuZ2ptcG5nZHFuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU0MDU3MjksImV4cCI6MjA2MDk4MTcyOX0.TJrY6gx0R1Mo_zCVOMXIEyfNSRJTab7kmbDZPSqbB90"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Postgrest)
            install(GoTrue)
            install(Realtime)
        }
    }

    suspend fun testConnection(): Boolean {
        return try {
            // محاولة الاتصال بقاعدة البيانات
            client.postgrest["reports"].select().limit(1)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
} 