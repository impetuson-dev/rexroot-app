package com.impetuson.rexroot.viewmodel.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JobsViewModel: ViewModel() {

    var _paymentStart = MutableLiveData<Int>(0)

    var _paymentEnd = MutableLiveData<Int>(Int.MAX_VALUE)

    var _jobSearch = MutableLiveData<String>("")

    var _jobLocation = MutableLiveData<String>("")

    var _paymentIsEnabled = MutableLiveData<Boolean>(false)

}