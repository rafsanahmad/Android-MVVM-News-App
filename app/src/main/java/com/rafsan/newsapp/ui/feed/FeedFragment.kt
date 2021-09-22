package com.rafsan.newsapp.ui.feed

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafsan.newsapp.R
import com.rafsan.newsapp.base.BaseFragment
import com.rafsan.newsapp.databinding.FragmentFeedBinding
import com.rafsan.newsapp.ui.MainActivity
import com.rafsan.newsapp.ui.MainViewModel
import com.rafsan.newsapp.ui.adapter.NewsAdapter
import com.rafsan.newsapp.utils.Constants
import com.rafsan.newsapp.utils.Constants.Companion.QUERY_PER_PAGE
import com.rafsan.newsapp.utils.EndlessRecyclerOnScrollListener
import com.rafsan.newsapp.utils.NetworkResult

class FeedFragment : BaseFragment<FragmentFeedBinding>() {

    override fun setBinding(): FragmentFeedBinding =
        FragmentFeedBinding.inflate(layoutInflater)

    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener
    lateinit var mainViewModel: MainViewModel
    lateinit var newsAdapter: NewsAdapter
    val countryCode = Constants.CountryCode

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        setupUI()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupUI() {
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (mainViewModel.isSearchActivated) {
                mainViewModel.searchNews(mainViewModel.newQuery)
                hideErrorMessage()
            } else {
                mainViewModel.fetchNews(countryCode)
            }
        }

        // scroll listener for recycler view
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                if (mainViewModel.isSearchActivated) {
                    mainViewModel.searchNews(mainViewModel.newQuery)
                } else {
                    mainViewModel.fetchNews(countryCode)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(onScrollListener)
        }
        newsAdapter.setOnItemClickListener { news ->
            val bundle = Bundle().apply {
                putSerializable("news", news)
            }
            findNavController().navigate(
                R.id.action_feedFragment_to_DetailsFragment,
                bundle
            )
        }
    }

    private fun setupObservers() {
        mainViewModel.newsResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { newResponse ->
                        newsAdapter.differ.submitList(newResponse.articles.toList())
                        val totalPage = newResponse.totalResults / QUERY_PER_PAGE + 2
                        onScrollListener.isLastPage = mainViewModel.searchNewsPage == totalPage
                        if (onScrollListener.isLastPage) {
                            binding.rvNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is NetworkResult.Loading -> {
                    showProgressBar()
                }

                is NetworkResult.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        showErrorMessage(response.message)
                    }
                }
            }
        })

        //Search response
        mainViewModel.searchNewsResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { searchResponse ->
                        newsAdapter.differ.submitList(searchResponse.articles.toList())
                        val totalPage = searchResponse.totalResults / QUERY_PER_PAGE + 2
                        onScrollListener.isLastPage = mainViewModel.searchNewsPage == totalPage
                        if (onScrollListener.isLastPage) {
                            binding.rvNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is NetworkResult.Loading -> {
                    showProgressBar()
                }

                is NetworkResult.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        showErrorMessage(response.message)
                    }
                }
            }
        })

        mainViewModel.errorToast.observe(viewLifecycleOwner, Observer { value ->
            if (value.isNotEmpty()) {
                Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
            } else {
                mainViewModel.hideErrorToast()
            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }
}