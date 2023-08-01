package com.bbt2000.boilerplate.util

import android.os.Environment
import android.util.Log
import java.io.File


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
}
