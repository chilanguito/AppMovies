package us.gonet.appmoviesgithub.movie_details

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import us.gonet.appmoviesgithub.model.ApiInterface
import us.gonet.appmoviesgithub.model.ApiClient
import us.gonet.appmoviesgithub.model.ApiClient.API_KEY
import us.gonet.appmoviesgithub.model.Movie
import us.gonet.appmoviesgithub.utils.Constants.CREDITS

@Suppress("PrivatePropertyName")
class MovieDetailsModel : MovieDetailsContract.Model {

    private val TAG = "MovieDetailsModel"

    override fun getMovieDetails(onFinishedListener: MovieDetailsContract.Model.OnFinishedListener, movieId: Int) {

        val apiService = ApiClient.client.create(ApiInterface::class.java)

        val call = apiService.getMovieDetails(movieId, API_KEY, CREDITS)
        call.enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                val movie = response.body()
                Log.d(TAG, "Movie data received: " + movie.toString())
                onFinishedListener.onFinished(movie!!)
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                Log.e(TAG, t.toString())
                onFinishedListener.onFailure(t)
            }
        })

    }
}