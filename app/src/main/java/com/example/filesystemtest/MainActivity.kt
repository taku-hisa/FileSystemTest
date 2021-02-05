package com.example.filesystemtest

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.filesystemtest.database.item
import com.example.filesystemtest.databinding.ActivityMainBinding
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

private val category = arrayOf("A","B","C","D","E") //カテゴリを定義
private var CATEGORY_CODE: Int = 42                 //カテゴリの変数

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding   //ビューバインディング
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        realm = Realm.getDefaultInstance() //realmのオープン処理

        //ギャラリーを開く
        binding.button.setOnClickListener {
            CATEGORY_CODE = 0 //初期選択="A"
            AlertDialog.Builder(this).apply {
                setTitle("フォルダ選択")
                setSingleChoiceItems(category, 0) { _, i -> //初期選択="A"
                    CATEGORY_CODE = i  // 選択した項目を保持
                }
                setPositiveButton("OK") { _, _ ->
                    if(CATEGORY_CODE < category.size + 1) {
                        intent()
                    }
                }
                setNegativeButton("Cancel", null)
            }.show()
        }
    }

    private fun intent() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        this.startActivityForResult(Intent.createChooser(intent, "Choose Photo"), CATEGORY_CODE)
    }

    //写真が選択された後の動き
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) {
            return
        }
        when (requestCode) {
            CATEGORY_CODE -> {
                try {
                    val uri = resultData?.data
                    if (uri != null) {
                        //一枚選択時の動作
                        val inputStream = contentResolver?.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        saveImage(image,999)
                    } else {
                        //複数枚選択時の動作
                        val clipData = resultData?.clipData
                        val clipItemCount = clipData?.itemCount
                        for (i in 0..clipItemCount!!) {
                            val item: ClipData.Item = clipData.getItemAt(i)
                            val itemUri: Uri = item.uri
                            val inputStream = contentResolver?.openInputStream(itemUri)
                            val image = BitmapFactory.decodeStream(inputStream)
                            saveImage(image,i)
                        }
                    }
                } catch (e: Exception) {
                    //Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, int:Int) {
        val date = Date()
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val name : String = "${sdf.format(date)}_${int}.jpg" //画像の名前
        val byteArrOutputStream = ByteArrayOutputStream()
        val outStream = openFileOutput(name, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.write(byteArrOutputStream.toByteArray())
        createDatabase(name)  //DB登録処理
        outStream.close()
    }

    private fun createDatabase(name:String){
        realm.executeTransaction { db: Realm ->
            val maxId = db.where<item>().max("id")
            val nextId : Long = (maxId?.toLong() ?: 0L) + 1L
            val item = db.createObject<item>(nextId)
            item.category = category[CATEGORY_CODE]
            item.name = name
            item.detail = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()                      //realmのクローズ処理
    }
}