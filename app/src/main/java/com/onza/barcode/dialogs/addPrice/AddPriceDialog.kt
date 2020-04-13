package com.onza.barcode.dialogs.addPrice

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.ShopDelegate
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.ProductPrice
import com.onza.barcode.data.model.Shop
import com.onza.barcode.fragments.BarCodeFragment
import com.onza.barcode.prices.PricesFragment
import com.onza.barcode.product.fragments.detail.DetailFragment
import com.onza.barcode.shop.ShopActivity
import kotlinx.android.synthetic.main.dialog_add_price.*
import kotlinx.android.synthetic.main.fragment_add_price.*
import kotlinx.android.synthetic.main.fragment_add_price.edt_price
import kotlinx.android.synthetic.main.fragment_add_price.list_shop
import kotlinx.android.synthetic.main.fragment_add_price.textView_location
import kotlinx.android.synthetic.main.fragment_add_price.textView_name
import kotlinx.android.synthetic.main.fragment_add_price.view_add_price
import kotlinx.android.synthetic.main.fragment_add_price.view_all_shops
import kotlinx.android.synthetic.main.fragment_add_price.view_cancel_price

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddPriceDialog: BottomSheetDialogFragment(), AddPriceView, ShopDelegate.ItemClick  {

    private lateinit var presenter: AddPricePresenter

    private lateinit var selectdProduct: Product

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ShopDelegate(context!!, this@AddPriceDialog))
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = AddPricePresenter(this, context!!)
        selectdProduct = arguments!!.getSerializable(SELECTED_PRODUCT) as Product

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        obtieneLocalizacion()

        textView_name.text = selectdProduct.name
        textView_location.text = selectdProduct.production_place
        view_all_shops.setOnClickListener {
            ShopActivity.getInstance(selectdProduct).show(this.childFragmentManager, "shop")
        }

        view_cancel_price.setOnClickListener {
            this.dismiss()
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
//                if (!selectdProduct.shops.isNullOrEmpty() || lat < 1.0) {?
                    presenter.getNeearShops(lat, lon)
//                } else {
//                    initShopList(selectdProduct.shops)
//                }
            }
    }

    override fun initShopList(shops: List<Shop>?) {
        if (shops.isNullOrEmpty()) {
            progressBar.visibility = View.GONE
            return
        }

        progressBar.visibility = View.GONE
        list_shop.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(activity!!,
            RecyclerView.HORIZONTAL,
            false)

        with(list_shop) {
            adapter = SimpleAdapter(shops, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    override fun shopSelected(shop: Shop, position: Int) {
        var adapter = list_shop.adapter as SimpleAdapter
        adapter.updateShopSelected(position)
        if (edt_price.text.toString().isNotEmpty()) {
            view_add_price.setBackgroundDrawable(
                ContextCompat.getDrawable(activity!!, R.drawable.ic_price_enebled)
            )
            view_add_price.setOnClickListener {
                presenter.addPriceToProduct(selectdProduct.id, shop.branch.id, edt_price.text.toString().toFloat())
            }
        }
    }

    companion object {

        const val SELECTED_PRODUCT = "selected_product"
        const val POSITION = "position"

        fun getInstance(selectedProduct: Product, position: Int): BottomSheetDialogFragment {
            val fragment = AddPriceDialog()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT, selectedProduct)
            bundle.putInt(POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }

    fun showError(text: String?) {
        progressBar.visibility = View.GONE
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
//        if(text != null) {
//            Snackbar
//                .make(rootView, text, Snackbar.LENGTH_SHORT)
//                .show()
//        }
//        else {
//            Snackbar
//                .make(rootView, R.string.default_error, Snackbar.LENGTH_SHORT)
//                .show()
//        }
    }

    override fun showMessage(text: String?) {
        showError(text)
    }

    override fun successAdded(productPrice: ProductPrice) {
        if (parentFragment is BarCodeFragment) {
            val parentFragment = parentFragment as BarCodeFragment
            parentFragment.showAddedPriceDialog(productPrice.price, selectdProduct.gtin!!, arguments!!.getInt(POSITION))
            dismiss()
        } else if (parentFragment is PricesFragment){
            val parentFragment = parentFragment as PricesFragment
            parentFragment.updatePrices()
            dismiss()
        } else {
            val parentFragment = parentFragment as DetailFragment
            parentFragment.refreshProductDetailData(selectdProduct.gtin!!, lat, lon)
            dismiss()
        }
    }
}
