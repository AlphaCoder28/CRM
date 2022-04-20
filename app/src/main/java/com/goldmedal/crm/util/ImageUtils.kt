package com.goldmedal.crm.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.goldmedal.crm.common.ImageSelectionListener
import java.io.*
import java.util.*


fun showPictureDialog(mContext: Context?, listener: ImageSelectionListener) {
    val pictureDialog = mContext?.let { AlertDialog.Builder(it) }
    pictureDialog?.setTitle("Choose")
    val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
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


fun saveImage(context: Context?, imageDirectory: String, myBitmap: Bitmap): String {
    val bytes = ByteArrayOutputStream()
    myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
    val wallpaperDirectory = File(
        (Environment.getExternalStorageDirectory()).toString() + imageDirectory
    )
    // have the object build the directory structure, if needed.
    Log.d("fee", wallpaperDirectory.toString())
    if (!wallpaperDirectory.exists()) {

        wallpaperDirectory.mkdirs()
    }

    try {
        Log.d("heel", wallpaperDirectory.toString())
        val f = File(
            wallpaperDirectory, ((Calendar.getInstance()
                .timeInMillis).toString() + ".jpg")
        )
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        MediaScannerConnection.scanFile(
            context,
            arrayOf(f.path),
            arrayOf("image/jpeg"), null
        )
        fo.close()
        Log.d("TAG", "File Saved::--->" + f.absolutePath)

        return f.absolutePath
    } catch (e1: IOException) {
        e1.printStackTrace()
    }

    return ""
}


fun convertBitmapToBase64(bitmap: Bitmap?): String {

    val baos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, baos)
    val data = baos.toByteArray()
   // val buffer: ByteBuffer = ByteBuffer.allocate(bitmap.rowBytes * bitmap.height)
   // bitmap.copyPixelsToBuffer(buffer)
  //  val data: ByteArray = buffer.array()

    return Base64.encodeToString(data, Base64.NO_WRAP)

}


fun scaleDown(
    realImage: Bitmap, maxImageSize: Float,
    filter: Boolean
): Bitmap? {
    val ratio = Math.min(
        maxImageSize / realImage.width,
        maxImageSize / realImage.height
    )
    if (ratio >= 1.0) {
        return realImage
    }
    val width = Math.round(ratio * realImage.width)
    val height = Math.round(ratio * realImage.height)
    return Bitmap.createScaledBitmap(realImage, width, height, filter)
}


fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "", null)
    return Uri.parse(path)
}


fun getBitmap(path: String?): Bitmap? {
    var bitmap: Bitmap? = null
        if(path !=null){
            try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
//        image.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
        }
    return null
}

fun getDrawable(context: Context, drawableResId: Int): Drawable? {
    return VectorDrawableCompat.create(context.resources, drawableResId, context.theme)
}

fun getDrawable(context: Context, drawableResId: Int, colorFilter: Int): Drawable {
    val drawable = getDrawable(context, drawableResId)
    drawable!!.setColorFilter(colorFilter, PorterDuff.Mode.SRC_IN)
    return drawable
}

fun getBitmap(context: Context, drawableId: Int): Bitmap {
    val drawable = getDrawable(context, drawableId)

    val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}