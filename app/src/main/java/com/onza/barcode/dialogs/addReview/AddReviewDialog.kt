package com.onza.barcode.dialogs.addReview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.ShopDelegate
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Shop
import com.onza.barcode.fragments.BarCodeFragment
import com.onza.barcode.product.fragments.detail.DetailFragment
import com.onza.barcode.reviews.ReviewsFragment
import com.onza.barcode.shop.ShopActivity
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.dialog_add_review.*
import kotlinx.android.synthetic.main.fragment_add_review.list_shop
import kotlinx.android.synthetic.main.fragment_add_review.view_all_shops

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddReviewDialog: BottomSheetDialogFragment(), AddReviewView, ShopDelegate.ItemClick  {

    private lateinit var presenter: AddReviewPresenter

    private lateinit var selectdProduct: Product
    private lateinit var selectedShop: Shop

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat = 0.0
    private var lon = 0.0

    private var eventListener: MiniAppCallback? = null
    private var storedShops = ArrayList<Shop>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ShopDelegate(context!!, this@AddReviewDialog))
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val sheetInternal: View = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        val view = inflater.inflate(R.layout.dialog_add_review, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = AddReviewPresenter(context!!, this, eventListener?.getToken())
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


        view_cancel_review.setOnClickListener {
            this.dismiss()
        }
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
//                if (!selectdProduct.shops.isNullOrEmpty() || lat < 1.0) {
                if (Utils().isInternetAvailable()) {
                    presenter.getNeearShops(lat, lon)
                } else {
                    showError(getString(R.string.no_connection_message))
                    initShop(ArrayList<Shop>())
                }
//                } else {
//                    initShop(selectdProduct.shops)
//                }
            }
    }

    override fun initShop(shops: List<Shop>?) {
        if (shops.isNullOrEmpty()) {
            progressBar.visibility = View.GONE
            view_no_data.visibility = View.VISIBLE
            return
        }

        this.storedShops.addAll(shops)
        progressBar.visibility = View.GONE
        list_shop.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context!!,
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
        txt_add_review.setTextColor(ContextCompat.getColor(context!!, android.R.color.black))
        view_add_review.setBackgroundDrawable(
                ContextCompat.getDrawable(context!!, R.drawable.ic_price_enebled)
            )
        view_add_review.setOnClickListener {
            if (Utils().isInternetAvailable()) {
                presenter.addReview(selectdProduct.id,
                    selectdProduct.owner_rating!!.toDouble(), selectedShop.id, positive.text.toString(), negative.text.toString(), review_text.text.toString())
            } else {
                showError(getString(R.string.no_connection_message))
            }
        }
    }

    fun showError(text: String?) {
        progressBar.visibility = View.GONE
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(text: String?) {
        showError(text)
    }

    override fun successAdded(ownerRating: Float) {
        eventListener!!.logEvent("ProductReview", "GoodsReview", null, selectdProduct.id.toLong())
        if (parentFragment is ReviewsFragment) {
            val parentFragment = parentFragment as ReviewsFragment
            parentFragment.updateReviews()
            dismiss()
        } else  if (parentFragment is BarCodeFragment) {
            val parentFragment = parentFragment as BarCodeFragment
            parentFragment.showAddedReviewDialog(selectdProduct.gtin!!, arguments!!.getInt(POSITION), ownerRating)
            dismiss()
        } else {
            val parentFragment = parentFragment as DetailFragment
            parentFragment.refreshProductDetailData(selectdProduct.id, lat, lon)
            dismiss()
        }
    }

    companion object {

        const val SELECTED_PRODUCT = "selected_product"
        const val POSITION = "position"

        fun getInstance(selectedProduct: Product, position: Int): BottomSheetDialogFragment {
            val fragment = AddReviewDialog()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT, selectedProduct)
            bundle.putInt(POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }

}
