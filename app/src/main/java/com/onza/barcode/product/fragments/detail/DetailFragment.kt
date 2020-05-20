package com.onza.barcode.product.fragments.detail

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.androidisland.ezpermission.EzPermission
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.ViewPagerPhotoAdapter
import com.onza.barcode.adapters.delegates.ReviewsDelegate
import com.onza.barcode.compare.CompareFragment
import com.onza.barcode.compare.products.CompareProdutsFragment
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Reviews
import com.onza.barcode.dialogs.addPrice.AddPriceDialog
import com.onza.barcode.dialogs.addReview.AddReviewDialog
import com.onza.barcode.prices.PricesFragment
import com.onza.barcode.product.fragments.AboutFragment
import com.onza.barcode.reviews.ReviewsFragment
import com.onza.barcode.utils.LinePagerIndicatorDecoration
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.dialog_add_product_to_list.*
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.fragment_product_detail.textView_name

/**
 * Created by Ilia Polozov on 28/January/2020
 */

class DetailFragment: Fragment(), DetailFragmentView, ReviewsDelegate.ItemClick {

    private lateinit var presenter: DetailFragmentPresenter

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ReviewsDelegate(activity as Context, this@DetailFragment))
            }
    }

    private lateinit var selectdProduct: Product
    private lateinit var dialog: Dialog
    private var eventListener: MiniAppCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = DetailFragmentPresenter(this, activity!!)
        val product = arguments!!.getSerializable(SLECTED_PRODUCT) as Product

        if (locationPermissonsApproved()) {

        } else {
            showError("Не удалось определить вашу геопозицию, проверьте настройки приложения")
        }

        if (Utils().isInternetAvailable()) {
            presenter.getProductById(product.id, 0.0, 0.0)
            presenter.onViewCreated(product.id)
        } else {
            showError(getString(R.string.no_connection_message))
        }

        view_back.setOnClickListener { activity!!.onBackPressed() }
    }

    private fun locationPermissonsApproved(): Boolean {
        val context = context ?: return false
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun initProductData(selectedProduct: Product) {
        this.selectdProduct = selectedProduct
        if (progressBar == null) {
            return
        }
        progressBar.visibility = View.GONE

        this.selectdProduct = selectedProduct

        var priceText = "Цена"
        view_show_about.setOnClickListener {
            eventListener!!.pushFragment(AboutFragment.getInstance(selectdProduct))
        }
        toolbar_title.text = selectdProduct.name
        textView_name.text = selectdProduct.name
        product_description.text = selectdProduct.name
        gtin_product.text = selectdProduct.gtin

        textView_compare.setOnClickListener {
            eventListener!!.pushFragment(CompareProdutsFragment.getInstance(selectdProduct.category!!.id, selectdProduct.category!!.name, selectdProduct.id))
        }

        if (selectdProduct.lists!!.in_compare) {
            txt_add_compare.visibility = View.GONE
            view_remove_from_compare.visibility = View.VISIBLE
        } else {
            txt_add_compare.visibility = View.VISIBLE
            view_remove_from_compare.visibility = View.GONE
        }

        view_review_count.setOnClickListener { eventListener!!.pushFragment(ReviewsFragment.getInstance(selectdProduct)) }
        product_rating.setOnClickListener { AddReviewDialog.getInstance(selectedProduct, 0).show(this.childFragmentManager, "add review")}
        cardView_post_review.setOnClickListener { AddReviewDialog.getInstance(selectedProduct, 0).show(this.childFragmentManager, "add review")}
        if (selectdProduct.permissions!!.has_added_price) {
            if (selectdProduct.avg_price != null && selectdProduct.avg_price != 0f) {
                view_price_detail.visibility = View.VISIBLE
                textView_price_detail.text =
                    "~ " + String.format("%.2f", selectdProduct.avg_price) + " Р"
                priceText = when (selectdProduct.amounts!!.prices) {
                    1 -> " Цена"
                    2 -> " Цены"
                    3 -> " Цены"
                    4 -> " Цены"
                    else -> " Цен"
                }
                textView_price_count_detail.text =
                    selectdProduct.amounts!!.prices.toString() + priceText
                view_price_detail.setOnClickListener {
                    eventListener!!.pushFragment(PricesFragment.getInstance(selectdProduct))
                }
            }
        } else {
            cardView_add_price.visibility = View.VISIBLE
            cardView_add_price.setOnClickListener {
                AddPriceDialog.getInstance(selectedProduct, 0).show(this.childFragmentManager, "add price")
            }
        }

        textView_rating_star.text = selectdProduct.rating.toString()
        textView_rates.text = selectdProduct.amounts!!.rates.toString()
        textView_reviews.text = selectdProduct.amounts!!.reviews.toString()

        if (!selectedProduct.source.isNullOrEmpty()) {
            txt_source.text = selectedProduct.source
            txt_source.visibility = View.VISIBLE
        }

        if (!selectdProduct.images.isNullOrEmpty()) {
            initPhotosViewPager()
        }

        view_add_favourites.setOnClickListener {
            showAddProductToListDialog()
        }

        txt_add_compare.setOnClickListener {
            eventListener!!.logEvent("AddProductComparison", "GoodsCompare", null, selectedProduct.id.toLong())
            presenter.addToCompareList(selectedProduct.id)
        }

        view_remove_from_compare.setOnClickListener {
            presenter.removeFromComareList(selectedProduct.id)
        }

        eventListener!!.logEvent("OpenProduct", "GoodsInfo", "goods_info", null)
    }

    private fun initPhotosViewPager() {
        val list: ArrayList<String> = ArrayList()
        if (selectdProduct.images != null) {
            image_product.visibility = View.GONE
            for (item in selectdProduct.images!!) {
                list.add(item.url)
            }

            viewPager.adapter = ViewPagerPhotoAdapter(this.childFragmentManager, list)
            viewPager.offscreenPageLimit = list.size
            tabLayout.setupWithViewPager(viewPager, true)
        }
    }

    fun refreshProductDetailData(id: Int, lat: Double, lon: Double) {
        presenter.getProductById(id, lat, lon)
        presenter.onViewCreated(selectdProduct.id)
    }

    override fun showReviwes(reviews: List<Reviews>, count: Int) {
        if (reviews.isNullOrEmpty()) {
            if (view_no_reviews != null && review_list != null) {
                view_no_reviews.visibility = View.VISIBLE
                review_list.visibility = View.GONE
            }
            return
        }

        if (count > 5) {
            textView_review_count.text = getString(R.string.more_reviews, count - 5)
        }

        review_list.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(activity,
            RecyclerView.HORIZONTAL,
            false)
        val snapHelper =  PagerSnapHelper()

        if (review_list.onFlingListener == null) {
            snapHelper.attachToRecyclerView(review_list)
        }

        review_list.addItemDecoration(LinePagerIndicatorDecoration())

        with(review_list) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(reviews, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    private fun showAddProductToListDialog() {
        var count = 1
        dialog = Dialog(activity!!, R.style.CustomAlertDialogStyle)
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        dialog.window.setBackgroundDrawableResource(R.color.dark_transparent)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setContentView(R.layout.dialog_add_product_to_list)
        dialog.setCancelable(true)

        dialog.name.text = selectdProduct.name
        dialog.location.text = selectdProduct.production_place
        initSendButton(1)

        if (selectdProduct.avg_price != null && selectdProduct.avg_price != 0f) {
            dialog.view_price.visibility = View.VISIBLE
            dialog.textView_price.text = "~ " + selectdProduct.avg_price.toString() + " Р"
        }

        dialog.view_cancel_product.setOnClickListener {
            dialog.dismiss()
        }

        dialog.product_plus.setOnClickListener {
            initSendButton(1)
            dialog.product_count.text = count++.toString()
        }

        dialog.product_minus.setOnClickListener {
            var productCount = dialog.product_count.text.toString().toInt()
            if (productCount != 0) {
                if (productCount == 1) {
                    count = 1
                    initSendButton(0)
                }
                dialog.product_count.text = (productCount - 1).toString()
            }
        }

        dialog.show()

    }

    private fun initSendButton(count: Int) {
        if (count > 0) {
            this.dialog.view_add_product.setBackgroundDrawable(
                ContextCompat.getDrawable(activity!!, R.drawable.ic_price_enebled)
            )
            this.dialog.txt_add.setTextColor(ContextCompat.getColor(activity!!, android.R.color.black))
            dialog.view_add_product.setOnClickListener {
                eventListener!!.logEvent("", "GoodsList", null, null)
                presenter.addToFavourites(selectdProduct.id, this.dialog.product_count.text.toString().toInt())
                this.dialog.dismiss()
            }
        } else {
            dialog.view_add_product.setOnClickListener {}
            this.dialog.view_add_product.setBackgroundDrawable(
                ContextCompat.getDrawable(activity!!, R.drawable.ic_price_disabled)
            )
            this.dialog.txt_add.setTextColor(ContextCompat.getColor(activity!!, R.color.dark_transparent))
        }
    }

    override fun onReviewClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(text: String?) {
        if (progressBar != null) {
            progressBar.visibility = View.GONE
        }
        if (text != null) {
            Toast.makeText(activity!!, text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity!!, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        const val SLECTED_PRODUCT = "selected_product"

        fun getInstance(selectedProduct: Product): Fragment {
            val fragment = DetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(SLECTED_PRODUCT, selectedProduct)
            fragment.arguments = bundle
            return fragment
        }
    }
}