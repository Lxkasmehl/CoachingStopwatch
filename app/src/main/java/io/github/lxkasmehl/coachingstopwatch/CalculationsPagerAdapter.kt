package io.github.lxkasmehl.coachingstopwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CalculationData(val description: String)

class CalculationsPagerAdapter(private val calculations: List<CalculationData>) :
    RecyclerView.Adapter<CalculationsPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calculation_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calculation = calculations[position]
        holder.description.text = calculation.description
    }

    override fun getItemCount(): Int = calculations.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.description)
    }
}