package com.example.daytripplanner_project1

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity

class DetailAdapter(val details: List<Detail>) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_nearby,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRow = details[position]

        holder.address.text = currentRow.address
        holder.address2.text = currentRow.address2
        holder.locationName.text = currentRow.name
        if(currentRow.pricePoint == "None" || currentRow.pricePoint.isEmpty()){
            holder.pricePoint.visibility = View.INVISIBLE
        } else {
            holder.pricePoint.visibility = View.VISIBLE
            holder.pricePoint.text = currentRow.pricePoint
        }
        holder.rating.numStars = currentRow.rating
        if(currentRow.phone.isEmpty()){
            holder.callButton.visibility = View.INVISIBLE
        } else {
            holder.callButton.visibility = View.VISIBLE
            holder.callButton.setOnClickListener{
                val intent: Intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${currentRow.phone}"))
                // For some reason this is the only way I can get the external intent to function correctly
                holder.itemView.context.startActivity(intent)
            }
        }
        if(currentRow.url.isEmpty()){
            holder.webButton.visibility = View.INVISIBLE
        } else {
            holder.webButton.visibility = View.VISIBLE
            holder.webButton.setOnClickListener{
                val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("${currentRow.url}"))
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address: TextView = itemView.findViewById(R.id.address)
        val address2: TextView = itemView.findViewById(R.id.address2)
        val locationName: TextView = itemView.findViewById(R.id.locationName)
        val callButton: ImageButton = itemView.findViewById(R.id.callButton)
        val webButton: ImageButton = itemView.findViewById(R.id.webButton)
        val pricePoint: TextView = itemView.findViewById(R.id.pricePoint)
        val rating: RatingBar = itemView.findViewById(R.id.ratingBar)
    }
}