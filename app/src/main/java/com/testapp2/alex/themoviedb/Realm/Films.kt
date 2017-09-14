package com.testapp2.alex.themoviedb.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by alex on 08.09.2017.
 */
open class Films : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var original_title: String = ""
    var picture: String = ""
    var popOrNot: Int = 0
    var favorites : Boolean = false
    var descritpion : String = ""
}