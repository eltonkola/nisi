package com.eltonkola.nisi.data.remote

import com.eltonkola.nisi.BuildConfig
import com.eltonkola.nisi.data.model.PexelsCuratedResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.* // Or OkHttp
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

const val PEXELS_API_KEY = BuildConfig.PEXELS_API_KEY

interface PexelsApiService {
    suspend fun getCuratedPhotos(page: Int = 1, perPage: Int = 20): Result<PexelsCuratedResponse>
}

@Singleton
class PexelsApiServiceImpl @Inject constructor() : PexelsApiService {

    private val client = HttpClient(CIO) {
        expectSuccess = true

        // Configure JSON Serialization
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Important for API changes
            })
        }

        // Add default headers (like Authorization) to every request
        defaultRequest {
            url("https://api.pexels.com/v1/")
            header(HttpHeaders.Authorization, PEXELS_API_KEY)
        }
    }

    override suspend fun getCuratedPhotos(page: Int, perPage: Int): Result<PexelsCuratedResponse> {
        return try {
            val response: PexelsCuratedResponse = client.get("curated") {
                parameter("page", page)
                parameter("per_page", perPage)
            }.body() // Deserialize response body
            Result.success(response)
        } catch (e: Exception) {
            // Log the exception
            println("Pexels API Error: ${e.message}")
            Result.failure(e) // Return failure Result
        }
    }

}