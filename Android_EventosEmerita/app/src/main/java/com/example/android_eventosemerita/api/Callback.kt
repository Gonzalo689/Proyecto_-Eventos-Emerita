package com.example.android_eventosemerita.api

import com.example.android_eventosemerita.api.model.User

interface Callback {
    interface MyCallback<T>{
        fun onSuccess(data: T)
        fun onError(errorMsg: T?)
    }
}