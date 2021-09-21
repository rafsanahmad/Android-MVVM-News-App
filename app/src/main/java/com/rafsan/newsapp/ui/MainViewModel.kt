package com.rafsan.newsapp.ui

import androidx.lifecycle.ViewModel
import com.rafsan.newsapp.network.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(mainRepo: MainRepository) : ViewModel() {

}