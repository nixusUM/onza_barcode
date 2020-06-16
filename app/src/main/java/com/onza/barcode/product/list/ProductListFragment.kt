package com.onza.barcode.product.list

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.CategoryDelegate
import com.onza.barcode.adapters.delegates.ProductInListDelegate
import com.onza.barcode.data.model.FavouriteProducts
import com.onza.barcode.data.model.FavouritesResponse
import com.onza.barcode.data.model.Product
import com.onza.barcode.history.HistoryFragment
import com.onza.barcode.product.detail.ProductDetaislFragment
import com.onza.barcode.utils.RecyclerTouchListener
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.dialog_add_product_to_list.*
import kotlinx.android.synthetic.main.item_product_logo.view.*
import kotlinx.android.synthetic.main.view_favourite_products.view.*


/**
 * Created by Ilia Polozov on 29/January/2020
 */

class ProductListFragment: Fragment(), ProductListView, ProductInListDelegate.ItemClick {

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ProductInListDelegate(context!!, this@ProductListFragment))
                addDelegate(CategoryDelegate(context!!))
            }
    }

    private lateinit var presenter: ProductListActivityPresenter
    private lateinit var dialog: Dialog
    private lateinit var selectedProduct: Product
    private var eventListener: MiniAppCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProductListActivityPresenter(this, context!!, eventListener?.getToken())
        view_back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        if (locationPermissonsApproved()) {
            if (Utils().isInternetAvailable()) {
                presenter.getFavouriteList()
            } else {
                showError(getString(R.string.no_connection_message))
                showFavourites(ArrayList<Any>())
            }
        } else {
            showError("Не удалось определить вашу геопозицию, проверьте настройки приложения")
            progressBar.visibility = View.GONE
            if (view_no_items != null && view_no_items.visibility == View.VISIBLE) {
                view_no_items.visibility = View.GONE
            } else {
                view_no_items.visibility = View.VISIBLE
            }
            error_title.text = "Нет доступа к геопозиции"
            error_hint.text = "Необходимо перейти в настройки и разрещить\nприложению использовать геопозицию"
            text_navigate.text = "Открыть настройки"
            cardView_scan.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri: Uri = Uri.fromParts("package", activity!!.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun locationPermissonsApproved(): Boolean {
        val context = context ?: return false
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    override fun showFavourites(favouriteList: List<Any>) {
        if (progressBar == null) {
            return
        }

        progressBar.visibility = View.GONE
        if (favouriteList.isEmpty()) {
            view_no_items.visibility = View.VISIBLE
            return
        }

        list_favourites.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(context,
            RecyclerView.VERTICAL,
            false)

        with(list_favourites) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(favouriteList, adapterManager)
            setLayoutManager(layoutManager)
        }

        var touchListener = RecyclerTouchListener(activity!!, list_favourites)
        touchListener.setClickable(object : RecyclerTouchListener.OnRowClickListener {
            override fun onRowClicked(position: Int) {
//                showError("clickedRoww$position")
            }

            override fun onIndependentViewClicked(independentViewID: Int, position: Int) {

            }
        })

        touchListener.setSwipeOptionViews(R.id.delete_task,R.id.edit_task).setSwipeable(R.id.rowFG, R.id.rowBG
        ) { viewID, position ->
            when (viewID) {
                R.id.delete_task -> {
                    var favouriteProducts = favouriteList as MutableList<FavouriteProducts>
                    if (favouriteProducts[position] is FavouriteProducts) {
                    presenter.removeProduct(favouriteProducts[position].product.id, position)
                    }
                }
                R.id.edit_task -> {
                    var favouriteProducts = favouriteList as MutableList<FavouriteProducts>
                    if (favouriteProducts[position] is FavouriteProducts) {
                        showEditCountDialog(favouriteProducts[position].product, favouriteProducts[position].count)
                    }
                }
            }
        }

        list_favourites.addOnItemTouchListener(touchListener)
        cardView_scan.setOnClickListener {
            eventListener!!.pushFragment(HistoryFragment())
        }
    }

    private fun showEditCountDialog(selectedProduct: Product, favouriteProductCount: Int) {
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
//        dialog.product_count.text = count.toString()

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
                presenter.updateProduct(selectedProduct.id, this.dialog.product_count.text.toString().toInt())
                this.dialog.dismiss()
            }
        } else {
            dialog.view_add_product.setOnClickListener {}
            this.dialog.view_add_product.setBackgroundDrawable(
                ContextCompat.getDrawable(context!!, R.drawable.ic_price_disabled)
            )
            this.dialog.txt_add.setTextColor(ContextCompat.getColor(context!!, R.color.dark_transparent))
        }
    }

    override fun onProductClicked(product: Product) {
        eventListener!!.pushFragment(ProductDetaislFragment.getInstance(product))
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
        val lyt = linflater.inflate(R.layout.item_favourite_product_black, null) as ConstraintLayout
        val params = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT)

        lyt.layoutParams = params
        var totalAmount = 0F

        for (i in products.indices) {
            for (item in products[i].products) {
                if (item.product.avg_price != null) {
                    totalAmount += item.product.avg_price * item.count
                }
            }
        }

        var showMoreCount = false

        lyt.total_amount.setTextColor(ContextCompat.getColor(context!!, android.R.color.white))
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
            lyt.txt_more_Count.setTextColor(ContextCompat.getColor(context!!, android.R.color.white))
            lyt.txt_more_Count.visibility = View.VISIBLE
            lyt.txt_more_Count.text  = getString(R.string.more_count, products.size - 6)
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

        lyt.product_logo.borderColor = ContextCompat.getColor(context!!, android.R.color.black)
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

    override fun removedProduct(position: Int) {
        var adapter = list_favourites.adapter as SimpleAdapter
        adapter.removeItem(position)
        updatedProduct()
    }

    override fun updatedProduct() {
        presenter.getFavouriteList()
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

        fun getInstance(): Fragment {
            val fragment = ProductListFragment()
//            val bundle = Bundle()
//            fragment.arguments = bundle
            return fragment
        }
    }
}