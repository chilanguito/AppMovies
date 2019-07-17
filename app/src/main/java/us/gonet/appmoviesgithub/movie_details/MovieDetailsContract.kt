package us.gonet.appmoviesgithub.movie_details

import us.gonet.appmoviesgithub.model.Movie

interface MovieDetailsContract {

    interface Model {

        interface OnFinishedListener {
            fun onFinished(movie: Movie)

            fun onFailure(t: Throwable)
        }

        fun getMovieDetails(onFinishedListener: OnFinishedListener, movieId: Int)
    }

    interface View {

        fun showProgress()

        fun hideProgress()

        fun setDataToViews(movie: Movie?)

        fun onResponseFailure(throwable: Throwable)
    }

    interface Presenter {
        fun onDestroy()

        fun requestMovieData(movieId: Int)
    }
}