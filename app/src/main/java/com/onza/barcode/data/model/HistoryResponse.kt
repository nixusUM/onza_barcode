package com.onza.barcode.data.model

/**
 * Created by Ilia Polozov on 23/February/2020
 */

data class HistoryResponse(val current_page: Int,
                           val total: Int,
                           val data: List<HistoryData>)