package com.example.artfestproject1

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.MyImage.ImageGallery
import org.bytedeco.opencv.global.opencv_imgcodecs
import org.bytedeco.opencv.opencv_core.Mat
import java.io.File


class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // 取得傳遞過來的資料
        val previousIntent = this.intent
        val imageURI = previousIntent.getStringExtra("imageURI")
        val uri: Uri = Uri.parse(imageURI)

        // get filename from URI
        var filename: String = uri.path.toString()
        val cut: Int = filename.lastIndexOf('/')
        if (cut != -1) {
            filename = filename.substring(cut + 1)
        }

        // 轉換照片並存檔
        val newFilename = "edited_" + filename
        var mat: Mat = ImageGallery.internalImgRead(filename, this)
        mat = ImageGallery.colorToGray(mat)
        ImageGallery.internalImgWrite(newFilename, mat, this)

        // 印到畫面上
        val internalEntryPoint: File = this.getFilesDir()
        val imgDir = File(internalEntryPoint.absolutePath, "Images")
        val imgFile: File = File(imgDir, newFilename)
        val imgFilePath = imgFile.absolutePath
        val newURI = Uri.parse(imgFilePath)
        val imageView: ImageView = findViewById<View>(R.id.imageView) as ImageView
        imageView.setImageURI(newURI)
    }
}