package com.rafsan.newsapp.ui.feed

import com.rafsan.newsapp.base.BaseFragment
import com.rafsan.newsapp.databinding.FragmentFeedBinding

class FeedFragment : BaseFragment<FragmentFeedBinding>() {

    override fun setBinding(): FragmentFeedBinding =
        FragmentFeedBinding.inflate(layoutInflater)

}