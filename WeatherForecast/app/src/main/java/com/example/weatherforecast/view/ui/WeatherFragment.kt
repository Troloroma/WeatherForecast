package com.example.weatherforecast.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FragmentWeatherBinding
import com.example.weatherforecast.service.model.Weather
import com.example.weatherforecast.view.adapter.DaysAdapter
import com.example.weatherforecast.viewmodel.WeatherViewModel
import java.lang.NullPointerException

//https://api.worldweatheronline.com/premium/v1/weather.ashx?key=0d29e282fb914f8bb29200426221311&q=Saint%20Petersburg&format=json&lang=ru&num_of_days=7&fx=yes&mca=no&tp=24
class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var sPrefs: SharedPreferences
    private lateinit var city: String
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var daysAdapter: DaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWeatherBinding.inflate(inflater)
        loadCity()
        loadWeather(city)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.reloadButton.setOnClickListener {
            loadWeather(city)
        }

        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.cityText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                inputMethodManager.hideSoftInputFromWindow(binding.cityText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                city = binding.cityText.text.toString()
                saveCity(city)
                loadWeather(city)
            }
            true
        }
    }

    private fun loadCity(): String{
        sPrefs = requireActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        city = sPrefs.getString(APP_SETTINGS_CITY, "Москва").toString()
        Log.d("WeatherFragment", "LOADED $city")
        return city
    }

    private fun saveCity(city: String){
        sPrefs = requireActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sPrefs.edit()
        editor.putString(APP_SETTINGS_CITY, city)
        editor.apply()
        Log.d("WeatherFragment", "SAVED $city")
    }

    private fun loadWeather(city: String){
        if (isNetworkAvailable()){
            showSuccessState()
            weatherViewModel.getWeatherInfo(city)
            weatherViewModel.responseCode.observe(viewLifecycleOwner){code ->
                when(code){
                    200 -> {
                        try {
                            binding.apply {
                                cityText.setText(weatherViewModel.weatherLiveData.value!!.data.request[0].query, TextView.BufferType.EDITABLE)
                                "${weatherViewModel.weatherLiveData.value!!.data.current_condition[0].temp_C} ℃".also { temperatureText.text = it }
                                descriptionText.text = weatherViewModel.weatherLiveData.value!!.data.current_condition[0].lang_ru[0].value
                                "Ощущается как ${weatherViewModel.weatherLiveData.value!!.data.current_condition[0].FeelsLikeC}°".also { maxT.text = it }
                                setupAdapter(weatherViewModel.weatherLiveData.value!!.data.weather)
                            }
                        }catch (e: NullPointerException){
                            //в связи с особенностями API с кодом 200 может прийти null, так что приходится делать проверку
                            Toast.makeText(context, "Город не найден", Toast.LENGTH_SHORT).show()
                        }
                    }
                    400 -> {
                        Toast.makeText(context, "Parameter key is missing from the request URL", Toast.LENGTH_SHORT).show()
                    }
                    401 -> {
                        Toast.makeText(context, "Key expired", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else showErrorState()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter(daysList: List<Weather>) {
        binding.apply {
            Log.d("WeatherFragment", "setupAdapter = $daysList")
            daysRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            daysAdapter = DaysAdapter(daysList)
            daysAdapter.notifyDataSetChanged()
            daysRecyclerView.adapter = daysAdapter
        }
    }


    private fun showSuccessState() {
        binding.apply {
            errorState.visibility = View.GONE
            successState.visibility = View.VISIBLE
        }
    }

    private fun showErrorState() {
        binding.apply {
            errorState.visibility = View.VISIBLE
            successState.visibility = View.GONE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }

    companion object {
        const val APP_SETTINGS = "mySettings"
        const val APP_SETTINGS_CITY = "city"
        fun newInstance() = WeatherFragment()
    }
}