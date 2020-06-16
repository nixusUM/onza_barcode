package com.onza.barcode.dialogs.addProduct

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.UnknownHostException
import java.util.*

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddProductPresenter(val view: AddProductView, val context: Context, val fragment: BottomSheetDialogFragment, val token: String?) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(token, gson, context)
    }

    fun onImagePick() {
        ImagePicker.with(fragment)
            .cropSquare()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1000, 1000)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    fun postProduct(
        name: String,
        gtin: String,
        price: Double,
        categoryId: Int,
        rating: Int,
        shopBranchId: Int,
        images: List<File>) {

        val imageList = ArrayList<MultipartBody.Part>()
        for (i in images.indices) {
            var fileBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), Utils().saveBitmap(Utils().saveBitmapToFile(File(images[i].path)), File(images[i].path).absolutePath))
            var body = MultipartBody.Part.createFormData("images[$i]", images[i].name, fileBody)
            imageList.add(body)
        }
        val  params = HashMap<String, RequestBody>()
        params.put("name", Utils().toRequestBody(name))
        params.put("gtin", Utils().toRequestBody(gtin))
        params.put("price", Utils().toRequestBody(price.toString()))
        params.put("category_id", Utils().toRequestBody(categoryId.toString()))
        params.put("rating", Utils().toRequestBody(rating.toString()))
        params.put("shop_branch_id", Utils().toRequestBody(shopBranchId.toString()))

        apiService.postProduct(params, imageList)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    when (it.code()) {
                        400 -> view.showMessage(it.errorBody()!!.string().toString())
                        200 -> view.addedProduct(it.body()!!.data!!.gtin!!, it.body()!!.data!!.id)
                    }

                },
                {
                    errorHandling(it)
                })
    }

    private fun errorHandling(t: Throwable) {
        Log.i("error", t.toString())
        if (t is UnknownHostException) {
            view.showMessage(t.toString())
        }
    }
}