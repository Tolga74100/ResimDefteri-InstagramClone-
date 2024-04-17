package com.tolga.resimdefteri.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tolga.resimdefteri.adapter.HomeRecyclerAdaper
import com.tolga.resimdefteri.databinding.FragmentHomePageBinding
import com.tolga.resimdefteri.databinding.FragmentProfileBinding
import com.tolga.resimdefteri.model.Post
import kotlinx.coroutines.currentCoroutineContext


class Profile : Fragment() {
    private lateinit var profileBinding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var homeAdapter: HomeRecyclerAdaper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postArrayList = ArrayList<Post>()

        auth = Firebase.auth
        db = Firebase.firestore



        getdata()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return profileBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileBinding.profileFragmentHomeButton.setOnClickListener {
            val action = ProfileDirections.actionProfileToHomePage()
            Navigation.findNavController(it).navigate(action)
        }
        profileBinding.profileFragmentDiscoverButton.setOnClickListener {
            val action = ProfileDirections.actionProfileToDiscover()
            Navigation.findNavController(it).navigate(action)
        }
        profileBinding.profileFragmentUploadButton.setOnClickListener {
            val action = ProfileDirections.actionProfileToUpload()
            Navigation.findNavController(it).navigate(action)
        }

        profileBinding.recyclerViewProfile.layoutManager = LinearLayoutManager(requireContext())

        homeAdapter = HomeRecyclerAdaper(postArrayList)
        profileBinding.recyclerViewProfile.adapter = homeAdapter
    }


    private  fun getdata (){
        val currentUser = auth.currentUser
        currentUser?.let {
            val email = it.email
        }
        db.collection("Posts").whereEqualTo("userEmail", currentUser?.email).addSnapshotListener { value, error ->
            if (error != null){
                Toast.makeText(requireContext(),error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                if (value != null){
                    if (!value.isEmpty){
                        val documents = value.documents
                        postArrayList.clear()
                        for (document in documents){
                            val comment = document.get("comment") as String
                            val useremail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(useremail,comment,downloadUrl)
                            postArrayList.add(post)

                        }
                        homeAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


}