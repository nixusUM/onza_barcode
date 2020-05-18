package com.onza.barcode.dialogs.addPrice

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.jakewharton.rxbinding2.widget.RxTextView
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
import com.onza.barcode.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.dialog_add_price.*
import kotlinx.android.synthetic.main.fragment_add_price.*
import kotlinx.android.synthetic.main.fragment_add_price.edt_price
import kotlinx.android.synthetic.main.fragment_add_price.list_shop
import kotlinx.android.synthetic.main.fragment_add_price.textView_location
import kotlinx.android.synthetic.main.fragment_add_price.textView_name
import kotlinx.android.synthetic.main.fragment_add_price.view_add_price
import kotlinx.android.synthetic.main.fragment_add_price.view_all_shops
import kotlinx.android.synthetic.main.fragment_add_price.view_cancel_price
import java.util.concurrent.TimeUnit

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddPriceDialog: BottomSheetDialogFragment(), AddPriceView, ShopDelegate.ItemClick  {

    private lateinit var presenter: AddPricePresenter

    private lateinit var selectdProduct: Product

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0

    private var eventListener: MiniAppCallback? = null
    private var storedShops = ArrayList<Shop>()

    private var selectedShop: Shop? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ShopDelegate(context!!, this@AddPriceDialog))
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val sheetInternal: View = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        val view = inflater.inflate(R.layout.dialog_add_price, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = AddPricePresenter(this, context!!)
        selectdProduct = arguments!!.getSerializable(SELECTED_PRODUCT) as Product

        if (locationPermissonsApproved()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
            obtieneLocalizacion()
            view_all_shops.setOnClickListener {
                ShopActivity.getInstance(selectdProduct).show(this.childFragmentManager, "shop")
            }

        } else {
            view_all_shops.setTextColor(ContextCompat.getColor(activity!!, R.color.gray_disabled_color))
            view_all_shops.setOnClickListener {
//                ShopActivity.getInstance(selectdProduct).show(this.childFragmentManager, "shop")
            }
            view_no_data.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }


        textView_name.text = selectdProduct.name
        textView_location.text = selectdProduct.production_place

        view_cancel_price.setOnClickListener {
            this.dismiss()
        }

        RxTextView.textChanges(edt_price)
            .debounce(100L, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (edt_price.text.isNullOrBlank()) {
                        view_add_price.setBackgroundDrawable(
                            ContextCompat.getDrawable(activity!!, R.drawable.ic_price_disabled)
                        )
                        view_add_price.setOnClickListener {}
                    } else {
                        if (this.selectedShop != null) {
                            view_add_price.setBackgroundDrawable(
                                ContextCompat.getDrawable(activity!!, R.drawable.ic_price_enebled)
                            )
                            view_add_price.setOnClickListener {
                                if (Utils().isInternetAvailable()) {
                                    eventListener!!.logEvent(
                                        "ProductCost",
                                        "GoodsPrice",
                                        null,
                                        null
                                    )
                                    presenter.addPriceToProduct(
                                        selectdProduct.id,
                                        this.selectedShop!!.branch.id,
                                        edt_price.text.toString().toFloat()
                                    )
                                } else {
                                    showError(getString(R.string.no_connection_message))
                                }
                            }
                        }
                    }
                },
                {})

        eventListener!!.logEvent("click_ProductCost", "GoodsPrice", null, selectdProduct.id.toLong())
    }

    private fun locationPermissonsApproved(): Boolean {
        val context = context ?: return false
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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
                if (Utils().isInternetAvailable()) {
                    presenter.getNeearShops(lat, lon, 1)

                } else {
                    initShopList(ArrayList<Shop>())
                    showError(getString(R.string.no_connection_message))
                }
//                } else {
//                    initShopList(selectdProduct.shops)
//                }
            }
    }

    override fun initShopList(shops: List<Shop>?) {
        if (shops.isNullOrEmpty()) {
            progressBar.visibility = View.GONE
            view_no_data.visibility = View.VISIBLE
            return
        }

        this.storedShops.addAll(shops)
        progressBar.visibility = View.GONE
        list_shop.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(activity!!,
            RecyclerView.HORIZONTAL,
            false)

        with(list_shop) {
            adapter = SimpleAdapter(storedShops, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    override fun shopSelected(shop: Shop, position: Int) {
        this.selectedShop = shop
        var selectedSjopPosition = 0
        var adapter = list_shop.adapter as SimpleAdapter

        if (!storedShops.contains(shop)) {
            storedShops.add(shop)
        }

        for (i in storedShops.indices) {
            if (storedShops[i].id == shop.id) {
                selectedSjopPosition = i
                storedShops[i].isSelected = true
            } else {
                storedShops[i].isSelected = false
            }
        }
        adapter.updateShopSelected(storedShops)
        list_shop.scrollToPosition(selectedSjopPosition)
        if (!edt_price.text.toString().isNullOrEmpty()) {
            view_add_price.setBackgroundDrawable(
                ContextCompat.getDrawable(activity!!, R.drawable.ic_price_enebled)
            )
            view_add_price.setOnClickListener {
                if (Utils().isInternetAvailable()) {
                    eventListener!!.logEvent(
                        "ProductCost",
                        "GoodsPrice",
                        null,
                        null
                    )
                    presenter.addPriceToProduct(
                        selectdProduct.id,
                        this.selectedShop!!.branch.id,
                        edt_price.text.toString().toFloat()
                    )
                } else {
                    showError(getString(R.string.no_connection_message))
                }
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
            parentFragment.updatePrices(productPrice.price)
            dismiss()
        } else {
            val parentFragment = parentFragment as DetailFragment
            parentFragment.refreshProductDetailData(selectdProduct.id, lat, lon)
            dismiss()
        }
    }
}
