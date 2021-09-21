package com.rafsan.newsapp.ui.favorite

import com.rafsan.newsapp.base.BaseFragment
import com.rafsan.newsapp.databinding.FragmentFavoritesBinding

class FavoriteFragment : BaseFragment<FragmentFavoritesBinding>() {

    override fun setBinding(): FragmentFavoritesBinding =
        FragmentFavoritesBinding.inflate(layoutInflater)
}