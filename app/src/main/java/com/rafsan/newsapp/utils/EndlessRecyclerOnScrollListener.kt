package com.rafsan.newsapp.utils

import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val layoutManager = recyclerView.layoutManager
        when (layoutManager) {
            is LinearLayoutManager -> {
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            }
            is GridLayoutManager -> {
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            }
            else -> {
                Exception("Unsupported LayoutManager")
            }
        }

        layoutManager?.let { manager ->
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

    fun resetOnLoadMore() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        isLoading = true
    }

    abstract fun onLoadMore()

}