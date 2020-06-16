package com.onza.barcode.shop

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.AllShopsDelegate
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Shop
import com.onza.barcode.dialogs.addPrice.AddPriceDialog
import com.onza.barcode.dialogs.addProduct.AddProductDialog
import com.onza.barcode.dialogs.addReview.AddReviewDialog
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.activity_shop.*

/**
 * Created by Ilia Polozov on 06/December/2019
 */

class ShopActivity: BottomSheetDialogFragment(), ShopView, AllShopsDelegate.ItemClick {

    private var selectdProduct: Product? = null
    private var page = 1
    private var lastFirstVisiblePosition = 0
    private var paginationPge = false
    private lateinit var textWather: TextWatcher
    private var query = ""
    private var shopListResult = ArrayList<Shop>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0

    private lateinit var presenter: ShopActivityPresenter
    private var eventListener: MiniAppCallback? = null

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(AllShopsDelegate(context!!, this@ShopActivity))
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val sheetInternal: View = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        val view = inflater.inflate(R.layout.activity_shop, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ShopActivityPresenter(this, context!!, eventListener?.getToken())

        if (arguments!!.getSerializable(SELECTED_PRODUCT) != null) {
            selectdProduct = arguments!!.getSerializable(SELECTED_PRODUCT) as Product
        }
        view_back.setOnClickListener {

        }

        icon_clear.setOnClickListener {
            if (list_shops.adapter != null) {
                edt_search.setText("")
                query = ""
                obtieneLocalizacion()
            }
        }

        textWather = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (list_shops.adapter != null) {
                    if (p0!!.length > 2) {
                        query = p0.toString()
                        obtieneLocalizacion()
                    } else {
                        query = ""
                        obtieneLocalizacion()
                    }
                }
            }
        }

        edt_search.addTextChangedListener(textWather)
    }

    override fun onResume() {
        super.onResume()
        if (locationPermissonsApproved()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
            obtieneLocalizacion()
        } else {
            progressBar.visibility = View.GONE
            showNoResultPlaceHolder(true)
        }
//        if (selectdProduct == null) {
//            obtieneLocalizacion()
//        } else {
////            initShopesAdapter()
//        }
    }

    private fun locationPermissonsApproved(): Boolean {
        val context = context ?: return false
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        edt_search.removeTextChangedListener(textWather)
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
        progressBar.visibility = View.VISIBLE
        list_shops.visibility = View.GONE
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
                if (Utils().isInternetAvailable()) {
                    presenter.getNeearShops(lat, lon, query,1)
                } else {
                    showError(getString(R.string.no_connection_message))
                    showShopList(ArrayList<Shop>())
                }
            }
    }

    private fun initShopesAdapter() {
        progressBar.visibility = View.GONE
        if (selectdProduct?.shops != null) {
            val layoutManager = LinearLayoutManager(context,
                RecyclerView.VERTICAL,
                false)

            with(list_shops) {
                adapter = SimpleAdapter(selectdProduct!!.shops!!, adapterManager)
                setLayoutManager(layoutManager)
            }

        } else {
            view_no_result.visibility = View.VISIBLE
        }

    }

    override fun onShopClicked(shop: Shop, position: Int) {
        if (getParentFragment() is AddProductDialog) {
            val parentFragment = parentFragment as AddProductDialog
            parentFragment.selectShop(shop.name, shop.branch.id)
            dismiss()
        }

        if (getParentFragment() is AddPriceDialog) {
            val parentFragment = parentFragment as AddPriceDialog
            parentFragment.shopSelected(shop, position)
            dismiss()
        }

        if (getParentFragment() is AddReviewDialog) {
            val parentFragment = parentFragment as AddReviewDialog
            parentFragment.shopSelected(shop, position)
            dismiss()
        }
    }

    fun showError(text: String?) {
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

    private fun showNoResultPlaceHolder(show: Boolean){
        if (show) {
            view_no_result.visibility = View.VISIBLE
            list_shops.visibility = View.GONE
        } else {
            view_no_result.visibility = View.GONE
            list_shops.visibility = View.VISIBLE
        }
    }

    override fun showShopList(shopList: List<Shop>) {
        if (shopList.isEmpty()) {
            this.shopListResult.clear()
            view_no_result.visibility = View.VISIBLE
            list_shops.visibility = View.GONE
            return
        }

        list_shops.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        progressBar_endless.visibility = View.GONE
        paginationPge = false

        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        this.shopListResult.clear()
        this.shopListResult.addAll(shopList)

        with(list_shops) {
            adapter = SimpleAdapter(shopListResult, adapterManager)
            setLayoutManager(layoutManager)
        }

        list_shops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !paginationPge) {
                    lastFirstVisiblePosition = layoutManager.findLastVisibleItemPosition()
                    progressBar_endless.visibility = View.VISIBLE
                    paginationPge = true
                    presenter.getNeearShops(lat, lon, query, page + 1)
                }
            }
        })

        if (lastFirstVisiblePosition != 0) {
            layoutManager.scrollToPosition(lastFirstVisiblePosition)
        }
    }

    override fun showMessage(text: String?) {
        showError(text)
    }

    companion object {

        const val SELECTED_PRODUCT = "selected_product"

        fun getInstance(selectedProduct: Product?): BottomSheetDialogFragment {
            val fragment = ShopActivity()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT, selectedProduct)
            fragment.arguments = bundle
            return fragment
        }

    }
}