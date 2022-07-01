package ru.netology.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.maps.R
import ru.netology.maps.adapter.PlaceAdapter
import ru.netology.maps.adapter.PlaceCallback
import ru.netology.maps.databinding.FragmentPlaceBinding
import ru.netology.maps.dto.Place
import ru.netology.maps.viewmodel.PlaceViewModel

class PlaceFragment : Fragment() {
    private val viewModel: PlaceViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private val bundle = Bundle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlaceBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PlaceAdapter(object : PlaceCallback {
            override fun onShow(place: Place) {
                val bundle = Bundle().apply {
                    putDouble("lat", place.latitude)
                    putDouble("lng", place.longitude)
                }
                findNavController().navigate(
                    R.id.action_placeFragment_to_mapsFragment,
                    bundle
                )
            }

            override fun onEdit(place: Place) {
                viewModel.edit(place)
                val bundle = Bundle().apply {
                    putString("name", place.name)
                    putString("description", place.description)
                }
                findNavController().navigate(
                    R.id.action_placeFragment_to_newPlaceFragment,
                    bundle
                )
            }

            override fun onRemove(place: Place) {
                viewModel.removeById(place.id)
            }
        })

        binding.recycler.adapter = adapter
        viewModel.edit(Place.empty)
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val recyclerComparsion = adapter.itemCount < state.places.size
            adapter.submitList(state.places) {
                if (recyclerComparsion) binding.recycler.scrollToPosition(0)
            }
        }

        return binding.root
    }
}