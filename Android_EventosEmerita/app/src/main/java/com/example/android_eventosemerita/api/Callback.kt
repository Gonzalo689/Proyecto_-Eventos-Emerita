package com.example.android_eventosemerita.api


interface Callback {
    interface MyCallback<T>{
        fun onSuccess(data: T)
        fun onError(errorMsg: T?)
    }
}