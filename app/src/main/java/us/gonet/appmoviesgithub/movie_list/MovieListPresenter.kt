package us.gonet.appmoviesgithub.movie_list

import us.gonet.appmoviesgithub.model.Movie
import us.gonet.appmoviesgithub.model.MovieListModel
import us.gonet.appmoviesgithub.presentation.MovieListContract

class MovieListPresenter(private var movieListView: MovieListContract.View?) : MovieListContract.Presenter,
    MovieListContract.Model.OnFinishedListener {

    private val movieListModel: MovieListContract.Model

    init {
        movieListModel = MovieListModel()
    }

    override fun onDestroy() {
        this.movieListView = null
    }

    override fun getMoreData(pageNo: Int) {

        if (movieListView != null) {
            movieListView!!.showProgress()
        }
        movieListModel.getMovieList(this, pageNo)
    }

    override fun requestDataFromServer() {

        if (movieListView != null) {
            movieListView!!.showProgress()
        }
        movieListModel.getMovieList(this, 1)
    }

    override fun onFinished(movieArrayList: List<Movie>) {
        movieListView!!.setDataToRecyclerView(movieArrayList)
        if (movieListView != null) {
            movieListView!!.hideProgress()
        }
    }

    override fun onFailure(t: Throwable) {

        movieListView!!.onResponseFailure(t)
        if (movieListView != null) {
            movieListView!!.hideProgress()
        }
    }
}