package com.snotshot.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.snotshot.myapplication.R
import com.snotshot.myapplication.databinding.ItemSpinnerBinding
import com.snotshot.myapplication.models.University

class CustomSpinnerAdapter(context: Context, items: List<University>) : ArrayAdapter<University>(context, R.layout.item_spinner, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = convertView?.tag as? ItemSpinnerBinding ?: ItemSpinnerBinding.inflate(
            LayoutInflater.from(context), parent, false).also{
                item ->
            item.root.tag = item
        }
        val item = getItem(position)

        binding.textName.text = item?.name
        binding.textDescription.text = item?.description
        item?.avatarResId?.let {
            binding.imageAvatar.setImageResource(it)
        }
        return binding.root
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val textView = convertView as? TextView ?: TextView(context)

        val item = getItem(position)
        textView.text = "${item?.name}: ${item?.description}"

        return textView
    }

}