package com.onza.barcode.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.R
import com.onza.barcode.adapters.SimpleAdapter
import com.onza.barcode.adapters.delegates.CategoryInListDelegate
import com.onza.barcode.data.model.Category
import com.onza.barcode.dialogs.addProduct.AddProductDialog
import com.onza.barcode.utils.Utils
import kotlinx.android.synthetic.main.activity_all_categories.*

/**
 * Created by Ilia Polozov on 26/February/2020
 */

class CategoryListActivity: BottomSheetDialogFragment(), CategoryListView, CategoryInListDelegate.Callback {

    private lateinit var presenter: CategoryListActivityPresenter

    private val adapterManager by lazy {
        AdapterDelegatesManager<List<*>>()
            .apply {
                addDelegate(CategoryInListDelegate(context!!, this@CategoryListActivity))
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val sheetInternal: View = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
            BottomSheetBehavior.from(sheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }

        val view = inflater.inflate(R.layout.activity_all_categories, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = CategoryListActivityPresenter(this, context!!)
        view_back.setOnClickListener { parentFragment!!.childFragmentManager.popBackStack() }

        if (Utils().isInternetAvailable()) {
            presenter.getCategories()
        } else {
            showCategoryList(ArrayList<Category>())
            showError(getString(R.string.no_connection_message))
        }

        edt_search.addTextChangedListener((object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var adapter = list_categories.adapter as SimpleAdapter
                if (p0!!.length > 2) {
                    adapter.filterCategories(p0.toString())
                    showNoResultPlaceHolder(adapter.items.isEmpty())
                } else {
                    adapter.filterCategories("")
                    showNoResultPlaceHolder(false)
                }
            }
        }))
    }

    fun showError(text: String?) {
        progressBar.visibility = View.GONE
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

    private fun showNoResultPlaceHolder(show: Boolean){
        if (show) {
            view_no_result.visibility = View.VISIBLE
            list_categories.visibility = View.GONE
        } else {
            view_no_result.visibility = View.GONE
            list_categories.visibility = View.VISIBLE
        }
    }

    override fun onCategoryClicked(name: String, id: Int) {
        val parentFragment = parentFragment as AddProductDialog
        parentFragment.selectCategory(name, id)
        dismiss()
    }

    override fun showCategoryList(categoryList: List<Category>) {
        progressBar.visibility = View.GONE
        if (categoryList.isEmpty()) {
            view_no_result.visibility = View.VISIBLE
            list_categories.visibility = View.GONE
            return
        }

        val layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        with(list_categories) {
            adapter = com.onza.barcode.adapters.SimpleAdapter(categoryList, adapterManager)
            setLayoutManager(layoutManager)
        }
    }

    override fun showMessage(text: String?) {
        showError(text)
    }

    companion object {

        fun getInstance(): BottomSheetDialogFragment = CategoryListActivity()
    }
}