package io.piano.android.cxense

import io.piano.android.cxense.model.EventDataRequest
import io.piano.android.cxense.model.SegmentLookupRequest
import io.piano.android.cxense.model.SegmentLookupResponse
import io.piano.android.cxense.model.TypedSegmentsResponse
import io.piano.android.cxense.model.User
import io.piano.android.cxense.model.UserDataRequest
import io.piano.android.cxense.model.UserExternalDataRequest
import io.piano.android.cxense.model.UserExternalTypedData
import io.piano.android.cxense.model.UserExternalTypedDataResponse
import io.piano.android.cxense.model.UserIdentity
import io.piano.android.cxense.model.UserIdentityMappingRequest
import io.piano.android.cxense.model.UserSegmentRequest
import io.piano.android.cxense.model.WidgetRequest
import io.piano.android.cxense.model.WidgetResponse
import io.piano.android.cxense.model.WidgetVisibilityReport
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

internal interface CxApi {
    @Authorized
    @POST(ENDPOINT_SEGMENTS_LOOKUP)
    suspend fun lookupSegments(@Body request: SegmentLookupRequest): Response<SegmentLookupResponse>

    @Authorized
    @POST(ENDPOINT_USER_SEGMENTS)
    suspend fun getUserTypedSegments(@Body request: UserSegmentRequest): Response<TypedSegmentsResponse>

    @Authorized
    @POST(ENDPOINT_USER_PROFILE)
    suspend fun getUser(@Body request: UserDataRequest): Response<User>

    @Authorized
    @POST(ENDPOINT_READ_USER_EXTERNAL_DATA)
    suspend fun getUserExternalTypedData(
        @Body request: UserExternalDataRequest,
    ): Response<UserExternalTypedDataResponse>

    @Authorized
    @POST(ENDPOINT_UPDATE_USER_EXTERNAL_DATA)
    suspend fun setUserExternalTypedData(@Body externalData: UserExternalTypedData): Response<Unit>

    @Authorized
    @POST(ENDPOINT_DELETE_USER_EXTERNAL_DATA)
    suspend fun deleteExternalUserData(@Body identity: UserIdentity): Response<Unit>

    @Authorized
    @POST(ENDPOINT_READ_USER_EXTERNAL_LINK)
    suspend fun getUserExternalLink(@Body request: UserIdentityMappingRequest): Response<UserIdentity>

    @Authorized
    @POST(ENDPOINT_UPDATE_USER_EXTERNAL_LINK)
    suspend fun addUserExternalLink(@Body request: UserIdentityMappingRequest): Response<UserIdentity>

    @Authorized
    @POST(ENDPOINT_PUSH_DMP_EVENTS)
    suspend fun pushEvents(@Body request: EventDataRequest): Response<Unit>

    @GET("https://comcluster.cxense.com/Repo/rep.gif")
    suspend fun trackInsightEvent(@QueryMap options: Map<String, String>): Response<Unit>

    @GET("https://comcluster.cxense.com/dmp/push.gif")
    suspend fun trackDmpEvent(
        @Query("persisted") persistentId: String,
        @QueryMap options: Map<String, String>,
    ): Response<Unit>

    @POST("https://comcluster.cxense.com/cce/push?experimental=true")
    suspend fun pushConversionEvents(@Body request: EventDataRequest): Response<Unit>

    @POST("public/widget/data")
    suspend fun getWidgetData(@Body request: WidgetRequest): Response<WidgetResponse>

    @POST("/public/widget/visibility")
    suspend fun reportWidgetVisibility(@Body request: WidgetVisibilityReport): Response<Unit>

    @GET
    suspend fun trackUrlClick(@Url url: String): Response<Unit>

    @GET
    suspend fun getPersisted(@Url url: String, @Query("persisted") persistentId: String): Response<ResponseBody>

    @POST
    suspend fun postPersisted(
        @Url url: String,
        @Query("persisted") persistentId: String,
        @Body data: Any,
    ): Response<ResponseBody>
}
