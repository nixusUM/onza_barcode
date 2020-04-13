package com.onza.barcode.reviews

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.ReviewsInListDelegate
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Reviews
import com.onza.barcode.dialogs.addReview.AddReviewDialog
import com.onza.barcode.utils.RecyclerTouchListener
import kotlinx.android.synthetic.main.activity_review.*

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class ReviewsFragment: Fragment(), ReviewsView, ReviewsInListDelegate.ItemClick {

    private lateinit var presenter: ReviewsActivityPresenter
    private lateinit var selectdProduct: Product
    private var paginationPge = false
    private var page = 1
    private var lastFirstVisiblePosition = 0

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ReviewsInListDelegate(context!!, this@ReviewsFragment))
            }
    }

    private var eventListener: MiniAppCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_review, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = ReviewsActivityPresenter(this, context!!)
        selectdProduct = arguments!!.getSerializable(SELECTED_PRODUCT) as Product
        view_back.setOnClickListener { activity!!.onBackPressed() }
        contraint_add_review.setOnClickListener {
            AddReviewDialog.getInstance(selectdProduct, 0).show(this.childFragmentManager, "add review")
        }
        cardView_review.setOnClickListener {
            AddReviewDialog.getInstance(selectdProduct, 0).show(this.childFragmentManager, "add review")
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getReviews(selectdProduct.id, page)

    }

    fun showError(text: String?) {
        if(text != null) {
            Snackbar
                .make(rootView, text, Snackbar.LENGTH_SHORT)
                .show()
        }
        else {
            Snackbar
                .make(rootView, R.string.default_error, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    fun updateReviews() {
        presenter.getReviews(selectdProduct.id, page)
    }

    override fun showReviews(reviewsList: List<Reviews>) {
        if (progressBar == null) {
            return
        }

        progressBar.visibility = View.GONE
        progressBar_endless.visibility = View.GONE
        paginationPge = false

        if (reviewsList.isNullOrEmpty()) {
            view_reviews_placeholder.visibility = View.VISIBLE
            return
        }

        list_reviews.visibility = View.VISIBLE
        contraint_add_review.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(context,
            RecyclerView.VERTICAL,
            false)

        with(list_reviews) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(reviewsList, adapterManager)
            setLayoutManager(layoutManager)
        }

        var touchListener = RecyclerTouchListener(activity!!, list_reviews)
        touchListener.setClickable(object : RecyclerTouchListener.OnRowClickListener {
            override fun onRowClicked(position: Int) {
//                showError("clickedRoww$position")
            }

            override fun onIndependentViewClicked(independentViewID: Int, position: Int) {

            }
        })

        touchListener.setSwipeOptionViews(R.id.delete_task).setSwipeable(R.id.rowFG, R.id.rowBG
        ) { viewID, position ->
            when (viewID) {
                R.id.delete_task -> {
                    presenter.removeReview(reviewsList[position].id, position)
                }
            }
        }

        list_reviews.addOnScrollListener(object :RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && reviewsList.size >= 20 &&  !paginationPge) {
                        lastFirstVisiblePosition = layoutManager.findLastVisibleItemPosition()
                        progressBar_endless.visibility = View.VISIBLE
                        paginationPge = true
                        presenter.getReviews(selectdProduct.id, page + 1)
                    }
                }
            })

        if (lastFirstVisiblePosition != 0) {
            layoutManager.scrollToPosition(lastFirstVisiblePosition)
        }

        list_reviews.addOnItemTouchListener(touchListener)
    }

    override fun showMessage(text: String) {
        if (progressBar != null) {
            progressBar.visibility = View.GONE
        }
        showError(text)
    }

    override fun removeReivew(positon: Int) {
        var adapter = list_reviews.adapter as SimpleAdapter
        adapter.removeItem(positon)
    }

    private fun onAddReviewClicked() {
        presenter.onAddReviewClicked(selectdProduct)
    }

    override fun onItemClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun changeFragment(fragment: Fragment,
                       tag: String,
                       addToBackStack: Boolean = false) {
        container.visibility = View.VISIBLE
        val transaction = childFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, tag)
        if (addToBackStack) transaction.addToBackStack(tag)
        transaction.commitAllowingStateLoss()
    }

    companion object {

        const val SELECTED_PRODUCT = "selected_product"

        fun getInstance(selectedProduct: Product): Fragment {
            val fragment = ReviewsFragment()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT, selectedProduct)
            fragment.arguments = bundle
            return fragment
        }
    }
}
