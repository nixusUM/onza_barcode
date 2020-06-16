package com.onza.barcode.compare

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.CompareListDelegate
import com.onza.barcode.compare.products.CompareProdutsFragment
import com.onza.barcode.data.model.CompareCategories
import com.onza.barcode.data.model.Product
import com.onza.barcode.utils.RecyclerTouchListener
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.activty_compare.*

/**
 * Created by Ilia Polozov on 01/March/2020
 */

class CompareFragment: Fragment(), CompareView, CompareListDelegate.ItemClick {

    private lateinit var presenter: CompareActivityPresenter
    private lateinit var selectedProduct: Product
    private var eventListener: MiniAppCallback? = null

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(CompareListDelegate(context!!, this@CompareFragment))
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        eventListener = context as? MiniAppCallback
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activty_compare, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = CompareActivityPresenter(this, context!!, eventListener?.getToken())
//        setStatusBarColor()
        cardView_scan.setOnClickListener { activity!!.onBackPressed() }
        view_back.setOnClickListener { activity!!.onBackPressed() }

        if (Utils().isInternetAvailable()) {
            presenter.getCompares()
        } else {
            showCompareCategories(ArrayList<CompareCategories>())
            showError(getString(R.string.no_connection_message))
        }
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

    override fun showCompareCategories(compareCategoryList: List<CompareCategories>) {
        if (progressBar == null) {
            view_no_items.visibility = View.VISIBLE
            return
        }
        progressBar.visibility = View.GONE
        if (compareCategoryList.isEmpty()) {
            view_no_items.visibility = View.VISIBLE
            return
        }

        list_compares.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(context,
            RecyclerView.VERTICAL,
            false)

        with(list_compares) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(compareCategoryList, adapterManager)
            setLayoutManager(layoutManager)
        }

        var touchListener = RecyclerTouchListener(activity, list_compares)
        touchListener.setClickable(object : RecyclerTouchListener.OnRowClickListener {
            override fun onRowClicked(position: Int) {
//                showError("clickedRoww$position")
            }

            override fun onIndependentViewClicked(independentViewID: Int, position: Int) {

            }
        })

        touchListener.setSwipeOptionViews(R.id.delete_task).setSwipeable(R.id.rowFG, R.id.rowBG
        ) { viewID, position ->
            when (viewID) {
                R.id.delete_task -> {
                    presenter.deleteCompareCategory(compareCategoryList[position].category_id, position)
                }
            }
        }

        list_compares.addOnItemTouchListener(touchListener)
    }

    override fun showMessage(text: String) {
        progressBar.visibility = View.GONE
        showError(text)
    }

    override fun removedCompareCategory(position: Int) {
        var adapter = list_compares.adapter as SimpleAdapter
        adapter.removeItem(position)
        presenter.getCompares()
    }

    override fun onCompareClicked(id: Int, name: String) {
        eventListener!!.pushFragment(CompareProdutsFragment.getInstance(id, name, 0))
    }

}