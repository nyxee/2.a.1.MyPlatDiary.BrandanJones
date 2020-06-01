package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.janus.a2a1myplatdiarybrandanjones.R
import com.janus.a2a1myplatdiarybrandanjones.dto.Photo
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import com.janus.a2a1myplatdiarybrandanjones.dto.Specimen
import kotlinx.android.synthetic.main.main_fragment.*
import kotlin.collections.ArrayList

//import java.util.jar.Manifest

class MainFragment : DiaryFragment() {
    private var _mPlantId: Int = 0
    private var mUser: FirebaseUser? = null
    private val AUTH__REQUEST_CODE = 2002
    private val LOCATION_PERMISSION_REQUEST_CODE = 2001
    private val IMAGE_GALLERY_REQUEST_CODE = 2000


    companion object {
        val TAG = MainFragment::class.java.simpleName
        fun newInstance() = MainFragment()
    }

    private lateinit var mViewModel: MainViewModel
    private lateinit var mLocationViewModel: LocationViewModel

    private var mPhotos = ArrayList<Photo>()
    private var mSpecimen = Specimen()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        activity?.let {
            mViewModel = ViewModelProvider(it).get(MainViewModel::class.java)
        }

        mViewModel.plants.observe(viewLifecycleOwner, Observer {
            Log.v(TAG, "\t\t Number of Plants Returned:: ${it.size}")
            actvPlantName.setAdapter(ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, it))
        })
        mViewModel.specimens.observe(viewLifecycleOwner, Observer { specimens ->
            spnSpecimens.adapter = ArrayAdapter(context!!, R.layout.support_simple_spinner_dropdown_item, specimens)
        })

        actvPlantName.setOnItemClickListener { parent, view, position, id ->
            val selectedPlant = parent.getItemAtPosition(position) as Plant
            _mPlantId = selectedPlant.plantId
        }

        btnTakePhoto.setOnClickListener {
            prepTakePhoto()
        }
        btnLogin.setOnClickListener{
            //prepOpenImageGalary()

            if (mUser == null)
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
        if (mUser == null) {
            login()
        }
        mUser ?: return

        storeSpecimen()
        mViewModel.save(mSpecimen, mPhotos, mUser!!)
        mSpecimen = Specimen() //Clear Memory, and prepare phosts array for next batch of photos.
        mPhotos = ArrayList()
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
        mLocationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        mLocationViewModel.getLocationLiveData().observe(viewLifecycleOwner, Observer {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestLocationUpdated()
                else {
                    Toast.makeText(context, "Unable To Update Location Without Permission", Toast.LENGTH_SHORT).show()
                }
            }
            else ->  super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

//    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, bundle: Intent?) {
        super.onActivityResult(requestCode, resultCode, bundle)
        if (resultCode == RESULT_OK)
            when (requestCode) {
                CAMERA_REQUEST_CODE ->{
                    showImage(bundle)

                }
                SAVE_IMAGE_REQUEST_CODE -> {
                    Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show()
                    showImage()
                    val photo = Photo(localUri = mPhotoURI.toString(), description = etDescription.text.toString() )
                    Log.v(TAG, "\t\tStored Photo in local list")

                    mPhotos.add(photo)
                    mPhotos.forEach {
                        Log.v(TAG, "\t\t$it")
                    }
                }
                IMAGE_GALLERY_REQUEST_CODE ->{
                    if (bundle != null && bundle.data != null) {
                        val image = bundle.data
                        //TODO: the linese below require AndroidP.
//                        val source = ImageDecoder.createSource(activity!!.contentResolver, image!!)
//                        val bitmap = ImageDecoder.decodeBitmap(source)
//                        imgPlant.setImageBitmap(bitmap)
                        //TODO: isnt the line below enough:
                        imgPlant.setImageURI(image)

                    }
                }
                AUTH__REQUEST_CODE -> { //the Login Activity returns here.
                    mUser = FirebaseAuth.getInstance().currentUser
                }
            }
    }

    private fun showImage(bundle: Intent?) {
        val imageBitMap = bundle!!.extras!!.get("data") as Bitmap
        imgPlant.setImageBitmap(imageBitMap)
    }

    private fun showImage() {

        imgPlant.setImageURI(mPhotoURI)
    }

    /**
     * this function takes all info in our views and some from the Plant Entity and saves it in our local specimen object and puts it in the viewModel.
     */
    fun storeSpecimen() {
        mSpecimen.apply {
            plantName = actvPlantName.text.toString()
            description = etDescription.text.toString()
            datePlanted = etDatePlanted.text.toString()
            plantId = _mPlantId
            latitude = lbllatitudeValue.text.toString()
            longitude = lbllongitudeValue.text.toString()
        }
        mViewModel.specimen = mSpecimen
    }
}
