package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class DiaryFragment: Fragment() {
    protected val SAVE_IMAGE_REQUEST_CODE = 1999
    protected val CAMERA_REQUEST_CODE = 1998
    val CAMERA_PERMISSION_REQUEST_CODE = 1997
    private lateinit var mCurrentPhotoPath: String
    protected var mPhotoURI: Uri? = null

    /**
     * See if we have Permission or Not.
     */
    protected fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(context!!,  Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            takePhoto()
        else {
            val permissionRequest = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    protected fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePhotoIntent->
//            takePhotoIntent.resolveActivity(context!!.packageManager)?.also {
//                startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE)
//            }
            takePhotoIntent.resolveActivity(context!!.packageManager)
            if (takePhotoIntent == null){
                Toast.makeText(context, "Unable To Save Photo!", Toast.LENGTH_SHORT).show()
            }else {
                val photoFile = createImageFile()
                Log.v(MainFragment.TAG,"\t\tphotoFile:: $photoFile")

                photoFile?.also {
                    mPhotoURI = FileProvider.getUriForFile(activity!!.applicationContext, "com.janus.a2a1myplatdiarybrandanjones.fileprovider", it)
                    Log.v(MainFragment.TAG,"\t\tphotoURI:: $photoFile")
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI)
                    startActivityForResult(takePhotoIntent, SAVE_IMAGE_REQUEST_CODE)
                }
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("HHmmss_ddMMYYYY", Locale.getDefault()).format(Date())
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PlantDiary$timeStamp",".jpg", storageDir).apply {
            mCurrentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePhoto()
                else {
                    Toast.makeText(context, "Unable To Take Photo Without Permission", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}