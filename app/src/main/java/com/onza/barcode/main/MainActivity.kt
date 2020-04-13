package com.onza.barcode.main

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.alterevit.gorodminiapp.library.MiniAppCallback
import com.google.android.material.snackbar.Snackbar
import com.onza.barcode.R
import com.onza.barcode.base.BaseActivity
import com.onza.barcode.fragments.BarCodeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BaseActivity, MiniAppCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }

    private fun initView() {
        changeFragment(BarCodeFragment(), BarCodeFragment.javaClass.simpleName)
    }

    fun changeFragment(fragment: Fragment,
                               tag: String,
                               addToBackStack: Boolean = false) {

        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment, tag)
        if (addToBackStack) transaction.addToBackStack(tag)
        transaction.commitAllowingStateLoss()
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

    override fun getToken(): String? {
        return ""
    }

    override fun pushFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
