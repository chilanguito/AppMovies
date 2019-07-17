package us.gonet.appmoviesgithub.movie_details

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.content_movie_details.*
import us.gonet.appmoviesgithub.R
import us.gonet.appmoviesgithub.adapter.CastAdapter
import us.gonet.appmoviesgithub.model.ApiClient
import us.gonet.appmoviesgithub.model.Cast
import us.gonet.appmoviesgithub.model.Movie
import us.gonet.appmoviesgithub.utils.Constants.KEY_MOVIE_ID

class MovieDetailsActivity : AppCompatActivity(), MovieDetailsContract.View {

    private var ivBackdrop: ImageView? = null
    private var pbLoadBackdrop: ProgressBar? = null
    private var tvMovieTitle: TextView? = null
    private var tvMovieReleaseDate: TextView? = null
    private var tvMovieRatings: TextView? = null
    private var tvOverview: TextView? = null
    private var castAdapter: CastAdapter? = null
    private var castList: MutableList<Cast>? = null
    private var pbLoadCast: ProgressBar? = null
    private var tvHomepageValue: TextView? = null
    private var tvTaglineValue: TextView? = null
    private var tvRuntimeValue: TextView? = null

    private var movieName: String? = null

    private var movieDetailsPresenter: MovieDetailsPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        initCollapsingToolbar()

        initUI()

        val mIntent = intent
        val movieId = mIntent.getIntExtra(KEY_MOVIE_ID, 0)

        movieDetailsPresenter = MovieDetailsPresenter(this)
        movieDetailsPresenter!!.requestMovieData(movieId)

    }

    /**
     * Initializing UI components
     */
    private fun initUI() {

        ivBackdrop = findViewById(R.id.iv_backdrop)
        pbLoadBackdrop = findViewById(R.id.pb_load_backdrop)
        tvMovieTitle = findViewById(R.id.tv_movie_title)
        tvMovieReleaseDate = findViewById(R.id.tv_release_date)
        tvMovieRatings = findViewById(R.id.tv_movie_ratings)
        tvOverview = findViewById(R.id.tv_movie_overview)

        castList = ArrayList()
        val rvCast = rv_cast
        castAdapter = CastAdapter(this, castList!!)
        rvCast.adapter = castAdapter
        pbLoadCast = findViewById(R.id.pb_cast_loading)

        tvHomepageValue = findViewById(R.id.tv_homepage_value)
        tvTaglineValue = findViewById(R.id.tv_tagline_value)
        tvRuntimeValue = findViewById(R.id.tv_runtime_value)
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private fun initCollapsingToolbar() {
        val collapsingToolbar = collapsing_toolbar
        collapsingToolbar.title = " "

        val appBarLayout = appbar
        appBarLayout.setExpanded(true)

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = movieName
                    isShow = true
                } else if (isShow) {
                    collapsingToolbar.title = " "
                    isShow = false
                }
            }
        })
    }

    override fun showProgress() {
        pbLoadBackdrop!!.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        pbLoadCast!!.visibility = View.GONE
    }

    override fun setDataToViews(movie: Movie?) {

        if (movie != null) {

            movieName = movie.title
            tvMovieTitle!!.text = movie.title
            tvMovieReleaseDate!!.text = movie.releaseDate
            tvMovieRatings!!.text = movie.rating.toString()
            tvOverview!!.text = movie.overview

            // loading album cover using Glide library
            Glide.with(this)
                .load(ApiClient.BACKDROP_BASE_URL + movie.backdropPath)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbLoadBackdrop!!.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        pbLoadBackdrop!!.visibility = View.GONE
                        return false
                    }
                })
                .apply(RequestOptions().placeholder(R.drawable.ic_place_holder).error(R.drawable.ic_place_holder))
                .into(ivBackdrop!!)

            castList!!.clear()
            castList!!.addAll(movie.credits?.cast!!)
            castAdapter!!.notifyDataSetChanged()

            tvTaglineValue!!.text = if (movie.tagline != null) movie.tagline else "N/A"
            tvHomepageValue!!.text = if (movie.homepage != null) movie.homepage else "N/A"
            tvRuntimeValue!!.text = if (movie.runTime != null) movie.runTime else "N/A"
        }

    }

    override fun onResponseFailure(throwable: Throwable) {

        Snackbar.make(findViewById(R.id.main_content), getString(R.string.error_data), Snackbar.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        movieDetailsPresenter!!.onDestroy()
    }
}