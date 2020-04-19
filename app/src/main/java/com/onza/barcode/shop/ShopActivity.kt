package com.onza.barcode.shop

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.onza.barcode.dialogs.addProduct.AddProductDialog
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.activity_shop.*

/**
 * Created by Ilia Polozov on 06/December/2019
 */

class ShopActivity: BottomSheetDialogFragment(), ShopView, AllShopsDelegate.ItemClick {

    private var selectdProduct: Product? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0

    private lateinit var presenter: ShopActivityPresenter

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(AllShopsDelegate(context!!, this@ShopActivity))
            }
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
        presenter = ShopActivityPresenter(this, context!!)

        if (arguments!!.getSerializable(SELECTED_PRODUCT) != null) {
            selectdProduct = arguments!!.getSerializable(SELECTED_PRODUCT) as Product
        }
        view_back.setOnClickListener {

        }

        edt_search.addTextChangedListener((object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var adapter = list_shops.adapter as SimpleAdapter
                if (p0!!.length > 2) {
                    adapter.filterShops(p0.toString())
                    showNoResultPlaceHolder(adapter.items.isEmpty())
                } else {
                    adapter.filterShops("")
                    showNoResultPlaceHolder(false)
                }
            }
        }))
    }

    override fun onResume() {
        super.onResume()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        obtieneLocalizacion()
        if (selectdProduct == null) {
            obtieneLocalizacion()
        } else {
            initShopesAdapter()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
                if (Utils().isInternetAvailable()) {
                    presenter.getNeearShops(lat, lon)
                } else {
                    showError(getString(R.string.no_connection_message))
                    showShopList(ArrayList<Shop>())
                }
            }
    }

    private fun initShopesAdapter() {
        progressBar.visibility = View.GONE
        if (selectdProduct?.shops != null) {
            val layoutManager = LinearLayoutManager(
                context!!,
                RecyclerView.VERTICAL,
                false
            )

            with(list_shops) {
                adapter = SimpleAdapter(selectdProduct!!.shops!!, adapterManager)
                setLayoutManager(layoutManager)
            }
        } else {
            view_no_result.visibility = View.VISIBLE
        }
    }

    override fun onShopClicked(name: String, id: Int) {
        val parentFragment = parentFragment as AddProductDialog
        parentFragment.selectShop(name, id)
        dismiss()
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
        progressBar.visibility = View.GONE
        if (shopList.isEmpty()) {
            view_no_result.visibility = View.VISIBLE
            list_shops.visibility = View.GONE
            return
        }

        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        with(list_shops) {
            adapter = SimpleAdapter(shopList, adapterManager)
            setLayoutManager(layoutManager)
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