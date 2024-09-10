package com.example.currencyconversion

data class CurrencyResponse(
    val base_currency_code: String,
    val base_currency_name: String,
    val amount: String,
    val rates: Map<String, CurrencyRate>
)
