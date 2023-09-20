package com.goldmedal.crm.util

import android.Manifest
import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.goldmedal.crm.BuildConfig
import com.goldmedal.crm.common.ImageSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.vmadalin.easypermissions.EasyPermissions
import java.io.*
import java.math.BigDecimal
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {

    //   Log.d("CalendarDate", "DAY_OF_MONTH - - - -" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    // Log.d("CalendarDate", "MONTH - - - -" + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
    // Log.d("CalendarDate", "DAY_OF_WEEK_IN_MONTH - - - -" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH));


    return Calendar.getInstance().time
}


fun formatDateString(
    rawString: String,
    inputFormat: String,
    outputFormat: String,
    locale: Locale = Locale.getDefault()
): String {
    val date: Date?
    val inputFormatter = SimpleDateFormat(inputFormat, locale)
    val outputFormatter = SimpleDateFormat(outputFormat, locale)
    try {
        date = inputFormatter.parse(rawString)
        date?.let {
            return outputFormatter.format(date)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}


fun getDateFromString(rawString: String, format: String, locale: Locale = Locale.getDefault()): Date? {
    try {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.parse(rawString);
    } catch (e: ParseException) {

        e.printStackTrace();
    }
    return Date()
}


public fun getAddressFromLatLong(mContext: Context?, latitude: Double, longitude: Double): String? {
    val addresses: List<Address>
    val geocoder = mContext?.let { Geocoder(it, Locale.getDefault()) }
    var cityAdd: String? = "Unnamed Road"
    try {
        addresses = geocoder?.getFromLocation(latitude, longitude, 1) as List<Address> // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if (addresses.isNotEmpty()) {
            cityAdd = addresses[0].getAddressLine(0)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return cityAdd
}


public fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
    val coder = context?.let { Geocoder(it) }
    val address: List<Address>?
    var p1: LatLng? = null
    try {
// May throw an IOException
        address = strAddress?.let { coder?.getFromLocationName(it, 5) }
        if (address == null || address.isEmpty()) {
            return null
        }

        val location = address[0]
        p1 = LatLng(location.latitude, location.longitude)
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
    return p1
}

fun formatNumber(value: String?): String? {
    var strNumber = ""
    // Format format = NumberFormat.getCurrencyInstance(new Locale("en", "in"));
    try {
        strNumber = if (value.isNullOrEmpty()) {
            ""
        } else {
            //        val f1: Format = NumberFormat.getIntegerInstance(Locale("en", "in"))
            val f1 = DecimalFormat()
            f1.minimumFractionDigits = 0
            f1.maximumFractionDigits = 2
            //        "\u20B9 " + f1.format(BigDecimal(value))
            f1.format(BigDecimal(value))
        }
    } catch (e: NumberFormatException) {

    }
    return strNumber
}


fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr = addr.hostAddress
                    //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.toUpperCase(Locale.getDefault()) else sAddr.substring(
                                0,
                                delim
                            ).toUpperCase(Locale.getDefault())
                        }
                    }
                }
            }
        }
    } catch (ignored: Exception) {
    } // for now eat exceptions
    return ""
}
fun getDeviceId(context: Context): String? {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

fun openDocumentsDialog(mContext: Context?, listener: ImageSelectionListener) {
    val pictureDialog = mContext?.let { AlertDialog.Builder(it) }
    pictureDialog?.setTitle("Choose")
    val pictureDialogItems = arrayOf("Select document from gallery", "Capture photo from camera")
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

fun getPathFromUri(context: Context, uri: Uri): String? {

//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(
                uri
            )
        ) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            }

            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(
                uri
            )
        ) {
            val id = DocumentsContract.getDocumentId(uri)
            if (id != null && id.startsWith("raw:")) {
                return id.substring(4)
            }
            val contentUriPrefixesToTry = arrayOf(
                "content://downloads/public_downloads",
                "content://downloads/my_downloads",
                "content://downloads/all_downloads"
            )
            for (contentUriPrefix in contentUriPrefixesToTry) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(contentUriPrefix), java.lang.Long.valueOf(
                        id!!
                    )
                )
                try {
                    val path: String? =
                        getDataColumn(
                            context,
                            contentUri,
                            null,
                            null
                        )
                    if (path != null) {
                        return path
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            val fileName: String? = getFileName(context, uri)
            val cacheDir: File? = getDocumentCacheDir(context)
            val file: File? = generateFileName(fileName, cacheDir)
            var destinationPath: String? = null
            if (file != null) {
                destinationPath = file.absolutePath
                saveFileFromUri(context, uri, destinationPath)
            }
            return destinationPath
        } else if (isMediaDocument(
                uri
            )
        ) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(
                context,
                contentUri,
                selection,
                selectionArgs
            )
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {

        // Return the remote address
        return if (isGooglePhotosUri(
                uri
            )
        ) uri.lastPathSegment else getDataColumn(
            context,
            uri,
            null,
            null
        )
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}
fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(
        column
    )
    try {
        cursor = context.contentResolver.query(
            uri!!, projection, selection, selectionArgs,
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun getFileName(context: Context, uri: Uri): String? {
    val mimeType = context.contentResolver.getType(uri)
    var filename: String? = null
    if (mimeType == null && context != null) {
        val path: String? =
           getPath(context, uri)
        filename = if (path == null) {
           getName(uri.toString())
        } else {
            val file = File(path)
            file.name
        }
    } else {
        val returnCursor = context.contentResolver.query(
            uri, null,
            null, null, null
        )
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            filename = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
    }
    return filename
}

fun getName(filename: String?): String? {
    if (filename == null) {
        return null
    }
    val index = filename.lastIndexOf('/')
    return filename.substring(index + 1)
}
fun getPath(context: Context, uri: Uri): String? {
    val absolutePath: String? =
      getLocalPath(context, uri)
    return absolutePath ?: uri.toString()
}
private fun getLocalPath(context: Context, uri: Uri): String? {
//    if (DEBUG) Log.d(
//        TAG + " File -",
//        "Authority: " + uri.authority +
//                ", Fragment: " + uri.fragment +
//                ", Port: " + uri.port +
//                ", Query: " + uri.query +
//                ", Scheme: " + uri.scheme +
//                ", Host: " + uri.host +
//                ", Segments: " + uri.pathSegments.toString()
//    )


    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // LocalStorageProvider
        if (isLocalStorageDocument(uri)) {
            // The path is the id
            return DocumentsContract.getDocumentId(uri)
        } else if (isExternalStorageDocument(
                uri
            )
        ) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if ("home".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory()
                    .toString() + "/documents/" + split[1]
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            if (id != null && id.startsWith("raw:")) {
                return id.substring(4)
            }
            val contentUriPrefixesToTry = arrayOf(
                "content://downloads/public_downloads",
                "content://downloads/my_downloads"
            )
            for (contentUriPrefix: String in contentUriPrefixesToTry) {
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse(contentUriPrefix), java.lang.Long.valueOf(
                        (id)!!
                    )
                )
                try {
                    val path: String? =
                     getDataColumn(
                         context,
                         contentUri,
                         null,
                         null
                     )
                    if (path != null) {
                        return path
                    }
                } catch (e: java.lang.Exception) {
                }
            }

            // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            val fileName: String? =
               getFileName(context, uri)
            val cacheDir: File? =
                getDocumentCacheDir(context)
            val file: File? = generateFileName(
                fileName,
                cacheDir
            )
            var destinationPath: String? = null
            if (file != null) {
                destinationPath = file.absolutePath
               saveFileFromUri(
                   context,
                   uri,
                   destinationPath
               )
            }
            return destinationPath
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if (("image" == type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if (("video" == type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if (("audio" == type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(
                split[1]
            )
            return getDataColumn(
                context,
                contentUri,
                selection,
                selectionArgs
            )
        }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {

        // Return the remote address
        return if (isGooglePhotosUri(uri)) {
            uri.lastPathSegment
        } else getDataColumn(
            context,
            uri,
            null,
            null
        )
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

fun isLocalStorageDocument(uri: Uri): Boolean {
    val AUTHORITY = "YOUR_AUTHORITY.provider"
    return AUTHORITY == uri.authority
}
fun getDocumentCacheDir(context: Context): File? {
    val DOCUMENTS_DIR = "documents"
    val dir =
        File(context.cacheDir, DOCUMENTS_DIR)
    if (!dir.exists()) {
        dir.mkdirs()
    }
  //  logDir(context.cacheDir)
  //  logDir(dir)
    return dir
}
fun generateFileName(name: String?, directory: File?): File? {
    var name = name ?: return null
    var file = File(directory, name)
    if (file.exists()) {
        var fileName = name
        var extension = ""
        val dotIndex = name.lastIndexOf('.')
        if (dotIndex > 0) {
            fileName = name.substring(0, dotIndex)
            extension = name.substring(dotIndex)
        }
        var index = 0
        while (file.exists()) {
            index++
            name = "$fileName($index)$extension"
            file = File(directory, name)
        }
    }
    try {
        if (!file.createNewFile()) {
            return null
        }
    } catch (e: IOException) {
       // Log.w(com.executive.goldmedal.executiveapp.common.FileUtils.TAG, e)
        return null
    }
  //  com.executive.goldmedal.executiveapp.common.FileUtils.logDir(directory)
    return file
}
//private fun logDir(dir: File) {
//    if (!DEBUG) return
//    Log.d(com.executive.goldmedal.executiveapp.common.FileUtils.TAG, "Dir=$dir")
//    val files = dir.listFiles()
//    for (file in files) {
//        Log.d(com.executive.goldmedal.executiveapp.common.FileUtils.TAG, "File=" + file.path)
//    }
//}
fun saveFileFromUri(context: Context, uri: Uri?, destinationPath: String?) {
    var `is`: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
        `is` = context.contentResolver.openInputStream(uri!!)
        bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
        val buf = ByteArray(1024)
        `is`!!.read(buf)
        do {
            bos.write(buf)
        } while (`is`.read(buf) != -1)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            `is`?.close()
            bos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


fun convertDocumentToBase64(filePath: String): String? {

//    val baos = ByteArrayOutputStream()
//    bitmap?.compress(Bitmap.CompressFormat.JPEG, 95, baos)
//    val data = baos.toByteArray()
//    // val buffer: ByteBuffer = ByteBuffer.allocate(bitmap.rowBytes * bitmap.height)
//    // bitmap.copyPixelsToBuffer(buffer)
//    //  val data: ByteArray = buffer.array()
//
//    return Base64.encodeToString(data, Base64.NO_WRAP)

    val file = File(filePath)
    val size = file.length().toInt()

    val bytes = ByteArray(size)

    try {
        val buf = BufferedInputStream(FileInputStream(file))
        buf.read(bytes, 0, bytes.size)
        buf.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}




fun downloadFile(context: Context, URL: String, Filename: String?) {
    //  Uri Download_Uri = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + URL);
    var URL = URL
    var Filename = Filename
    if (!URL.equals("", ignoreCase = true)) {
        Filename = if (Filename == null || Filename.isEmpty()) "Sample" else Filename

        //Log.d("url ", "----" + URL);
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Goldmedal/" + Filename

        //Log.d("downloadFile: ", path);
        val file = File(path)
        if (file.exists()) {
            val PdfURI: Uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.toString() + ".common.GenericFileProvider",
                    file)
            val target = Intent(Intent.ACTION_VIEW)
            target.setDataAndType(PdfURI, "image/*")

            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val intent: Intent = Intent.createChooser(target, "Open File")
            //                Intent target = new Intent(Intent.ACTION_VIEW);
//                target.setDataAndType(Uri.fromFile(file), "application/pdf");
//                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//                Intent intent = Intent.createChooser(target, "Open File");
//                try {
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Please install a image viewer to open this file", Toast.LENGTH_SHORT).show()
            }


//                } catch (Exception e) {
//                    // Instruct the user to install a PDF reader here, or something
//                    Log.d("downloadFile: ", " Error :- " + e);
//                }
        } else {
            //   if (CheckStoragePerm(context, "storage")) {
            URL = URL.replace(" ", "%20")
            val Download_Uri = Uri.parse(URL)
            val request: DownloadManager.Request = DownloadManager.Request(Download_Uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setAllowedOverRoaming(false)
            request.setTitle("$Filename")
            request.setDescription("Downloading $Filename")
            request.setVisibleInDownloadsUi(true)
            request.setNotificationVisibility(1)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Goldmedal/$Filename") //
            val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context, "Downloading $Filename", Toast.LENGTH_LONG).show()
            //  }
        }
    } else {
        Toast.makeText(context, "No File Found", Toast.LENGTH_LONG).show()
    }


//fun downloadFile(context: Context, URL: String, Filename: String?) {
//    //  Uri Download_Uri = Uri.parse("http://docs.google.com/gview?embedded=true&url=" + URL);
//    var URL = URL
//    var Filename = Filename
//    if (!URL.equals("", ignoreCase = true)) {
//        Filename = if (Filename == null || Filename.isEmpty()) "Sample" else Filename
//
//        //Log.d("url ", "----" + URL);
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/Goldmedal/" + Filename + ".jpg"
//
//        //Log.d("downloadFile: ", path);
//        val file = File(path)
//        if (file.exists()) {
//            val PdfURI: Uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.toString() + ".common.GenericFileProvider",
//                    file)
//            val target = Intent(Intent.ACTION_VIEW)
//            target.setDataAndType(PdfURI, "image/*")
//
//            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
//            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            val intent: Intent = Intent.createChooser(target, "Open File")
//            //                Intent target = new Intent(Intent.ACTION_VIEW);
////                target.setDataAndType(Uri.fromFile(file), "application/pdf");
////                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////
////                Intent intent = Intent.createChooser(target, "Open File");
////                try {
//            if (intent.resolveActivity(context.packageManager) != null) {
//                context.startActivity(intent)
//            } else {
//                Toast.makeText(context, "Please install a PDF viewer to open this file", Toast.LENGTH_SHORT).show()
//            }
//
//
////                } catch (Exception e) {
////                    // Instruct the user to install a PDF reader here, or something
////                    Log.d("downloadFile: ", " Error :- " + e);
////                }
//        } else {
//            if (EasyPermissions.hasPermissions(context, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                URL = URL.replace(" ", "%20")
//                val Download_Uri = Uri.parse(URL)
//                val request: DownloadManager.Request = DownloadManager.Request(Download_Uri)
//                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//                request.setAllowedOverRoaming(false)
//                request.setTitle("$Filename.jpg")
//                request.setDescription("Downloading $Filename.jpg")
//                request.setVisibleInDownloadsUi(true)
//                request.setNotificationVisibility(1)
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/Goldmedal/$Filename.jpg") //
//                val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//                downloadManager.enqueue(request)
//                Toast.makeText(context, "Downloading $Filename.jpg", Toast.LENGTH_LONG).show()
//            }
//        }
//    } else {
//        Toast.makeText(context, "No File Found", Toast.LENGTH_LONG).show()
//    }
}