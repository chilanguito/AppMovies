package us.gonet.appmoviesgithub.plataform

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.view.View.GONE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import us.gonet.appmoviesgithub.R
import us.gonet.appmoviesgithub.adapter.MoviesAdapter
import us.gonet.appmoviesgithub.model.Movie
import us.gonet.appmoviesgithub.movie_details.MovieDetailsActivity
import us.gonet.appmoviesgithub.movie_list.MovieListPresenter
import us.gonet.appmoviesgithub.presentation.MovieListContract
import us.gonet.appmoviesgithub.utils.Constants.ACTION_MOVIE_FILTER
import us.gonet.appmoviesgithub.utils.Constants.KEY_MOVIE_ID
import us.gonet.appmoviesgithub.utils.Constants.KEY_RELEASE_FROM
import us.gonet.appmoviesgithub.utils.Constants.KEY_RELEASE_TO
import us.gonet.appmoviesgithub.utils.GridSpacingItemDecoration
import us.gonet.appmoviesgithub.utils.GridSpacingItemDecoration.Companion.dpToPx

@Suppress("DEPRECATED_IDENTITY_EQUALS")
@SuppressLint("Registered")
class MovieListActivity : AppCompatActivity(), MovieListContract.View, MovieListContract.MovieItemClickListener,
    MovieListContract.ShowEmptyView {

    private var movieListPresenter: MovieListPresenter? = null
    private var rvMovieList: RecyclerView? = null
    private var moviesList: MutableList<Movie>? = null
    private var moviesAdapter: MoviesAdapter? = null
    private var pbLoading: ProgressBar? = null
    private var fabFilter: FloatingActionButton? = null
    private var tvEmptyView: TextView? = null

    private var pageNo = 1

    //Constants for load more
    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 5
    internal var firstVisibleItem: Int = 0
    internal var visibleItemCount: Int = 0
    internal var totalItemCount: Int = 0
    private var mLayoutManager: GridLayoutManager? = null

    // Constants for filter functionality
    private var fromReleaseFilter = ""
    private var toReleaseFilter = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        supportActionBar!!.title = getString(R.string.most_popular_movies)
        initUI()

        setListeners()

        //Initializing presenter
        movieListPresenter = MovieListPresenter(this)

        movieListPresenter!!.requestDataFromServer()
    }

    /**
     * This method will initialize the UI components
     */
    private fun initUI() {

        rvMovieList = findViewById(R.id.rv_movie_list)

        moviesList = ArrayList()
        moviesAdapter = MoviesAdapter(this, moviesList)

        mLayoutManager = GridLayoutManager(this, 2)
        rvMovieList!!.layoutManager = mLayoutManager
        rvMovieList!!.addItemDecoration(GridSpacingItemDecoration(2, dpToPx(this, 10), true))
        rvMovieList!!.itemAnimator = DefaultItemAnimator()
        rvMovieList!!.adapter = moviesAdapter

        pbLoading = findViewById(R.id.pb_loading)

        fabFilter = findViewById(R.id.fab_filter)

        tvEmptyView = findViewById(R.id.tv_empty_view)
    }

    /**
     * This function will contain listeners for all views.
     */
    private fun setListeners() {

        rvMovieList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

           override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = rvMovieList!!.childCount
                totalItemCount = mLayoutManager!!.itemCount
                firstVisibleItem = mLayoutManager!!.findFirstVisibleItemPosition()

                // Handling the infinite scroll
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                    movieListPresenter!!.getMoreData(pageNo)
                    loading = true
                }

                // Hide and show Filter button
                if (dy > 0 && fabFilter!!.visibility === View.VISIBLE) {
                    fabFilter!!.hide()
                } else if (dy < 0 && fabFilter!!.visibility !== View.VISIBLE) {
                    fabFilter!!.show()
                }
            }
        })

        fabFilter!!.setOnClickListener {
            // Going to filter screen
            val movieFilterIntent = Intent(this@MovieListActivity, MovieFilterActivity::class.java)
            movieFilterIntent.putExtra(KEY_RELEASE_FROM, fromReleaseFilter)
            movieFilterIntent.putExtra(KEY_RELEASE_TO, toReleaseFilter)
            startActivityForResult(movieFilterIntent, ACTION_MOVIE_FILTER)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == ACTION_MOVIE_FILTER) {
            if (resultCode == Activity.RESULT_OK) {
                // Checking if there is any data to filter
                fromReleaseFilter = data!!.getStringExtra(KEY_RELEASE_FROM)
                toReleaseFilter = data.getStringExtra(KEY_RELEASE_TO)

                moviesAdapter!!.setFilterParameter(fromReleaseFilter, toReleaseFilter)
                moviesAdapter!!.filter.filter("")
            }
        }
    }

    override fun showProgress() {
        pbLoading!!.visibility = View.VISIBLE
    }

    override fun hideProgress() {

        pbLoading!!.visibility = GONE
    }

    override fun setDataToRecyclerView(movieArrayList: List<Movie>) {

        moviesList!!.addAll(movieArrayList)
        moviesAdapter!!.notifyDataSetChanged()

        // This will help us to fetch data from next page no.
        pageNo++
    }


    override fun onResponseFailure(throwable: Throwable) {

        Log.e(TAG, throwable.message)
        Toast.makeText(this, getString(R.string.communication_error), Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        movieListPresenter!!.onDestroy()
    }

    override fun onMovieItemClick(position: Int) {

        if (position == -1) {
            return
        }
        val detailIntent = Intent(this, MovieDetailsActivity::class.java)
        detailIntent.putExtra(KEY_MOVIE_ID, moviesList!![position].id)
        startActivity(detailIntent)
    }

   override fun showEmptyView() {
       rvMovieList!!.visibility =  GONE
        tvEmptyView!!.visibility = View.VISIBLE

    }

   override fun hideEmptyView() {
       rvMovieList!!.visibility = View.VISIBLE
        tvEmptyView!!.visibility = GONE
    }

    companion object {
        private const val TAG = "MovieListActivity"
    }
}