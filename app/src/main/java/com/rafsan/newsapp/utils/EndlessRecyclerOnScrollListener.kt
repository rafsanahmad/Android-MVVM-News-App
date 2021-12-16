/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

package com.rafsan.newsapp.utils

import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rafsan.newsapp.utils.Constants.Companion.QUERY_PER_PAGE

abstract class EndlessRecyclerOnScrollListener(
    private val threshHold: Int = QUERY_PER_PAGE
) : RecyclerView.OnScrollListener() {

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val mLayoutManager = recyclerView.layoutManager
        when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val firstVisibleItemPositions =
                    mLayoutManager.findFirstVisibleItemPositions(
                        null
                    )
                // get maximum element within the list
                firstVisibleItem = getFirstVisibleItem(firstVisibleItemPositions)
                //firstVisibleItem = firstVisibleItemPositions[0]
            }
            is GridLayoutManager -> {
                firstVisibleItem =
                    mLayoutManager.findFirstVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                firstVisibleItem =
                    mLayoutManager.findFirstVisibleItemPosition()
            }
            else -> {
                Exception("Unsupported LayoutManager")
            }
        }

        mLayoutManager?.let { manager ->
            visibleItemCount = manager.childCount
            totalItemCount = manager.itemCount
        }

        val isNoError = !isError
        val isNotLoadingAndIsNotLastPage = !isLoading && !isLastPage
        val isAtLastItem = (firstVisibleItem + visibleItemCount) >= totalItemCount
        val isNotAtBeginning = firstVisibleItem > 0
        val isTotalMoreThanVisible = totalItemCount >= threshHold

        val shouldPaginate =
            isNoError && isNotLoadingAndIsNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

        if (shouldPaginate) {
            isScrolling = false;
            onLoadMore()
        }
    }

    private fun getFirstVisibleItem(firstVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in firstVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = firstVisibleItemPositions[i]
            } else if (firstVisibleItemPositions[i] > maxSize) {
                maxSize = firstVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    fun resetOnLoadMore() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        isLoading = true
    }

    abstract fun onLoadMore()

}