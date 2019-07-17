package us.gonet.appmoviesgithub.model

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object ApiClient {

    private const val BASE_URL = "http://api.themoviedb.org/3/"
    private var retrofit: Retrofit? = null
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w200/"
    const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780/"
    const val API_KEY="a858f4af7754f6025494dea5eeffd6cd"

    /**
     * This method returns retrofit client instance
     *
     * @return Retrofit object
     */
    val client: Retrofit
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
}