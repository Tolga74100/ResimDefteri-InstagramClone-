package com.tolga.resimdefteri.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
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
import com.tolga.resimdefteri.model.Post


class HomePage : Fragment() {
   private lateinit var homePageBinding: FragmentHomePageBinding
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
        homePageBinding =FragmentHomePageBinding.inflate(inflater, container, false)
        return homePageBinding.root






    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homePageBinding.homeFragmentDiscoverButton.setOnClickListener {
            val action = HomePageDirections.actionHomePageToDiscover()
            Navigation.findNavController(it).navigate(action)
        }
        homePageBinding.homeFragmentUploadButton.setOnClickListener {
            val action = HomePageDirections.actionHomePageToUpload()
            Navigation.findNavController(it).navigate(action)
        }
        homePageBinding.homeFragmentProfileButton.setOnClickListener {
            val action = HomePageDirections.actionHomePageToProfile()
            Navigation.findNavController(it).navigate(action)
        }

        homePageBinding.recylerView.layoutManager = LinearLayoutManager(requireContext())

        homeAdapter = HomeRecyclerAdaper(postArrayList)
        homePageBinding.recylerView.adapter = homeAdapter
    }

    private fun getdata (){
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
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