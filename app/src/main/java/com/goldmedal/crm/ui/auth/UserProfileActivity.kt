package com.goldmedal.crm.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ImageSelectionListener
import com.goldmedal.crm.data.model.profileDetailData
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.databinding.ActivityUserProfileBinding
import com.goldmedal.crm.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File

//<!--added by shetty 6 jan 21-->
class UserProfileActivity : AppCompatActivity(), KodeinAware, ImageSelectionListener,AuthListener<Any>{

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()
    private lateinit var viewModel: LoginViewModel
    var profilePhoto = String()
    var userID = 0
    var profilePhotoLink = String()

    private var _binding: ActivityUserProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile)

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        viewModel.authListener = this

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                userID = user.UserId ?: 0
                viewModel.getProfileDetail(userID)
            }
        })


        binding.imvRemovePhoto.setOnClickListener {
            if(!profilePhotoLink.isEmpty() && !profilePhotoLink.equals("-")) {

                    MaterialAlertDialogBuilder(this)

                        .setMessage("Do you want to remove your Profile image?")

                        .setPositiveButton(resources.getString(R.string.str_yes)) { dialog, which ->
                            Glide.with(this)
                                .load("")
                                .fitCenter()
                                .placeholder(R.drawable.male_avatar)
                                .into(imgprofile)

                            binding.rootLayout.snackbar("Profile Photo removed successfully")
                            profilePhoto = ""

                            viewModel.onUpdateProfilePhoto(
                                userId = userID,
                                ProfilePhoto = profilePhoto
                            )
                        }

                        .setNegativeButton(resources.getString(R.string.str_no)) { dialog, which ->
                            finish()
                        }

                        .show()


            }else{
                binding.rootLayout.snackbar("No Profile Photo Uploaded")
            }
        }


        // - -  - - for selfie upload
        binding.btnAddProfilePic.setOnClickListener {
            openDocumentsDialog(this, this)
        }

    }


    private fun bindUI(user: profileDetailData?) = Coroutines.main {
        user?.let {
            binding.tvUserName.text = user.UserFullName
            binding.tvEmployeeCode.text = user.EmpCode
            binding.tvJoiningDate.text =
                formatDateString(user.JoiningDate ?: "", "MM/dd/yyyy hh:mm:ss a", "dd/MM/yyyy")
            binding.tvUserMobNo.text = user.UserPhone
            binding.tvOfficeEmail.text = user.UserEmail
            binding.tvUserServiceCentre.text = user.ServiceCenter
            binding.tvScAddress.text = user.ScAddress
            binding.tvUserWorkExp.text = "Experience : " + user.WorkExp
            binding.tvHighestQualification.text = user.HighestQualification
            binding.tvHomeAddress.text = user.Address

            profilePhotoLink = user?.ProfilePhoto ?: ""
            Glide.with(this)
                .load(profilePhotoLink)
                .fitCenter()
                .placeholder(R.drawable.male_avatar)
                .into(imgprofile)
        }
    }


    fun openDocumentsDialog(mContext: Context?, listener: ImageSelectionListener) {
        val pictureDialog = mContext?.let { AlertDialog.Builder(it) }
        pictureDialog?.setTitle("Choose")
        val pictureDialogItems =
            arrayOf("Select document from gallery", "Capture photo from camera")
        pictureDialog?.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> listener.choosePhotoFromGallery()
                1 -> listener.takePhotoFromCamera()
            }
        }
        pictureDialog?.show()
    }

    override fun choosePhotoFromGallery() {
        onClickRequestPermissionStorageButton()
    }

    override fun takePhotoFromCamera() {
        onClickRequestPermissionCameraButton()
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    private fun onClickRequestPermissionCameraButton() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, RC_CAMERA_PERM)
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_camera_rationale_message),
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    private fun onClickRequestPermissionStorageButton() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            // Have permission, do the thing!
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            val mimetypes = arrayOf("image/*", "application/pdf")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
            startActivityForResult(intent, RC_STORAGE_PERM)
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_storage_rationale_message),
                RC_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_CAMERA_PERM) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                prepareImgUpload(thumbnail)
            }
        } else if (requestCode == RC_STORAGE_PERM && resultCode == Activity.RESULT_OK) {
            val uri: Uri
            if (data != null) {
                uri = data.data!!

                val filePath = getPathFromUri(this, uri)
//                assert uri != null;
//                filePath = uri.getPath();
                if (filePath != null) {
                    val file = File(filePath)

                    // Get length of file in bytes
                    val fileSizeInBytes = file.length()
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    val fileSizeInKB = fileSizeInBytes / 1024
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB > 4) {
                        Toast.makeText(
                            this,
                            "Cannot attach file more than 4 Mb",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }


                    val thumbnail = getBitmap(filePath)
                    if (thumbnail != null) {
                        prepareImgUpload(thumbnail)
                    } else {
                        Toast.makeText(this, "No Image Found", Toast.LENGTH_SHORT).show()
                    }

                }
            }

        }
    }


    private fun prepareImgUpload(thumbnail: Bitmap) {
    //    Toast.makeText(this, thumbnail.toString(), Toast.LENGTH_SHORT).show()

        val scaledBitmap = scaleDown(thumbnail, GlobalConstant.FULL_IMAGE_SIZE, true)

        profilePhoto = convertBitmapToBase64(scaledBitmap)

        val displayedBitmap = scaleDown(thumbnail, GlobalConstant.THUMBNAIL_SIZE, true)

      //  imgprofile.setImageBitmap(displayedBitmap)

        Glide.with(this)
            .load(displayedBitmap)
            .fitCenter()
            .placeholder(R.drawable.male_avatar)
            .into(imgprofile)

     //   Toast.makeText(this, "Image attached successfully", Toast.LENGTH_SHORT).show()

        viewModel.onUpdateProfilePhoto(
            userId = userID,
            ProfilePhoto = profilePhoto
        )
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val RC_CAMERA_PERM = 122
        private const val RC_STORAGE_PERM = 123
        private const val REFRESH_RESULT_CODE = 102

        fun start(context: Context) {
            context.startActivity(Intent(context, UserProfileActivity::class.java))
        }
    }

    override fun onStarted() {
            progress_bar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        if(callFrom.equals("profile_photo_update")){
            progress_bar.stop()
            toast("Profile Photo Updated Successfully")
        }

        if(callFrom.equals("profile_detail")){
            progress_bar.stop()
            val data = _object as List<profileDetailData?>

            if(data.count()>0){
                bindUI(data[0])
            }
        }

    }

    override fun onFailure(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar.stop()
        if(callFrom.equals("profile_photo_update")){
            toast(message)
        }
    }

    override fun onValidationError(message: String) {
        progress_bar.stop()
    }
}
