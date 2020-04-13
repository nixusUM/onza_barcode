package com.onza.barcode.product.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.onza.barcode.R
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.ProductDetail
import kotlinx.android.synthetic.main.fragment_about_product.*
import kotlinx.android.synthetic.main.item_properties.view.*
import kotlinx.android.synthetic.main.view_product_properties.view.*

/**
 * Created by Ilia Polozov on 28/January/2020
 */

class AboutFragment : Fragment(), AboutFragmentView {

    private lateinit var presenter: AboutFragmentPresenter
    private lateinit var selectdProduct: Product

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = AboutFragmentPresenter(this, activity!!)
        selectdProduct = arguments!!.getSerializable(SLECTED_PRODUCT) as Product
        presenter.getProductDetail(selectdProduct.id)
        view_back.setOnClickListener { activity!!.onBackPressed() }
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

    override fun showAboutProduct(productDetail: List<ProductDetail>) {
        view_product_data.removeAllViews()
        for(detail in productDetail) {
            val property = cratePropertyViews(detail)
            view_product_data.addView(property)
        }
    }

    private fun cratePropertyViews(detail: ProductDetail): LinearLayout {
        val linflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lyt = linflater.inflate(R.layout.view_product_properties, null) as LinearLayout
        val lytProperty = linflater.inflate(R.layout.item_properties, null) as LinearLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        lyt.layoutParams = params
        lytProperty.layoutParams = params
        lyt.title.text = detail.title
        lyt.lyt_properties.removeAllViews()
        for (item in detail.properties) {
            val proertfds = createProperites(item.title, item.value)
            lyt.lyt_properties.addView(proertfds)
        }

        return lyt
    }

    private fun createProperites(name: String, value: String): LinearLayout {
        val linflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lytProperty = linflater.inflate(R.layout.item_properties, null) as LinearLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        lytProperty.layoutParams = params
        lytProperty.hint.text = name
        lytProperty.value.text = value

        return lytProperty
    }

    companion object {

        const val SLECTED_PRODUCT = "selected_product"

        fun getInstance(selectedProduct: Product): Fragment {
            val fragment = AboutFragment()
            val bundle = Bundle()
            bundle.putSerializable(SLECTED_PRODUCT, selectedProduct)
            fragment.arguments = bundle
            return fragment
        }
    }
}