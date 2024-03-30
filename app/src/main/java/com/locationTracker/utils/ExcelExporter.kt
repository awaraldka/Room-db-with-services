package com.locationTracker.utils


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.locationTracker.R
import com.locationTracker.modelClass.LocationData
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ExcelExporter(private val context: Context) {


    private val CHANNEL_ID = "DownloadChannel"


    fun generateExcel(currentFilterDataList: ArrayList<LocationData>) {
        val hssfWorkbook = HSSFWorkbook()
        val hssfSheet = hssfWorkbook.createSheet("Location Data")


        val hssfRowTitle = hssfSheet.createRow(1)
        hssfRowTitle.createCell(0).setCellValue("addressLine")
        hssfRowTitle.createCell(1).setCellValue("country")
        hssfRowTitle.createCell(2).setCellValue("city")
        hssfRowTitle.createCell(3).setCellValue("state")
        hssfRowTitle.createCell(4).setCellValue("postalCode")
        hssfRowTitle.createCell(5).setCellValue("lat")
        hssfRowTitle.createCell(6).setCellValue("long")
        hssfRowTitle.createCell(7).setCellValue("deviceId")
        hssfRowTitle.createCell(8).setCellValue("speed")

        // Data rows
        var row = 2
        var srNo = 1
        for (a in currentFilterDataList) {
            val hssfRow = hssfSheet.createRow(row)
            hssfRow.createCell(0).setCellValue(a.addressLine)
            hssfRow.createCell(1).setCellValue(a.country)
            hssfRow.createCell(2).setCellValue(a.city)
            hssfRow.createCell(3).setCellValue(a.state)
            hssfRow.createCell(4).setCellValue(a.postalCode)
            hssfRow.createCell(5).setCellValue(a.lat)
            hssfRow.createCell(6).setCellValue(a.long)
            hssfRow.createCell(7).setCellValue(a.deviceId)
            hssfRow.createCell(8).setCellValue(a.speed)
            row++
            srNo++
        }

        val path =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}")
        path.mkdirs()
        val fileName = "${path.absolutePath}/Location_Data_${System.currentTimeMillis()}.xls"
        try {
            val fileOutputStream = FileOutputStream(fileName)
            hssfWorkbook.write(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            showNotification(fileName)
            Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showNotification(filePath: String) {
        val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(filePath))

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(fileUri, "application/vnd.ms-excel")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Download completed")
            .setContentText("Click to open the downloaded file")
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationManager.IMPORTANCE_HIGH) // Set the priority to high
            .setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Download Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}