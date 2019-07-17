package us.gonet.appmoviesgithub.plataform

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_movie_filter.*
import us.gonet.appmoviesgithub.R
import us.gonet.appmoviesgithub.utils.Constants.KEY_RELEASE_FROM
import us.gonet.appmoviesgithub.utils.Constants.KEY_RELEASE_TO

@Suppress("DEPRECATION")
class MovieFilterActivity : AppCompatActivity() {

    private var tvToReleaseDate: TextView? = null
    private var tvClerAll: TextView? = null

    private var fromDate = ""
    private var toDate = ""

    private var rlMainLayout: RelativeLayout? = null

    /**
     * Handling the text change listeners on dates to hide/show clear all button.
     */
    private val twDates = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun afterTextChanged(editable: Editable) {

            if (fromDate.isNotEmpty() || toDate.isNotEmpty()) {
                tvClerAll!!.visibility = View.VISIBLE
            } else if (fromDate.isEmpty() && toDate.isEmpty()) {
                tvClerAll!!.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_filter)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        initUI()
        setListeners()

        val mIntent = intent
        fromDate = mIntent.getStringExtra(KEY_RELEASE_FROM)
        toDate = mIntent.getStringExtra(KEY_RELEASE_TO)

        // If the data from import is not empty then set it release from and release to values
        if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
            tv_from_date.text = fromDate
            tvToReleaseDate!!.text = toDate
        }
    }

    /**
     * This method will intialize the UI components
     */
    private fun initUI() {
        tvToReleaseDate = findViewById(R.id.tv_to_date)
        tvClerAll = findViewById(R.id.tv_clear_all)
        rlMainLayout = findViewById(R.id.rl_main_layout)
    }

    /**
     * This method will handle the listeners for the UI components
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setListeners() {

        tv_from_date.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    val date = "$year-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format("%02d", dayOfMonth)
                    fromDate = date
                    tv_from_date.text = date
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show(fragmentManager, "Datepickerdialog")
        }


        tvToReleaseDate!!.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                { _, year, monthOfYear, dayOfMonth ->
                    val date = "$year-" + String.format(
                        "%02d",
                        monthOfYear + 1
                    ) + "-" + String.format("%02d", dayOfMonth)
                    toDate = date
                    tvToReleaseDate!!.text = date
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show(fragmentManager, "Datepickerdialog")
        }

        tvClerAll!!.setOnClickListener {
            fromDate = ""
            toDate = ""
            tv_from_date.text = getString(R.string.from)
            tvToReleaseDate!!.text = getString(R.string.to)
        }

        tvToReleaseDate!!.addTextChangedListener(twDates)
        tv_from_date.addTextChangedListener(twDates)
    }

   override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_done -> {
                if (fromDate.isEmpty() && toDate.isNotEmpty() || fromDate.isNotEmpty() && toDate.isEmpty()) {
                    val snackbar = Snackbar
                        .make(rlMainLayout!!, getString(R.string.error_date_filter), Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return true
                }

                // Sending the return intent
                val returnIntent = Intent()
                returnIntent.putExtra(KEY_RELEASE_FROM, fromDate)
                returnIntent.putExtra(KEY_RELEASE_TO, toDate)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}