package com.janus.a2a1myplatdiarybrandanjones.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.janus.a2a1myplatdiarybrandanjones.R
import com.janus.a2a1myplatdiarybrandanjones.dto.PlantEvent
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class DiaryFragment: Fragment() {
    protected val SAVE_IMAGE_REQUEST_CODE = 1999
    protected val CAMERA_REQUEST_CODE = 1998
    val CAMERA_PERMISSION_REQUEST_CODE = 1997
    private lateinit var mCurrentPhotoPath: String
    protected var mPhotoURI: Uri? = null
    private val TAG = DiaryFragment::class.simpleName
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

    inner class EventsAdapter(val events: List<PlantEvent>, val itemLayout: Int ):
        RecyclerView.Adapter<EventsViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent,false)
            return EventsViewHolder(view)
        }

        override fun getItemCount() = events.size


        override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
            holder.bind(events[position])
        }
    }
    inner class EventsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val imgEventThumbnail:ImageView = itemView.findViewById(R.id.imgEventThumbnail)
        private val lblEventInfo:TextView = itemView.findViewById(R.id.lblEventInfo)

        //        @RequiresApi(Build.VERSION_CODES.P)
//@RequiresApi(Build.VERSION_CODES.KITKAT)
        fun bind(plantEvent: PlantEvent){
            lblEventInfo.text = plantEvent.toString()
            if (plantEvent.localPhotoURI != null && plantEvent.localPhotoURI != "null" ) {
//                val file = File(plantEvent.localPhotoURI!!)
//                if (file.exists()){
//                    imgEventThumbnail.setImageURI(Uri.parse(plantEvent.localPhotoURI))
//                }else{
//
//                    Log.e(TAG, "\t\tFILE DOES NOT EXISTS.. :: FILE-> ${file.absoluteFile}, \nURI-> ${plantEvent.localPhotoURI!!}")
//                }

                imgEventThumbnail.setImageURI(Uri.parse(plantEvent.localPhotoURI))


//                val source = ImageDecoder.createSource(activity!!.contentResolver, Uri.parse(plantEvent.localPhotoURI)))
//                val bitmap = ImageDecoder.decodeBitmap(source)
//                imgPlant.setImageBitmap(bitmap)
            }
        }
    }
}