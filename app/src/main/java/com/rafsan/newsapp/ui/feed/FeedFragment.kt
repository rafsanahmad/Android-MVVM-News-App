package com.rafsan.newsapp.ui.feed

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rafsan.newsapp.R
import com.rafsan.newsapp.base.BaseFragment
import com.rafsan.newsapp.databinding.FragmentFeedBinding
import com.rafsan.newsapp.ui.adapter.NewsAdapter
import com.rafsan.newsapp.ui.main.MainActivity
import com.rafsan.newsapp.ui.main.MainViewModel
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
    private lateinit var searchView: SearchView
    private var checkSearch: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = (activity as MainActivity).mainViewModel
        setupUI()
        setupRecyclerView()
        setupObservers()
        setHasOptionsMenu(true)
    }

    private fun setupUI() {
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            if (checkSearch) {
                mainViewModel.searchNews(mainViewModel.newQuery)
            } else {
                mainViewModel.fetchNews(countryCode)
            }
            hideErrorMessage()
        }

        // scroll listener for recycler view
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                if (checkSearch) {
                    mainViewModel.searchNews(mainViewModel.newQuery)
                } else {
                    mainViewModel.fetchNews(countryCode)
                }
            }
        }

        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel.clearSearch()
            mainViewModel.fetchNews(countryCode)
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener);

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
                com.rafsan.newsapp.R.id.action_feedFragment_to_DetailsFragment,
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

        mainViewModel.isSearchActivated.observe(viewLifecycleOwner, Observer { activated ->
            checkSearch = activated
            if (activated) {
                observeSearchResponse()
            } else {
                mainViewModel.searchNewsResponse.removeObservers(this)
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

    private fun observeSearchResponse() {
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.getActionView() as SearchView
        //Search button clicked
        searchView.setOnSearchClickListener {
            searchView.maxWidth = android.R.attr.width;
        }
        //Close button clicked
        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                mainViewModel.clearSearch()
                mainViewModel.fetchNews(countryCode)
                //Collapse the action view
                searchView.onActionViewCollapsed();
                searchView.maxWidth = 0;
                return true
            }
        })

        val searchPlate =
            searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
        searchPlate.hint = "Search"
        val searchPlateView: View =
            searchView.findViewById(androidx.appcompat.R.id.search_plate)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mainViewModel.searchNews(query)
                    mainViewModel.enableSearch()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        activity?.let {
            searchPlateView.setBackgroundColor(
                ContextCompat.getColor(
                    it,
                    android.R.color.transparent
                )
            )
            val searchManager =
                it.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(it.componentName))
        }
        //check if search is activated
        if (checkSearch) {
            searchView.isIconified = false
            searchItem.expandActionView();
            searchView.setQuery(mainViewModel.newQuery, false);
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }
}