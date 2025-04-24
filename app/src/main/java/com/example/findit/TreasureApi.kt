package com.example.findit

import okhttp3.MultipartBody
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
private const val BASE_URL = "http://10.0.2.2:8080" // <- dla emulatora

interface TreasureApi {

    @GET("/api/treasures")
    suspend fun getTreasures(): List<Treasure>

    @POST("/api/treasures")
    suspend fun addTreasure(@Body treasure: Treasure): Treasure

    @POST("/api/found")
    suspend fun markTreasureAsFound(@Body request: FoundRequest): Response<Unit>

    @GET("/api/found")
    suspend fun getFoundTreasures(@Query("userId") userId: Int): List<Int>

    @GET("/api/found/treasures")
    suspend fun getUserFoundTreasures(@Query("userId") userId: Int): List<Treasure>

    @GET("/api/ranking/position")
    suspend fun getUserRanking(@Query("userId") userId: Int): Int

    @Multipart
    @POST("/api/photo")
    suspend fun uploadPhoto(
        @Part photo: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("treasureId") treasureId: RequestBody
    ): Response<Unit>

    @GET("/api/found/withPhoto")
    suspend fun getFoundTreasureIdsWithPhotos(@Query("userId") userId: Int): List<Int>


}

data class FoundRequest(
    val userId: Int,
    val treasureId: Int
)
