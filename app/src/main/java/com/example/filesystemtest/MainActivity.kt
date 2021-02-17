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
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

private val category = arrayOf("A","B","C","D","E") //カテゴリを定義
private var CATEGORY_CODE: Int = 42                //カテゴリの変数 42は適当な数値を入れているだけです

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding   //ビューバインディング
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)  //ビューバインディング
        setContentView(binding.root)  //ビューバインディング
        realm = Realm.getDefaultInstance() //realmのオープン処理

        //ボタンを押すと、アラートダイアログが表示されます
        binding.button.setOnClickListener {
            CATEGORY_CODE = 0 //初期選択 category[0] = "A"
            AlertDialog.Builder(this).apply {
                setTitle("フォルダ選択")
                setSingleChoiceItems(category, 0) { _, i -> //初期選択 category[0] = "A"
                    CATEGORY_CODE = i  // 選択した項目を保持
                }
                setPositiveButton("OK") { _, _ -> //OKボタンを押したときの処理
                    if(CATEGORY_CODE < category.size + 1) {
                        intent() //Strage Access Framework(以下SAF)をインテントで呼び出します
                        //SAFとは、名前の通り、Androidのストレージに簡単にアクセスするための機能です
                    }
                }
                setNegativeButton("Cancel", null)//Cancelボタンを押したときの処理
            }.show() //以上の内容でアラートダイアログを表示する
        }
    }

    //SAF　参考：https://akira-watson.com/android/gallery.html
    private fun intent() { //SAFの定義
        val intent = Intent() //インテントのインスタンス
        intent.type = "image/*" //画像のみを表示するオプション
        intent.action = Intent.ACTION_OPEN_DOCUMENT //ファイルを選択して、変数にするためのオプション
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) //複数ファイルを選択するオプション
        this.startActivityForResult(Intent.createChooser(intent, "Choose Photo"), CATEGORY_CODE) //以上の内容でSAFを終了し、onActivityResultへ結果を返します
    }

    //写真が選択された後の動き
    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode != RESULT_OK) { //インテントが正常に送れたかどうか
            return //だめなら終了
        }
        when (requestCode) {
            CATEGORY_CODE -> { //requestCode = CATEGORY_CODEであれば、以下の処理を行う
                try {
                    val uri = resultData?.data //一枚選択時はresultDataクラスのdataに画像が格納されている
                    var byteArrOutputStream = ByteArrayOutputStream()
                    if (uri != null) {
                        //一枚選択時の動作
                        val inputStream = contentResolver?.openInputStream(uri)  //resultData.dataはuri型で格納されているので、inputStreamへ変換する
                                                                                //uriを変換するときの選択肢として、①file型　②inputStream型があるが、
                                                                                //ディレクトリ指定が不要な②inputStream型を使うほうが楽なので使いました
                                                                                //参考:https://akira-watson.com/android/fileoutputstream.html
                        val image = BitmapFactory.decodeStream(inputStream) //inputStreamをbitmapへ変換します
                        if (inputStream != null) inputStream.close()
                        saveImage(image,999,byteArrOutputStream)  //saveImageメソッドを呼び出す
                    } else {
                        //複数枚選択時の動作
                        val clipData = resultData?.clipData  //複数枚選択時はresultDataクラスのclipdataに画像が格納されている
                        val clipItemCount = clipData?.itemCount
                        for (i in 0..clipItemCount!!) { //各画像のUriを取得する
                            val item: ClipData.Item = clipData.getItemAt(i)
                            val itemUri: Uri = item.uri
                            val inputStream = contentResolver?.openInputStream(itemUri) //一枚選択地と同様の処理を行う
                            val image = BitmapFactory.decodeStream(inputStream) //inputStreamをbitmapへ変換します
                            if (inputStream != null) inputStream.close()
                            saveImage(image,i,byteArrOutputStream)  //saveImageメソッドを呼び出す
                        }
                    }
                    byteArrOutputStream.close()

                } catch (e: Exception) {
                    //Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap, int:Int,byteArrOutputStream:ByteArrayOutputStream) { //画像をアプリ内部へ保存するためのメソッド
        val date = Date()
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val name : String = "${sdf.format(date)}_${int}.jpg" //画像名が被らないようにします
        val outStream = openFileOutput(name, Context.MODE_PRIVATE)  //ファイル(画像に限らず、txtなども)を保存する場合の選択肢として、①file型　②outputStream型があるが、
                                                                    // ディレクトリ指定が不要な②outputStream型を使うほうが楽なので使いました
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream) //jpegへ変換する qualityは画像の質です。100が最大値で、下げるほど画像の質が下がります。
        outStream.write(byteArrOutputStream.toByteArray())  //byte型へ変換し、画像ファイルを保存します
                                                            //uri→inputStream→bitmap→jpeg(→outputStream→byteArrayOutputStream)→byte型　※保存時はbyte型、表示はbitmap型まで変換してください※
        createDatabase(name)  //DB登録処理
        outStream.close() //いちいち書くのは面倒なので、try...with resourcesを使ったほうが良い
    }

    private fun createDatabase(name:String){ //データベースへ保存するメソッド
        realm.executeTransaction { db: Realm ->
            val maxId = db.where<item>().max("id")
            val nextId : Long = (maxId?.toLong() ?: 0L) + 1L
            val item = db.createObject<item>(nextId)
            item.category = category[CATEGORY_CODE]
            item.name = name //画像名"honyarara.jpg"を保存します
                             // 画像を表示するときは "honyarara.jpg"という名前から、bitmapまで変換して表示します
            item.detail = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()//realmのクローズ処理
    }
}