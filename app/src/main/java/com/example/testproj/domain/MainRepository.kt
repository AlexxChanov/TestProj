package com.flametech.vaytoday.domain.network.api

import com.flametech.vaytoday.data.pojo.*
import com.flametech.vaytoday.utils.App
import com.flametech.vaytoday.utils.SharedPreferencesManager
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext

private const val TAG = "MainRepository"
class MainRepository : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    private val header = mutableMapOf<String, String>()
    private val imageMap = mutableMapOf<String, String>()
    private val headerWithToken = mutableMapOf<String, String>()
    private val adminData = mutableMapOf<String, String>()
    private val authenticator = TokenRefresh(this)
    private var refreshTokenCountLimit = 0

    companion object {
        lateinit var builder: Retrofit.Builder
        lateinit var retrofit: Retrofit
    }

    val api = "https://api.test.ru"

    init {
        adminData["username"] = "test"
        adminData["password"] = "test"
        header["Api-key"] = "somekey"
        headerWithToken["X-Api-Key"] = "test"
        headerWithToken["Authorization"] =
            "Bearer ${SharedPreferencesManager(App.getInstance()).getAccessToken()}"
        builder = Retrofit.Builder()
            .baseUrl("https://api.test/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        retrofit = builder.build()
    }

    private fun httpClient(): OkHttpClient {
        val client = OkHttpClient()
        return client.newBuilder()
            .retryOnConnectionFailure(true)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .callTimeout(2, TimeUnit.MINUTES)
            .authenticator(authenticator)
            .addInterceptor(buildLoggingInterceptor())
            .build()
    }

    private fun buildLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }

    private val mainApi = retrofit
        .create(CategoriesApi::class.java)

    fun resetPassword(data: HashMap<String, String>) = mainApi.resetPassword(header, data)

    fun signUp(data: HashMap<String, Any>) = mainApi.signUp(header, data)

    fun login(data: HashMap<String, String>) = mainApi.login(header, data)

    fun loginAdmin() = mainApi.loginAdmin(header, adminData as HashMap<String, String>)

    fun refreshProfile(user: User) = mainApi.refreshProfile(headerWithToken, user)

    fun deleteImage(id: Int) = mainApi.deleteCompanyImages(headerWithToken, id)

    fun getProfile() = mainApi.getProfile(headerWithToken)

    fun getMyCompanies(limit: Int, offset: Int) =
        mainApi.getMyCompanies(headerWithToken, limit, offset)

    fun updateCompany(company: BusinessSent, id: Int) = mainApi.updateCompany(headerWithToken, company, id)

    fun deleteCompany(id: Int) = mainApi.deleteCompany(headerWithToken, id)

    fun getCategories() = mainApi.getCategory(header)

    fun getCities() = mainApi.getCities(header, 1000, 0)

    fun setCompany(company: BusinessSent) = mainApi.setCompany(headerWithToken, company)

    fun setJob(job: JobSent) = mainApi.setJob(headerWithToken, job)

    fun setVisit(id: HashMap<String, String>) = mainApi.setVisit(header, id)

    fun getRecommendations() = mainApi.getRecommendations(header)

    fun getSearchedCompanies(key: String) = mainApi.getSearchedCompanies(header, key)

    fun getReviewsWithToken(id: Int) = mainApi.getReviews(headerWithToken, id)

    fun getReviewsWithoutToken(id: Int) = mainApi.getReviews(header, id)

    fun setReview(review: CompanyReview) = mainApi.setReview(headerWithToken, review)

    fun setReply(reply: String, reviewId: Int) : Completable {
        val map = HashMap<String,String>()
        map["reply"] = reply
        return mainApi.setReply(headerWithToken,  reviewId, map)
    }

    fun updateReview(review: CompanyReview, id: Int) = mainApi.updateReview(headerWithToken, review, id)

    fun deleteReview(companyId: Int) = mainApi.deleteReview(headerWithToken, companyId)

    fun getUsersReview(companyId: Int) = mainApi.getUsersReview(headerWithToken, companyId)
}

