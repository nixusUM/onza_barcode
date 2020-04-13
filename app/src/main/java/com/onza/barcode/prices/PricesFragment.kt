package com.onza.barcode.prices

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.delegates.PricesDelegate
import com.onza.barcode.base.BaseActivity
import com.onza.barcode.data.Price
import com.onza.barcode.data.model.Product
import com.onza.barcode.dialogs.addPrice.AddPriceDialog
import kotlinx.android.synthetic.main.activity_price.*

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class PricesFragment: Fragment(), BaseActivity, PriceActivityView, PricesDelegate.ItemClick {

    private lateinit var presenter: PricesActivityPresenter

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(PricesDelegate(context!!, this@PricesFragment))
            }
    }

    private lateinit var selectedProduct: Product

    private var eventListener: MiniAppCallback? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_price, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = PricesActivityPresenter(this, context!!)
        selectedProduct = arguments!!.getSerializable(SELECTED_PRODUCT) as Product
        view_back.setOnClickListener { activity!!.onBackPressed() }
        contraint_add_price.setOnClickListener {
            AddPriceDialog.getInstance(selectedProduct, 0).show(this.childFragmentManager, "add price")
        }
        cardView_add_price.setOnClickListener{
            AddPriceDialog.getInstance(selectedProduct, 0).show(this.childFragmentManager, "add price")
        }
    }

    override fun onResume() {
        super.onResume()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        obtieneLocalizacion()
    }

    fun updatePrices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        obtieneLocalizacion()
    }

    @SuppressLint("MissingPermission")
    private fun obtieneLocalizacion() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                }
                presenter.getProductPrices(selectedProduct.id, lat, lon)
            }
    }

    override fun showError(text: String?) {
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

    override fun onItemClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProductPrices(priceList: List<Price>) {
        if (priceList.isNullOrEmpty()) {
            view_prices_placeholder.visibility = View.VISIBLE
            return
        }

        list_prices.visibility = View.VISIBLE
        contraint_add_price.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(context,
            RecyclerView.VERTICAL,
            false)

        with(list_prices) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(priceList, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    override fun showMessage(text: String?) {
        showError(text)
    }

    companion object {

        const val SELECTED_PRODUCT = "selected_product"

        fun getInstance(selectedProduct: Product): Fragment {
            val fragment = PricesFragment()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT, selectedProduct)
            fragment.arguments = bundle
            return fragment
        }
    }
}
