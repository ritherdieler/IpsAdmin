package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dscorp.ispadmin.R

class TableAdapter(
    val context: Context,
    private val categories: Array<String>,
    val values: Array<Double>
) : BaseAdapter() {

    override fun getCount() = categories.size

    override fun getItem(position: Int) = categories[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.table_layout_dashboard, parent, false)

        val categoryText = view.findViewById<TextView>(R.id.row_key)
        val valueText = view.findViewById<TextView>(R.id.row_value)

        categoryText.text = categories[position]
        valueText.text = valueText.toString()
        return view
    }
}