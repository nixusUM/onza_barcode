package com.onza.barcode.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.androidisland.ezpermission.EzPermission
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.NoProductDelegate
import com.onza.barcode.adapters.delegates.ProductDelegate
import com.onza.barcode.compare.CompareFragment
import com.onza.barcode.data.model.CompareImages
import com.onza.barcode.data.model.FavouritesResponse
import com.onza.barcode.data.model.Product
import com.onza.barcode.dialogs.addPrice.AddPriceDialog
import com.onza.barcode.dialogs.addProduct.AddProductDialog
import com.onza.barcode.dialogs.addReview.AddReviewDialog
import com.onza.barcode.history.HistoryFragment
import com.onza.barcode.product.detail.ProductDetaislFragment
import com.onza.barcode.product.list.ProductListFragment
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.dialog_add_product_to_list.*
import kotlinx.android.synthetic.main.dialog_price_submit.*
import kotlinx.android.synthetic.main.dialog_review_submit.*
import kotlinx.android.synthetic.main.fragment_barcode.*
import kotlinx.android.synthetic.main.item_product_logo.view.*
import kotlinx.android.synthetic.main.view_favourite_products.view.*
import kotlinx.android.synthetic.main.view_favourite_products.view.txt_more_Count
import kotlinx.android.synthetic.main.view_favourite_products.view.view_logo
import kotlinx.android.synthetic.main.view_product_compare.view.*
import kotlin.math.roundToInt

/**
 * Created by Ilia Polozov on 02/December/2019
 */

class BarCodeFragment: Fragment(), BarCodeView,
                       NoProductDelegate.ItemClick, ProductDelegate.AddPrice, ProductDelegate.onAddToFavourite,
                       ProductDelegate.ShareProduct, ProductDelegate.ProductCallback, ProductDelegate.ReviewCallback {

    private lateinit var codeScanner: CodeScanner

    private lateinit var dialog: Dialog
    private var gtin = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0
    private lateinit var selectedProduct: Product
    private var savedPosition = 0

    private lateinit var presenter: BarCodeFragmentPresenter
    private var eventListener: MiniAppCallback? = null
    private lateinit var prefs: SharedPreferences
    private var recyclerViewState: Parcelable? = null
    private var isScanning = false

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(NoProductDelegate(activity as Context, this@BarCodeFragment))
                addDelegate(ProductDelegate(activity as Context, this@BarCodeFragment,
                    this@BarCodeFragment, this@BarCodeFragment, this@BarCodeFragment, this@BarCodeFragment))
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!Utils.getScannedList().isNullOrEmpty()) {
            list_scanned.visibility = View.VISIBLE
            initScannedProductList(Utils.getScannedList())
        } else {
            initScannedProductList(mutableListOf())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context!!)
        if (!prefs.getBoolean("first", false)) {
            prefs.edit().putBoolean("first", true).apply()
            view_hint.visibility = View.VISIBLE
        } else {
            view_hint.visibility = View.GONE
        }

        if (fineLocationPermissionApproved()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            obtieneLocalizacion()
        } else {
            EzPermission.with(activity!!)
                .permissions(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .request { granted, denied, permanentlyDenied ->
                    if (granted.toString().isNotEmpty()) {
                        initLocation()
                    }
                    Log.i("granted", granted.toString())
                    Log.i("denied", denied.toString())
                    Log.i("permanentlyDenied", permanentlyDenied.toString())
                }
        }

        if (cameraPermissonsApproved()) {
            codeScanner = CodeScanner(activity!!, scanner_view)
            codeScanner.isFlashEnabled = false
            codeScanner.scanMode = ScanMode.CONTINUOUS
            codeScanner.isAutoFocusEnabled = true
            codeScanner.decodeCallback = DecodeCallback {
                activity!!.runOnUiThread {
                    if (gtin != it.text && !it.text.isNullOrEmpty() && !isScanning) {
                        isScanning = true
                        getProduct(it.text)
                    }
                }
            }
        } else {
            EzPermission.with(activity!!)
                .permissions(
                    Manifest.permission.CAMERA
                )
                .request { granted, denied, permanentlyDenied ->
                    if (granted.toString().isNotEmpty()) {
                        initCamera()
                    }
                }
        }

        presenter = BarCodeFragmentPresenter(activity!!, this)

        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
        frame_history.setOnClickListener {
            eventListener!!.pushFragment(HistoryFragment())
        }
        frame_list.setOnClickListener {
            eventListener!!.pushFragment(ProductListFragment())
        }
        img_close_hint.setOnClickListener {
            view_hint.visibility = View.GONE
        }

        hint_text.text = Html.fromHtml(getString(R.string.scan_hint))
    }

    private fun initCamera() {
        codeScanner = CodeScanner(activity!!, scanner_view)
        codeScanner.isFlashEnabled = false
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.decodeCallback = DecodeCallback {
            activity!!.runOnUiThread {
                if (gtin != it.text && !it.text.isNullOrEmpty()) {
                    getProduct(it.text)
                }
            }
        }
    }

    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        obtieneLocalizacion()
    }

    private fun fineLocationPermissionApproved(): Boolean {
        val context = context ?: return false
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun cameraPermissonsApproved(): Boolean {
        val context = context ?: return false
        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        )
    }

    fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true // must be granted after installed.
        return activity!!.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
            }
    }

    private fun getProduct(gtin: String) {
        progressBar.visibility = View.VISIBLE
        this.gtin = gtin
        obtieneLocalizacion()
        if (Utils().isInternetAvailable()) {
            presenter.getProductByBarCode(gtin, lat, lon)
        } else {
            showError(getString(R.string.no_connection_message))
        }
    }

    override fun showError(text: String?) {
        progressBar.visibility = View.GONE
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

    override fun addScannedProduct(product: Any, recognized: Boolean, compareImages: List<CompareImages>?) {
        isScanning = false
        sendProductEvent(product, recognized)
        if (!compareImages.isNullOrEmpty()) {
            initCompareListView(compareImages)
        } else {
            view_compare_list.visibility = View.GONE
        }
        textView_hint.text = "Продолжайте сканирование"
        progressBar.visibility = View.GONE
        if (list_scanned.visibility == View.GONE) {
            view_hint.visibility = View.GONE
            list_scanned.visibility = View.VISIBLE
        }
        var adapter = list_scanned.adapter as SimpleAdapter
        adapter.addItem(product, savedPosition)
        list_scanned.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun initCompareListView(compareImages: List<CompareImages>) {
        view_compare_list.findViewById<CardView>(R.id.cardView_favourite).removeAllViews()
        val productLogo = creteCompareListView(compareImages)
        view_compare_list.findViewById<CardView>(R.id.cardView_favourite).addView(productLogo)
        view_compare_list.visibility = View.VISIBLE
    }

    private fun creteCompareListView(images: List<CompareImages>): ConstraintLayout {
        val linflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lyt = linflater.inflate(R.layout.view_product_compare, null) as ConstraintLayout
        val params = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT)

        lyt.layoutParams = params

        var showMoreCount = false

        lyt.view_logo.removeAllViews()

        val marginNext = Utils().dpToPx(-8, context!!)

        for (i in images!!.indices) {
            if (i < 6) {
                var logoView = if (i == 0) {
                    createLogos(images[i].image, 0)
                } else {
                    createLogos(images[i].image, marginNext)
                }
                lyt.view_logo.addView(logoView)
            } else {
                showMoreCount = true
            }
        }

        if (showMoreCount) {
            lyt.txt_more_Count.visibility = View.VISIBLE
            lyt.txt_more_Count.text  = getString(R.string.more_count, images.size - 6)
        }

        lyt.cardView_to_compare.setOnClickListener {
            eventListener!!.pushFragment(CompareFragment())
        }

        return lyt
    }

    private fun createLogos(url: String?, margin: Int): ConstraintLayout {
        val linflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lyt = linflater.inflate(R.layout.item_product_logo, null) as ConstraintLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        params.setMargins(margin, 0, 0, 0)
        lyt.layoutParams = params

        lyt.product_logo.borderColor = ContextCompat.getColor(context!!, R.color.yellow)
        lyt.product_logo.borderWidth = 3
        if (!url.isNullOrEmpty()) {
            Glide.with(context!!)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_no_rpoduct_png)
                .into(lyt.product_logo)
        } else {
            lyt.product_logo.setCircleBackgroundColorResource(R.color.ef_white)
        }

        return lyt
    }

    private fun sendProductEvent(product: Any, recognized: Boolean) {
        if (recognized) {
            val recognizedProduct = product as Product
            eventListener!!.logEvent("ProductScan", "GoodsScan", "success_scan", recognizedProduct.id.toLong())
        } else {
            eventListener!!.logEvent("ProductScan", "GoodsScan", "not_found_scan", null)
        }
    }

    private fun initScannedProductList(list: List<Any>) {
        val myCallback = object: ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.DOWN) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var adapter = list_scanned.adapter as SimpleAdapter
                adapter.removeItem(viewHolder.adapterPosition)
                gtin = ""
            }
        }

        val layoutManager = LinearLayoutManager(activity,
            RecyclerView.HORIZONTAL,
            false)

        val myHelper = ItemTouchHelper(myCallback)
        myHelper.attachToRecyclerView(list_scanned)

        with(list_scanned) {
            adapter = SimpleAdapter(list, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            list_scanned.layoutManager!!.onRestoreInstanceState(recyclerViewState)
        }
        if (cameraPermissonsApproved()) {
            codeScanner.startPreview()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        var adapter = list_scanned.adapter as SimpleAdapter
        Utils.setSccanedList(adapter.getItemsList())
    }

    override fun onPause() {
        gtin = ""
        if (cameraPermissonsApproved()) {
            codeScanner.releaseResources()
        }
        recyclerViewState = list_scanned.layoutManager!!.onSaveInstanceState()!!
        super.onPause()
    }

    override fun onAddProductClicked(position: Int) {
        AddProductDialog.getInstance(position, gtin).show(this.childFragmentManager, "add product")
    }

    override fun onAddPriceClicked(selectedProduct: Product, position: Int) {
        AddPriceDialog.getInstance(selectedProduct, position).show(this.childFragmentManager, "add price")
    }

    override fun onProductClicked(selectedProduct: Product) {
        eventListener!!.pushFragment(ProductDetaislFragment.getInstance(selectedProduct))
    }

    override fun onShareProductClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddToFavouriteClicked(selectedProduct: Product) {
        showAddProductToListDialog(selectedProduct)
    }

    override fun onAddReviewClicked(selectedProduct: Product, position: Int) {
        AddReviewDialog.getInstance(selectedProduct, position).show(this.childFragmentManager, "add review")
    }

    private fun showAddProductToListDialog(selectedProduct: Product) {
        this.selectedProduct = selectedProduct
        var count = 1
        dialog = Dialog(activity!!, R.style.CustomAlertDialogStyle)
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        dialog.window.setBackgroundDrawableResource(R.color.dark_transparent)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setContentView(R.layout.dialog_add_product_to_list)
        dialog.setCancelable(true)

        dialog.name.text = selectedProduct.name
        dialog.location.text = selectedProduct.production_place
        initSendButton(1)

        var priceText = "Цен"
        if (selectedProduct.avg_price != null && selectedProduct.avg_price != 0f) {
            dialog.view_price.visibility = View.VISIBLE
            dialog.textView_price.text = "~ " + String.format("%.2f", selectedProduct.avg_price) + " Р"
            priceText = when (selectedProduct.amounts!!.prices) {
                1 -> " Цена"
                2 -> " Цены"
                3 -> " Цены"
                4 -> " Цены"
                else -> " Цен"
            }
            dialog.textView_price_count.text =
                selectedProduct.amounts!!.prices.toString() + priceText
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
                if (Utils().isInternetAvailable()) {
                    eventListener!!.logEvent("", "GoodsList", null, null)
                    presenter.addToFavourites(
                        selectedProduct.id,
                        this.dialog.product_count.text.toString().toInt()
                    )
                } else {
                    showError(getString(R.string.no_connection_message))
                }
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

    fun updateItem(gtin: String, position: Int) {
        var adapter = list_scanned.adapter as SimpleAdapter
        savedPosition = position
        adapter.removeItem(savedPosition)
        getProduct(gtin)
    }

    fun showAddedReviewDialog(gtin: String, position: Int) {
        val dialog = Dialog(activity!!, R.style.CustomAlertDialogStyle)
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        dialog.window.setBackgroundDrawableResource(R.color.dark_transparent)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setContentView(R.layout.dialog_review_submit)
        dialog.setCancelable(true)

        dialog.view_add_review.setOnClickListener {
            var adapter = list_scanned.adapter as SimpleAdapter
            savedPosition = position
            adapter.removeItem(savedPosition)
            getProduct(gtin)
            dialog.dismiss()
        }

        dialog.view_cancel_review.setOnClickListener {
            var adapter = list_scanned.adapter as SimpleAdapter
            savedPosition = position
            adapter.removeItem(savedPosition)
            getProduct(gtin)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showAddedPriceDialog(productPirce: Float, gtin: String, position: Int) {
        val dialog = Dialog(context, R.style.CustomAlertDialogStyle)
        dialog.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        dialog.window.setBackgroundDrawableResource(R.color.dark_transparent)
        dialog.window.attributes.windowAnimations = R.style.DialogAnimation

        dialog.setContentView(R.layout.dialog_price_submit)
        dialog.price_hint.text = String.format(getString(R.string.product_price_added, productPirce.roundToInt()))
        dialog.contraint_next.setOnClickListener {
            var adapter = list_scanned.adapter as SimpleAdapter
            savedPosition = position
            adapter.removeItem(savedPosition)
            getProduct(gtin)
            dialog.dismiss()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun showFavouriteView(products: List<FavouritesResponse>) {
        view_favourite_list.findViewById<CardView>(R.id.cardView_favourite).removeAllViews()
        val productLogo = createFavouriteView(products)
        view_favourite_list.findViewById<CardView>(R.id.cardView_favourite).addView(productLogo)
        view_favourite_list.visibility = View.VISIBLE
    }

    override fun showAttentionDialog(title: String, content: String) {
        progressBar.visibility = View.GONE
        isScanning = false
        val dialog = AlertDialog.Builder(activity!!)
            .setTitle(title)
            .setMessage(content)
            .setPositiveButton("OK") { dialog, _ ->
                this.gtin = ""
                dialog.dismiss()
            }
            .create()
        dialog.show()
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

        var showMoreCount = false

        lyt.total_amount.text = "~ " + String.format("%.2f", totalAmount) + " Р"
        lyt.view_logo.removeAllViews()

        val marginNext = Utils().dpToPx(-8, context!!)

        for (i in products.indices) {
            for (item in products[i].products) {
                if (i < 6) {
                    var logoView = if (i == 0) {
                        createLogos(item.product, 0)
                    } else {
                        createLogos(item.product, marginNext)
                    }
                    lyt.view_logo.addView(logoView)
                } else {
                    showMoreCount = true
                }
            }
        }

        if (showMoreCount) {
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
            lyt.product_logo.setCircleBackgroundColorResource(R.color.ef_white)
        }

        return lyt
    }

    companion object {

        const val PRODUCT = "product"

        fun getInstance(): Fragment = BarCodeFragment()
    }
}