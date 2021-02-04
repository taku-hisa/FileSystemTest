package com.example.filesystemtest.database

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class FileSystemApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this) //realmの初期化
        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true).build() //UIスレッドでのデータベース書込みを許可する
        Realm.setDefaultConfiguration(config)
    }
    //このあと、必ずAndroidManifest.xmlを変更すること！
}