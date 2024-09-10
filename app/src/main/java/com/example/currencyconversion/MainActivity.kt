package com.example.currencyconversion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var currencyApiService: CurrencyApiService
    private lateinit var fromCurrency: EditText
    private lateinit var toCurrency: EditText
    private lateinit var amount: EditText
    private lateinit var convertButton: Button
    private lateinit var conversionResultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the UI components
        fromCurrency = findViewById(R.id.fromCurrency)
        toCurrency = findViewById(R.id.toCurrency)
        amount = findViewById(R.id.amount)
        convertButton = findViewById(R.id.convertButton)
        conversionResultTextView = findViewById(R.id.conversionResultTextView)

        // Initialize Retrofit
        retrofit = Retrofit.Builder()
            .baseUrl("https://currency.getgeoapi.com") // Base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON conversion
            .build()

        // Create the API service
        currencyApiService = retrofit.create(CurrencyApiService::class.java)

        // Set up the button's click listener to trigger currency conversion
        convertButton.setOnClickListener {
            val from = fromCurrency.text.toString().trim().uppercase()
            val to = toCurrency.text.toString().trim().uppercase()
            val amountValue = amount.text.toString().toDoubleOrNull()

            if (from.isNotEmpty() && to.isNotEmpty() && amountValue != null) {
                convertCurrency(from, to, amountValue)
            } else {
                conversionResultTextView.text = "Please enter valid inputs"
            }
        }
    }

    private fun convertCurrency(from: String, to: String, amount: Double) {
        val call = currencyApiService.convertCurrency("0775e3bb2153cd52bcfd9fabd4dc945b55e4fef1", from, to, amount)

        call.enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(
                call: Call<CurrencyResponse>,
                response: Response<CurrencyResponse>
            ) {
                if (response.isSuccessful) {
                    val currencyResponse = response.body()

                    // Extract the conversion rate and update the UI
                    val conversionRate = currencyResponse?.rates?.get(to)?.rate_for_amount
                    if (conversionRate != null) {
                        conversionResultTextView.text = "Converted Amount: $conversionRate"
                    } else {
                        conversionResultTextView.text = "Conversion failed. Check your inputs."
                    }
                } else {
                    conversionResultTextView.text = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                // Handle API call failure
                conversionResultTextView.text = "Failed to convert currency. Please try again."
            }
        })
    }
}

