package com.example.memorymirror.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorymirror.adapter.ItemAdapter
import com.example.memorymirror.database.MemoryMirrorDao
import com.example.memorymirror.database.MemoryMirrorEntity
import com.example.memorymirror.databinding.ActivityMainBinding
import com.example.memorymirror.util.MemoryMirrorApp
import com.example.memorymirror.util.SwipeToDeleteCallback
import com.example.memorymirror.util.SwipeToEditCallback
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private lateinit var memoryMirrorDao: MemoryMirrorDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.fabAddBtn?.setOnClickListener {
            val intent = Intent(this, AddMemoryPlaceActivity::class.java)
            startActivity(intent)
        }
        memoryMirrorDao = (application as MemoryMirrorApp).db.memoryMirrorDao()
        lifecycleScope.launch {
            memoryMirrorDao.fetchAllMemory().collect {
                val list = ArrayList(it)
                setUpRecycleView(list)
            }
        }
    }

    private fun setUpRecycleView(itemList: ArrayList<MemoryMirrorEntity>) {
        if (itemList.isNotEmpty()) {
            val adapter = ItemAdapter(itemList = itemList)
            binding?.rvMemoryMirror?.layoutManager = LinearLayoutManager(this)
            binding?.rvMemoryMirror?.adapter = adapter
            binding?.rvMemoryMirror?.visibility = View.VISIBLE
            binding?.tvNoMemory?.visibility = View.INVISIBLE
            adapter.setOnClickListener(object : ItemAdapter.OnClickListener {
                override fun onClick(position: Int, listItem: MemoryMirrorEntity) {
                    val intent = Intent(this@MainActivity, MemoryPlaceDetailActivity::class.java)
                    intent.putExtra(EXTRA_PLACE_DETAILS, listItem)
                    startActivity(intent)
                }

            })

            val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter1 = binding?.rvMemoryMirror?.adapter as ItemAdapter
                    val position = viewHolder.adapterPosition
                    val deletedItem = adapter1.removeItem(position)
                    deleteItemFromDatabase(deletedItem)
                }
            }
            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding?.rvMemoryMirror)


            val editSwapHandler = object : SwipeToEditCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter1 = binding?.rvMemoryMirror?.adapter as ItemAdapter
                    val position = viewHolder.adapterPosition
                    adapter1.editItemNotify(
                        this@MainActivity,
                        position = position,
                        requestCode = EXTRA_PLACE_DETAILS
                    )
                }
            }
            val editItemTouchHelper = ItemTouchHelper(editSwapHandler)
            editItemTouchHelper.attachToRecyclerView(binding?.rvMemoryMirror)

        } else {
            binding?.rvMemoryMirror?.visibility = View.GONE
            binding?.rvMemoryMirror?.visibility = View.VISIBLE
        }
    }


    private fun deleteItemFromDatabase(item: MemoryMirrorEntity) {
        lifecycleScope.launch {
            // Use your memoryMirrorDao to delete the item from the database
            memoryMirrorDao.delete(item)
        }
    }

    companion object {
        const val EXTRA_PLACE_DETAILS = "extra_place_detail"
    }

}

