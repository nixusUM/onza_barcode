package com.onza.barcode.compare.products

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.delegates.CompareProductDelegate
import com.onza.barcode.data.model.Product
import kotlinx.android.synthetic.main.activity_compare_products.*

/**
 * Created by Ilia Polozov on 02/March/2020
 */

class CompareProdutsFragment: Fragment(), CompareProdutsView, CompareProductDelegate.ItemClick {

    private lateinit var presenter: CompareProdutsActivityPresenter

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(CompareProductDelegate(context!!, this@CompareProdutsFragment))
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_compare_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = CompareProdutsActivityPresenter(this, context!!)

//        setStatusBarColor()
        view_back.setOnClickListener { activity!!.onBackPressed() }
        toolbar_title.text = arguments!!.getString(NAME)
        presenter.getComparesProducts(arguments!!.getInt(ID, 0))
    }

    override fun showCompareProducts(compareList: List<Product>) {
        progressBar.visibility = View.GONE
        if (compareList.isEmpty()) {
            view_no_items.visibility = View.VISIBLE
            return
        }

        list_products_compare.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

        val helper = LinearSnapHelper()
        list_products_compare.onFlingListener = null
        helper.attachToRecyclerView(list_products_compare)

        with(list_products_compare) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(compareList, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    override fun showMessage(text: String) {
        progressBar.visibility = View.GONE
        showError(text)
    }

    fun showError(text: String?) {
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

    companion object {

        const val ID = "categoryId"
        const val NAME = "categoryName"

        fun getInstance(categoryId: Int, categoryName: String): Fragment {
            val fragment = CompareProdutsFragment()
            val bundle = Bundle()
            bundle.putInt(ID, categoryId)
            bundle.putString(NAME, categoryName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun removeProduct(position: Int) {

    }
}