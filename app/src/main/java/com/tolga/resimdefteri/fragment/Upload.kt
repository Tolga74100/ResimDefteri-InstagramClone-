package com.tolga.resimdefteri.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.tolga.resimdefteri.databinding.FragmentUploadBinding
import java.util.UUID


class Upload : Fragment() {
    private lateinit var uploadBinding: FragmentUploadBinding
    private lateinit var activityResultLouncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLouncher: ActivityResultLauncher<String>


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    var selectedPicture: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLouncher()


        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        uploadBinding = FragmentUploadBinding.inflate(inflater, container, false)
        return uploadBinding.root



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadBinding.uploadFragmentHomeButton.setOnClickListener {
            val action = UploadDirections.actionUploadToHomePage()
            Navigation.findNavController(it).navigate(action)
        }
        uploadBinding.uploadFragmentDiscoverButton.setOnClickListener {
            val action = UploadDirections.actionUploadToDiscover()
            Navigation.findNavController(it).navigate(action)
        }
        uploadBinding.uploadFragmentProfileButton.setOnClickListener {
            val action = UploadDirections.actionUploadToProfile()
            Navigation.findNavController(it).navigate(action)
        }

        uploadBinding.uploadButton.setOnClickListener {

        }


        uploadBinding.selectImageView.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                        Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                            permissionLouncher.launch(Manifest.permission.READ_MEDIA_IMAGES) }).show()
                    } else {
                        permissionLouncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLouncher.launch(intentToGallery)

                }

            }else{
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener {
                            permissionLouncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }).show()
                    } else {
                        permissionLouncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                } else {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLouncher.launch(intentToGallery)

                }
            }

        }

        uploadBinding.uploadButton.setOnClickListener {
            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpg"

            val referance = storage.reference
            val imageReferance = referance.child("images").child(imageName)

            if (selectedPicture != null){

                imageReferance.putFile(selectedPicture!!).addOnSuccessListener {
                    val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        if (auth.currentUser != null){
                            val postMap = hashMapOf<String,Any>()
                            postMap.put("downloadUrl", downloadUrl)
                            postMap.put("userEmail", auth.currentUser!!.email!!)
                            postMap.put("comment", uploadBinding.commonText.text.toString())
                            postMap.put("date", Timestamp.now())

                            firestore.collection("Posts").add(postMap).addOnSuccessListener {
                                val action = UploadDirections.actionUploadToHomePage()
                                Navigation.findNavController(view).navigate(action)

                            }.addOnFailureListener {
                                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                            }
                        }

                    }

                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }

        }

    }




    private fun registerLouncher() {
        activityResultLouncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let { uploadBinding.selectImageView.setImageURI(it)
                        }
                    }
                }
            }

        permissionLouncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLouncher.launch(intentToGallery)
            }else{
                //permission dineted
                Toast.makeText(requireContext(),"Pemission needed", Toast.LENGTH_LONG).show()
            }
        }
    }



}