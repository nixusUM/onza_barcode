package com.onza.barcode.compare.products

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.CompareProductDelegate
import com.onza.barcode.data.model.Product
import com.onza.barcode.fragments.BarCodeFragment
import com.onza.barcode.utils.Utils
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

    private var eventListener: MiniAppCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_compare_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = CompareProdutsActivityPresenter(this, context!!, eventListener?.getToken())

        view_back.setOnClickListener { activity!!.onBackPressed() }
        toolbar_title.text = arguments!!.getString(NAME)
        if (Utils().isInternetAvailable()) {
            presenter.getComparesProducts(arguments!!.getInt(ID, 0), arguments!!.getInt(PRODUCT_ID, 0))
        } else {
            showCompareProducts(ArrayList<Product>())
            showError(getString(R.string.no_connection_message))
        }
        cardView_go_to_scan.setOnClickListener {
            Log.i("text", "true")
            eventListener!!.pushFragment(BarCodeFragment())
        }
    }

    override fun showCompareProducts(compareList: List<Product>) {

        progressBar.visibility = View.GONE
        if (compareList.isEmpty() || compareList.size == 1) {
            showPlaceHolderView()
            return
        }

        list_products_compare.visibility = View.VISIBLE

//        eventListener!!.logEvent("ProductComparison", "GoodsCompare", null, (page + 1).toLong())

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

    override fun showPlaceHolderView() {
        list_products_compare.visibility = View.GONE
        progressBar.visibility = View.GONE
        view_no_items.visibility = View.VISIBLE
    }

    fun showError(text: String?) {
        if(text != null) {
            Toast
                .makeText(activity!!, text, Toast.LENGTH_SHORT)
                .show()
        }
        else {
            Toast
                .makeText(activity!!, R.string.default_error, Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {

        const val ID = "category_id"
        const val NAME = "category_name"
        const val PRODUCT_ID = "product_id"

        fun getInstance(categoryId: Int, categoryName: String, productId: Int): Fragment {
            val fragment = CompareProdutsFragment()
            val bundle = Bundle()
            bundle.putInt(ID, categoryId)
            bundle.putString(NAME, categoryName)
            bundle.putInt(PRODUCT_ID, productId)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun removeProduct(productId: Int, position: Int) {
        var adapter = list_products_compare.adapter as SimpleAdapter
        adapter.removeItem(position)
        presenter.removeProductFromCompare(productId)
    }
}