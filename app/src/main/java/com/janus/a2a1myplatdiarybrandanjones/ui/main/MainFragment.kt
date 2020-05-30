package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.janus.a2a1myplatdiarybrandanjones.R
import com.janus.a2a1myplatdiarybrandanjones.dto.Specimen
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

//import java.util.jar.Manifest

class MainFragment : Fragment() {
    private var user: FirebaseUser? = null
    private val AUTH__REQUEST_CODE = 2002
    private val LOCATION_PERMISSION_REQUEST_CODE = 2001
    private val IMAGE_GALLERY_REQUEST_CODE = 2000
    private val SAVE_IMAGE_REQUEST_CODE = 1999
    private val CAMERA_REQUEST_CODE = 1998
    val CAMERA_PERMISSION_REQUEST_CODE = 1997


    companion object {
        val TAG = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var currentPhotoPath: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.plants.observe(viewLifecycleOwner, Observer {
            Log.v(TAG, "\t\t Number of Plants Returned:: ${it.size}")
            actvPlantName.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, it))
        })

        viewModel.specimens.observe(viewLifecycleOwner, Observer { specimens ->
            spnSpecimens.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, specimens)
        })

        btnTakePhoto.setOnClickListener {
            prepTakePhoto()
        }
        btnLogin.setOnClickListener{
            //prepOpenImageGalary()

            if (user == null)
                login()
            else
                signOut() //THIS IS FOR TESTING PURPOSES, I WILL USE A LOGOUT MENU.
        }
        btnSave.setOnClickListener{
            saveSpecimen()
        }
        prepRequestLocationUpdates()
    }

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build())
        //TODO: ADD TWITTER, MICROSOFT, GITHUB AND YAHOO.

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), AUTH__REQUEST_CODE)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(activity!!.applicationContext)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    Log.e(TAG, "\t\tFAILED TO COMPLETE LOGOUT")
                if (it.isComplete)
                    Log.e(TAG, "\t\tLOGGED OUT COMPLETED SUCCESSFULLY")

            }

    }
    private fun saveSpecimen() {
        val specimen = Specimen().apply {
            latitude = lbllatitudeValue.text.toString()
            longitude = lbllongitudeValue.text.toString()
            plantName = actvPlantName.text.toString()
            description = etDescription.text.toString()
            datePlanted = etDatePlanted.text.toString()

        }
        viewModel.save(specimen)
    }

    private fun prepRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            requestLocationUpdated()
        }else {
            // TODO: Consider calling  ActivityCompat#requestPermissions here to request the missing
            // permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            val permissionRequest = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permissionRequest, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun requestLocationUpdated() {
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        locationViewModel.getLocationLiveData().observe(viewLifecycleOwner, Observer {
            lbllatitudeValue.text = it.latitude
            lbllongitudeValue.text = it.longitude
        })
    }

    private fun prepOpenImageGalary() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            startActivityForResult(this, IMAGE_GALLERY_REQUEST_CODE)
        }
    }

    /**
     * See if we have Permission or Not.
     */
    private fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(context!!,  Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            takePhoto()
        else {
            val permissionRequest = arrayOf(Manifest.permission.CAMERA)

            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePhoto()
                else {
                    Toast.makeText(context, "Unable To Take Photo Without Permission", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestLocationUpdated()
                else {
                    Toast.makeText(context, "Unable To Update Location Without Permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePhotoIntent->
//            takePhotoIntent.resolveActivity(context!!.packageManager)?.also {
//                startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE)
//            }
            takePhotoIntent.resolveActivity(context!!.packageManager)
            if (takePhotoIntent == null){
                Toast.makeText(context, "Unable To Save Photo!", Toast.LENGTH_SHORT).show()
            }else {
                val photoFile = createImageFile()
                photoFile.also {
                    val photoURI = FileProvider.getUriForFile(activity!!.applicationContext, "com.janus.a2a1myplatdiarybrandanjones.fileprovider", it)
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
                    startActivityForResult(takePhotoIntent, SAVE_IMAGE_REQUEST_CODE)
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, bundle: Intent?) {
        super.onActivityResult(requestCode, resultCode, bundle)
        if (resultCode == RESULT_OK)
            when (requestCode) {
                CAMERA_REQUEST_CODE ->{
                    val imageBitMap = bundle!!.extras!!.get("data") as Bitmap
                    imgPlant.setImageBitmap(imageBitMap)
                }
                SAVE_IMAGE_REQUEST_CODE -> {
                    Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
                }
                IMAGE_GALLERY_REQUEST_CODE ->{
                    if (bundle != null && bundle.data != null) {
                        val image = bundle.data
                        //TODO: the linese below require AndroidP.
                        val source = ImageDecoder.createSource(activity!!.contentResolver, image!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imgPlant.setImageBitmap(bitmap)

                    }
                }
                AUTH__REQUEST_CODE -> {
                    user = FirebaseAuth.getInstance().currentUser
                }
            }
    }

    private fun createImageFile(): File{
        val timeStamp = SimpleDateFormat("HHmmss_ddMMYYYY", Locale.getDefault()).format(Date())
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("PlantDiary$timeStamp",".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

}
