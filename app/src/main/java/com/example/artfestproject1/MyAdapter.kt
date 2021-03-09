package com.example.artfestproject1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView


internal class MainAdapter(
    private val context: Context,
    private val numberImage: MutableList<Int>,
    private val numberColor: MutableList<Int>
) :
        BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var imageView: ImageView
    private lateinit var statusColor: ImageView
//    private lateinit var textView: TextView
    override fun getCount(): Int {
//        return numbersInWords.size
        return numberImage.size
    }
    override fun getItem(position: Int): Any? {
        return null
    }
    override fun getItemId(position: Int): Long {
        return 0
    }
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.activity_grid_view_item, null)
        }
        imageView = convertView!!.findViewById(R.id.icon)
        statusColor = convertView!!.findViewById(R.id.statusColor)
//        textView = convertView.findViewById(R.id.textView)
        imageView.setImageResource(numberImage[position])
        statusColor.setBackgroundColor(numberColor[position])
//        textView.text = numbersInWords[position]
        return convertView
    }
}