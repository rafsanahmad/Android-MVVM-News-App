package com.rafsan.newsapp.ui.details

import com.rafsan.newsapp.base.BaseFragment
import com.rafsan.newsapp.databinding.FragmentDetailsBinding

class DetailsFragment : BaseFragment<FragmentDetailsBinding>() {

    override fun setBinding(): FragmentDetailsBinding =
        FragmentDetailsBinding.inflate(layoutInflater)
}