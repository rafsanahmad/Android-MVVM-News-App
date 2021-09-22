package com.rafsan.newsapp.ui.favorite

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rafsan.newsapp.R
import com.rafsan.newsapp.base.BaseFragment
import com.rafsan.newsapp.databinding.FragmentFavoritesBinding
import com.rafsan.newsapp.ui.MainActivity
import com.rafsan.newsapp.ui.MainViewModel
import com.rafsan.newsapp.ui.adapter.NewsAdapter

class FavoriteFragment : BaseFragment<FragmentFavoritesBinding>() {

    override fun setBinding(): FragmentFavoritesBinding =
        FragmentFavoritesBinding.inflate(layoutInflater)

    lateinit var viewModel: MainViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).mainViewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_favoriteFragment_to_detailsFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteNews(article)
                Snackbar.make(view, "Successfully deleted news article", Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Undo") {
                            viewModel.saveNews(article)
                        }
                        show()
                    }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavoriteNews)
        }

        viewModel.getFavoriteNews().observe(viewLifecycleOwner, Observer { news ->
            newsAdapter.differ.submitList(news)
        })
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvFavoriteNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}