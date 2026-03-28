package com.example.translator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data Models
data class TranslationResponse(val responseData: ResponseData)
data class ResponseData(val translatedText: String)

// Retrofit API Interface
interface TranslationApi {
    @GET("get")
    suspend fun translateText(
        @Query("q") query: String,
        @Query("langpair") langPair: String
    ): TranslationResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://api.mymemory.translated.net/"

    val api: TranslationApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TranslationApi::class.java)
    }
}

class TranslatorViewModel : ViewModel() {

    private val _translatedText = MutableLiveData<String>()
    val translatedText: LiveData<String> = _translatedText

    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    fun translateText(text: String, sourceLang: String, targetLang: String) {
        viewModelScope.launch {
            try {
                // Free API allows langpair in format like "en|es"
                val langpair = "$sourceLang|$targetLang"
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.translateText(text, langpair)
                }
                _translatedText.postValue(response.responseData.translatedText)
            } catch (e: Exception) {
                _errorMsg.postValue(e.localizedMessage ?: "Failed to translate")
            }
        }
    }
}
