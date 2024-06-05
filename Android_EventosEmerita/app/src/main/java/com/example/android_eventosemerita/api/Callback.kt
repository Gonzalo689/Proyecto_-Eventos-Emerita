package com.example.android_eventosemerita.api

/**
 * Interfaz que define un Callback genérico para manejar resultados de la API.
 */
interface Callback {
    /**
     * Interfaz interna que define métodos para manejar el éxito y el error de una operación.
     * @param <T> El tipo de dato del resultado de la operación asíncrona.
     */
    interface MyCallback<T>{
        fun onSuccess(data: T)
        fun onError(errorMsg: T?)
    }
}