package com.example.weatherforecast.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.DayCardBinding
import com.example.weatherforecast.service.model.Weather
import com.squareup.picasso.Picasso;

class DaysAdapter(private val daysList:List<Weather>): RecyclerView.Adapter<DaysAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = DayCardBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bind(day: Weather) = with(binding){
            binding.apply {
                dateTextview.text = day.date
                weatherTextview.text = day.hourly[0].lang_ru[0].value
                temperatureTextview.text = day.avgtempC + "℃"
                Picasso.get().load(day.hourly[0].weatherIconUrl[0].value).into(imageView);
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.day_card, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(daysList[position])
    }
    override fun getItemCount(): Int {
        return daysList.size
    }
}