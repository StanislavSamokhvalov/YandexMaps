package ru.netology.maps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.maps.R
import ru.netology.maps.databinding.CardPlaceBinding
import ru.netology.maps.dto.Place

interface PlaceCallback {
    fun onShow(place: Place) {}
    fun onEdit(place: Place) {}
    fun onRemove(place: Place) {}
}

class PlaceAdapter(
    private val onInteractionListener: PlaceCallback,
) : ListAdapter<Place, PlaceViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = CardPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }
}

class PlaceViewHolder(
    private val binding: CardPlaceBinding,
    private val placeCallback: PlaceCallback,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(place: Place) {
        binding.apply {
            name.text = place.name
            description.text = place.description

            menu.setOnClickListener{
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.place_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.place_remove -> {
                                placeCallback.onRemove(place)
                                true
                            }
                            R.id.place_edit -> {
                                placeCallback.onEdit(place)
                                true
                            }
                            R.id.place_open -> {
                                placeCallback.onShow(place)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            group.addAllOnClickListener{placeCallback.onShow(place)}
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
        return oldItem == newItem
    }
}

fun Group.addAllOnClickListener(listener: View.OnClickListener?) {
    referencedIds.forEach { id ->
        rootView.findViewById<View>(id).setOnClickListener(listener)
    }
}
