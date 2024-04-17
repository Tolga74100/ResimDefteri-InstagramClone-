package com.tolga.resimdefteri.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.tolga.resimdefteri.databinding.FragmentDiscoverBinding


class Discover : Fragment() {
    private lateinit var discoverBinding: FragmentDiscoverBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        discoverBinding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return discoverBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discoverBinding.discoverFragmentHomeButton.setOnClickListener {
            val action = DiscoverDirections.actionDiscoverToHomePage()
            Navigation.findNavController(it).navigate(action)
        }

        discoverBinding.discoverFragmentUploadButton.setOnClickListener {
            val action = DiscoverDirections.actionDiscoverToUpload()
            Navigation.findNavController(it).navigate(action)
        }

        discoverBinding.discoverFragmentProfileButton.setOnClickListener {
            val action = DiscoverDirections.actionDiscoverToProfile()
            Navigation.findNavController(it).navigate(action)
        }
    }


}