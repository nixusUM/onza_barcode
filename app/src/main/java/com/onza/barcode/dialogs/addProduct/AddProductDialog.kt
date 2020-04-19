package com.onza.barcode.dialogs.addProduct

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.ProductImageDelegate
import com.onza.barcode.category.CategoryListActivity
import com.onza.barcode.fragments.BarCodeFragment
import com.onza.barcode.shop.ShopActivity
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.dialog_add_product.*

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddProductDialog: BottomSheetDialogFragment(), AddProductView, ProductImageDelegate.ItemClick {

    private lateinit var presenter: AddProductPresenter

    private val storedImages = mutableListOf<Image>()

    private var shopid = 0
    private var categoryId = 0

    private var eventListener: MiniAppCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(ProductImageDelegate(context!!, this@AddProductDialog))
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val sheetInternal: View = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = AddProductPresenter(this, context!!, this)

        if (!arguments!!.getString(GTIN).isNullOrEmpty()) {
            txt_barcode.setText(arguments!!.getString(GTIN))
        }

        view_shops.setOnClickListener {
            ShopActivity.getInstance(null).show(this.childFragmentManager, "shop")
        }

        view_category.setOnClickListener {
            CategoryListActivity.getInstance().show(this.childFragmentManager, "category")
        }

        image.setOnClickListener {
            presenter.onImagePick()
        }

        view_cancel.setOnClickListener {
            this.dismiss()
        }

    }

    fun showError(text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(text: String?) {
        showError(text)
    }

    override fun addedProduct(gtin: String, id: Int) {
        if (parentFragment is BarCodeFragment) {
            eventListener!!.logEvent("AddProduct", "GoodsAdd", "success_add",  id.toLong())
            val parentFragment = parentFragment as BarCodeFragment
            parentFragment.updateItem(gtin, arguments!!.getInt(POSITION))
            dismiss()
        }
    }

    fun selectCategory(category: String, id: Int) {
        category_name.text = category
        categoryId = id
        initSendButton()
    }

    fun selectShop(name: String, id: Int) {
        shop_name.text = name
        shopid = id
        initSendButton()
    }

    private fun initSendButton() {
        if (shopid != 0 && categoryId != 0 && !txt_barcode.text.isNullOrEmpty() && !txt_price.text.toString().isNullOrEmpty()) {
            view_add_product.setBackgroundDrawable(
                ContextCompat.getDrawable(context!!, R.drawable.ic_price_enebled)
            )
            view_add_product.setOnClickListener {
                if (Utils().isInternetAvailable()) {
                    presenter.postProduct(name.text.toString(), txt_barcode.text.toString(), txt_price.text.toString().toDouble(),
                        categoryId, product_rating.rating.toInt(), shopid, storedImages)
                } else {
                    showError(getString(R.string.no_connection_message))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val images = ImagePicker.getImages(data)
            if (images != null) {
                this.storedImages.addAll(images)
                initProductImageAdapter(storedImages)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initProductImageAdapter(imageList: MutableList<Image>) {
        image_list.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(
            context!!,
            RecyclerView.HORIZONTAL,
            false
            )

            with(image_list) {
                adapter = SimpleAdapter(imageList, adapterManager)
                setLayoutManager(layoutManager)
            }
    }

    override fun removeImage(postion: Int) {
        this.storedImages.removeAt(postion)
        initProductImageAdapter(this.storedImages)
    }

    companion object {

        const val CATEGORY = "category"
        const val SHOP = "shop"
        const val POSITION = "position"
        const val GTIN = "gtin"
        const val ID = "id"

        fun getInstance(position: Int, gtin: String): BottomSheetDialogFragment {
            val fragment = AddProductDialog()
            val bundle = Bundle()
            bundle.putString(GTIN, gtin)
            bundle.putInt(POSITION, position)
            fragment.arguments = bundle
            return fragment
        }
    }
}
