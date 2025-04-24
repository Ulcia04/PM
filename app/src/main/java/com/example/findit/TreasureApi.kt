//package com.example.findit
//
//import okhttp3.MultipartBody
//import retrofit2.http.*
//import retrofit2.Response
//import okhttp3.RequestBody
//private const val BASE_URL = "http://10.0.2.2:8080" // <- dla emulatora
//
//interface TreasureApi {
//
//    @GET("/api/treasures")
//    suspend fun getTreasures(): List<Treasure>
//
//    @POST("/api/treasures")
//    suspend fun addTreasure(@Body treasure: Treasure): Treasure
//
//    @POST("/api/found")
//    suspend fun markTreasureAsFound(@Body request: FoundRequest): Response<Unit>
//
//    @GET("/api/found")
//    suspend fun getFoundTreasures(@Query("userId") userId: Int): List<Int>
//
//    @GET("/api/found/treasures")
//    suspend fun getUserFoundTreasures(@Query("userId") userId: Int): List<Treasure>
//
//    @GET("/api/ranking/position")
//    suspend fun getUserRanking(@Query("userId") userId: Int): Int
//
//    @Multipart
//    @POST("/api/photo")
//    suspend fun uploadPhoto(
//        @Part photo: MultipartBody.Part,
//        @Part("userId") userId: RequestBody,
//        @Part("treasureId") treasureId: RequestBody
//    ): Response<Unit>
//
//    @GET("/api/found/withPhoto")
//    suspend fun getFoundTreasureIdsWithPhotos(@Query("userId") userId: Int): List<Int>
//
//    @PUT("/api/users/{id}")
//    suspend fun updateUsername(@Path("id") userId: Int, @Body username: String): Response<Unit>
//    @GET("/api/userByIp")
//    suspend fun getUserByIp(@Query("ip") ip: String): UserResponse
////    @POST("/api/users")
////    @FormUrlEncoded
////    suspend fun createUser(@Field("username") username: String): Response<Unit>
//    @FormUrlEncoded
//    @POST("/api/users")
//    suspend fun createUser(
//        @Field("username") username: String
//    ): Response<Map<String, Int>>
//    @GET("/api/users")
//    suspend fun getUsers(): List<Map<String, Any>>
//
//}
//data class UserResponse(val id: Int, val username: String)
//
//data class FoundRequest(
//    val userId: Int,
//    val treasureId: Int
//)

package com.example.findit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import kotlinx.serialization.Serializable

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

    @PUT("/api/users/{id}")
    suspend fun updateUsername(
        @Path("id") userId: Int,
        @Body username: String
    ): Response<Unit>

    @GET("/api/users")
    suspend fun getUsers(): List<Map<String, Any>>

    @FormUrlEncoded
    @POST("/api/users")
    suspend fun createUser(
        @Field("username") username: String,
        @Field("ip") ip: String
    ): Response<Map<String, Int>>

    @GET("/api/userByIp")
    suspend fun getUserByIp(@Query("ip") ip: String): UserResponse
    @GET("/api/users")
    suspend fun getUsersWithFoundCounts(): List<UserWithFoundCount>


}

@Serializable
data class UserResponse(
    val id: Int,
    val username: String
)
@Serializable
data class FoundRequest(
    val userId: Int,
    val treasureId: Int
)
@kotlinx.serialization.Serializable
data class UserWithFoundCount(
    val id: Int,
    val username: String,
    val foundCount: Int
)

