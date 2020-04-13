package com.onza.barcode.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.onza.barcode.R
import com.onza.barcode.data.model.Product
import com.onza.barcode.product.fragments.detail.DetailFragment
import kotlinx.android.synthetic.main.activity_detail.*

/**
 * Created by Ilia Polozov on 28/January/2020
 */

class ProductDetaislFragment : Fragment() {

    private lateinit var presenter: ProductDetaislActivityPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = ProductDetaislActivityPresenter(this)
        changeFragment(
            DetailFragment.getInstance(arguments!!.getSerializable(SELECTED_PRODUCT) as Product),
            ""
        )

    }

    fun showError(text: String?) {
        if (text != null) {
            Snackbar
                .make(rootView, text, Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Snackbar
                .make(rootView, R.string.default_error, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun changeFragment(
        fragment: Fragment,
        tag: String
    ) {

        val transaction = fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.main_container, fragment, tag)
        if (tag.isNotEmpty()) {
            transaction!!.addToBackStack(tag)
        }
        transaction!!.commitAllowingStateLoss()
    }

    companion object {

        const val SELECTED_PRODUCT = "selected_product"

        fun getInstance(selectedProduct: Product): Fragment {
            val fragment = ProductDetaislFragment()
            val bundle = Bundle()
            bundle.putSerializable(SELECTED_PRODUCT, selectedProduct)
            fragment.arguments = bundle
            return fragment
        }
    }
}