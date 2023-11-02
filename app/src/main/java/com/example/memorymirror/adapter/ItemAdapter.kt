package com.example.memorymirror.adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memorymirror.activities.AddMemoryPlaceActivity
import com.example.memorymirror.database.MemoryMirrorEntity
import com.example.memorymirror.databinding.RvMemoryMirrorItemsBinding

class ItemAdapter(
    private val itemList: ArrayList<MemoryMirrorEntity>
) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    class ViewHolder(binding: RvMemoryMirrorItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.rvImage
        val title = binding.rvTvTitle
        val description = binding.rvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvMemoryMirrorItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }



    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, listItem: MemoryMirrorEntity)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.image.setImageURI(Uri.parse(item.image))
        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, item)
            }
        }
    }

    fun removeItem(position: Int): MemoryMirrorEntity {
        notifyItemRemoved(position)
        return itemList.removeAt(position)
    }

    fun editItemNotify(activity : Activity , position : Int , requestCode : String) {
        val editItem = itemList[position]
        val intent = Intent(activity,AddMemoryPlaceActivity::class.java)
        notifyItemChanged(position)
       intent.putExtra(requestCode,editItem)
        activity.startActivity(intent)
    }


}