package ru.netology.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.maps.R
import ru.netology.maps.databinding.FragmentNewPlaceBinding
import ru.netology.maps.viewmodel.PlaceViewModel

class NewPlaceFragment : Fragment() {
    private val viewModel: PlaceViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val latitude = requireArguments().getDouble("latitude")
        val longitude = requireArguments().getDouble("longitude")
        val name = arguments?.getString("name")
        val description = arguments?.getString("description")

        val binding = FragmentNewPlaceBinding.inflate(inflater, container, false)
        binding.apply {
            nameFieldEdit.requestFocus()
            nameFieldEdit.setText(name)
            descriptionFieldEdit.setText(description)

            save.setOnClickListener {
                val nameNewPlace = nameFieldEdit.text.toString()
                val descriptionNewPlace = descriptionFieldEdit.text.toString()

                if (nameNewPlace.isEmpty()) {
                    Snackbar.make(
                        binding.root,
                        R.string.error_empty_name,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    viewModel.changeName(nameNewPlace)
                    viewModel.changeDescription(descriptionNewPlace)
                    viewModel.save(latitude, longitude)
                    val bundle = Bundle().apply {
                        putDouble("lat", requireArguments().getDouble("latitude"))
                        putDouble("lng", requireArguments().getDouble("longitude"))
                    }
                    findNavController().navigate(
                        R.id.action_newPlaceFragment_to_mapsFragment,
                        bundle
                    )
                }
            }
        }
        return binding.root
    }
}

