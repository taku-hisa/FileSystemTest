package com.example.filesystemtest.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class item : RealmObject() {
    @PrimaryKey
    var id : Long = 0 //判別ID
    var category : String = "" //画像のカテゴリ
    var name : String = "" //画像の名前
    var detail : String = "" //画像の詳細
}