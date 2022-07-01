package ru.netology.maps.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.netology.maps.R
import ru.netology.maps.databinding.FragmentInfoBinding


class InformationFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentInfoBinding.inflate(
            inflater,
            container,
            false
        )

        binding.apply {
            infoHeader.setText(R.string.app_name)
            infoContent.setText(R.string.information_content)
        }

        return binding.root
    }
}