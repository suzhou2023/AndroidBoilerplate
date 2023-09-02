package com.bbt2000.boilerplate.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream


object FileUtil {
    fun getInternalDataDir(): File {
        return Environment.getDataDirectory()
    }

    fun getExternalPicDir(dirName: String? = "Boilerplate"): File? {
        try {
            val picsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val myPicDir = File(picsDir, dirName)
            if (!myPicDir.exists()) myPicDir.mkdir()
            return myPicDir
        } catch (e: Exception) {
            Log.e("FileUtil", "Create dir failed", e)
            e.printStackTrace()
        }
        return null
    }

    fun createRecordFile(path: String? = null): File? {
        try {
            val file: File = if (path != null) {
                File(path)
            } else {
                File("${getExternalPicDir()}/${System.currentTimeMillis()}.mp4")
            }
            file.parentFile?.mkdirs()
            file.createNewFile()
            return file
        } catch (e: Exception) {
            Log.e("FileUtil", "Create file failed", e)
            e.printStackTrace()
        }
        return null
    }

    /**
     * 保存bitmap到文件中
     */
    fun saveBitmapToFile(bitmap: Bitmap, path: String? = null): File? {
        try {
            val file: File = if (path != null) {
                File(path)
            } else {
                File("${getExternalPicDir()}/${System.currentTimeMillis()}.jpg")
            }
            file.parentFile?.mkdirs()
            file.createNewFile()

            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            return file
        } catch (e: Exception) {
            Log.e("FileUtil", "saveBitmapToFile failed", e)
            e.printStackTrace()
        }
        return null
    }
}
