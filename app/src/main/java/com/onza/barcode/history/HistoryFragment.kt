package com.onza.barcode.history

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.DateSplitterDelegate
import com.onza.barcode.adapters.delegates.HistoryDelegate
import com.onza.barcode.data.model.FavouritesResponse
import com.onza.barcode.data.model.Product
import com.onza.barcode.product.detail.ProductDetaislFragment
import com.onza.barcode.product.list.ProductListFragment
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.dialog_add_product_to_list.*
import kotlinx.android.synthetic.main.item_product_logo.view.*
import kotlinx.android.synthetic.main.view_favourite_products.view.*

/**
 * Created by Ilia Polozov on 19/January/2020
 */

class HistoryFragment: Fragment(), HistoryView, HistoryDelegate.ItemClick, HistoryDelegate.ProductClick {

    private lateinit var presenter: HistoryActivityPresenter

    private lateinit var dialog: Dialog
    private lateinit var selectedProduct: Product
    private var eventListener: MiniAppCallback? = null
    private var paginationPge = false
    private var page = 1
    private var lastFirstVisiblePosition = 0

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(HistoryDelegate(context!!, this@HistoryFragment, this@HistoryFragment))
                addDelegate(DateSplitterDelegate(context!!))
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = HistoryActivityPresenter(this, context!!)
        cardView_scan.setOnClickListener { activity!!.onBackPressed() }
        view_back.setOnClickListener { activity!!.onBackPressed() }
        if (Utils().isInternetAvailable()) {
            presenter.getScanHistory(1)
        } else {
            showError(getString(R.string.no_connection_message))
            showProductHistory(ArrayList())
        }

        eventListener!!.logEvent("AddProHistoryProductScanduct", "GoodsHistory", null,  null)
    }

    override fun onAddToFavourite(selectedProduct: Product) {
        showAddProductToListDialog(selectedProduct)
    }

    private fun showAddProductToListDialog(selectedProduct: Product) {
        this.selectedProduct = selectedProduct
        var count = 1
        dialog = Dialog(context, R.style.CustomAlertDialogStyle)
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        dialog.window.setBackgroundDrawableResource(R.color.dark_transparent)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setContentView(R.layout.dialog_add_product_to_list)
        dialog.setCancelable(true)

        dialog.name.text = selectedProduct.name
        dialog.location.text = selectedProduct.production_place
        initSendButton(1)

        if (selectedProduct.avg_price != null && selectedProduct.avg_price != 0f) {
            dialog.view_price.visibility = View.VISIBLE
            dialog.textView_price.text = "~ " + selectedProduct.avg_price.toString() + " Р"
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
                ContextCompat.getDrawable(context!!, R.drawable.ic_price_enebled)
            )
            this.dialog.txt_add.setTextColor(ContextCompat.getColor(context!!, android.R.color.black))
            dialog.view_add_product.setOnClickListener {
                eventListener!!.logEvent("", "GoodsList", null, null)
                if (Utils().isInternetAvailable()) {
                    presenter.addToFavourites(selectedProduct.id, this.dialog.product_count.text.toString().toInt())
                } else {
                    showError(getString(R.string.no_connection_message))
                }
                this.dialog.dismiss()
            }
        } else {
            dialog.view_add_product.setOnClickListener {
            }
            this.dialog.view_add_product.setBackgroundDrawable(
                ContextCompat.getDrawable(context!!, R.drawable.ic_price_disabled)
            )
            this.dialog.txt_add.setTextColor(ContextCompat.getColor(context!!, R.color.dark_transparent))
        }
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

    companion object {

        fun getStarterIntent(context: Context): Intent {
            return Intent(context, HistoryFragment::class.java)
        }
    }

    override fun showProductHistory(historyList: List<Any>) {
        if (progressBar == null) {
            return
        }
        progressBar.visibility = View.GONE
        progressBar_endless.visibility = View.GONE
        paginationPge = false

        if (historyList.isEmpty()) {
            view_no_items.visibility = View.VISIBLE
            return
        }

        var layoutManager = GridLayoutManager(context, 2)
        list_history.visibility = View.VISIBLE

        if (historyList.size == 1) {
            layoutManager = GridLayoutManager(context, 1)
        }

        with(list_history) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(historyList, adapterManager)
            setLayoutManager(layoutManager)
        }

        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when ((list_history.adapter as SimpleAdapter).getItemViewType(position)) {
                    0 -> 1
                    else -> 2
                }
            }
        }

        list_history.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && historyList.size >= 20 && !paginationPge) {
                    lastFirstVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    progressBar_endless.visibility = View.VISIBLE
                    paginationPge = true
                    presenter.getScanHistory(page + 1)
                }
            }
        })

        if (lastFirstVisiblePosition != 0) {
            layoutManager.scrollToPosition(lastFirstVisiblePosition)
        }

    }

    override fun showMessage(text: String) {
        progressBar.visibility = View.GONE
        showError(text)
    }

    override fun showFavouriteView(products: List<FavouritesResponse>) {
        view_favourite_list.findViewById<CardView>(R.id.cardView_favourite).removeAllViews()
        val productLogo = createFavouriteView(products)
        view_favourite_list.findViewById<CardView>(R.id.cardView_favourite).addView(productLogo)
        view_favourite_list.visibility = View.VISIBLE
    }

    private fun createFavouriteView(products: List<FavouritesResponse>): ConstraintLayout {
        val linflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lyt = linflater.inflate(R.layout.view_favourite_products, null) as ConstraintLayout
        val params = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT)

        lyt.layoutParams = params
        var totalAmount = 0F

        for (i in products.indices) {
            for (item in products[i].products) {
                if (item.product.avg_price != null) {
                    totalAmount += item.product.avg_price
                }
            }
        }

        lyt.total_amount.text = "~ " + String.format("%.2f", totalAmount) + " Р"
        lyt.view_logo.removeAllViews()

        val marginNext = Utils().dpToPx(-8, context!!)

        for (i in products.indices) {
            if (i < 6) {
                var logoView = if (i == 0) {
                    createLogos(products[i].products[0].product, 0)
                } else {
                    createLogos(products[i].products[0].product, marginNext)
                }
                lyt.view_logo.addView(logoView)
            }
        }

        if (products.size > 6) {
            lyt.txt_more_Count.visibility = View.VISIBLE
            lyt.txt_more_Count.text  = getString(R.string.more_count, products.size - 6)
        }

        lyt.cardView_to_favourite.setOnClickListener {
            eventListener!!.pushFragment(ProductListFragment())
        }

        return lyt
    }

    private fun createLogos(product: Product, margin: Int): ConstraintLayout {
        val linflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lyt = linflater.inflate(R.layout.item_product_logo, null) as ConstraintLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(margin, 0, 0, 0)
        lyt.layoutParams = params

        lyt.product_logo.borderColor = ContextCompat.getColor(context!!, R.color.yellow)
        lyt.product_logo.borderWidth = 3
        if (!product.images.isNullOrEmpty()) {
            Glide.with(context!!)
                .load(product.images[0].url)
                .centerCrop()
                .placeholder(R.drawable.ic_no_rpoduct_png)
                .into(lyt.product_logo)
        } else {
            lyt.product_logo.setCircleBackgroundColorResource(android.R.color.white)
        }

        return lyt
    }

    override fun onProductClicked(selectedProduct: Product) {
        eventListener!!.pushFragment(ProductDetaislFragment.getInstance(selectedProduct))
    }
}
