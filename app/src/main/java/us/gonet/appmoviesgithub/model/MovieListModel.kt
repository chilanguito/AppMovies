package us.gonet.appmoviesgithub.model

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import us.gonet.appmoviesgithub.model.ApiClient.API_KEY
import us.gonet.appmoviesgithub.presentation.MovieListContract

@Suppress("PrivatePropertyName")
class MovieListModel : MovieListContract.Model {

    private val TAG = "MovieListModel"

    /**
     * This function will fetch movies data
     * @param onFinishedListener
     * @param pageNo : Which page to load.
     */
    override fun getMovieList(onFinishedListener: MovieListContract.Model.OnFinishedListener, pageNo: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)

        val call = apiService.getPopularMovies(API_KEY, pageNo)
        call.enqueue(object : Callback<MovieListResponse> {
            override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                val movies = response.body()!!.results
                Log.d(TAG, "Number of movies received: " + movies!!.size)
                onFinishedListener.onFinished(movies)
            }

            override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
                onFinishedListener.onFailure(t)
            }
        })
    }

}