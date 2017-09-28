package com.testapp2.alex.themoviedb.Realm

import com.testapp2.alex.themoviedb.Json.InsideResults

class UserProfile {

    var email: String = ""
    var name: String = ""
    var photo : String = ""
    var listFav : MutableList<InsideResults> = mutableListOf(InsideResults(0,"","","", false))


    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(email: String, username: String, photo:String, listFav:MutableList<InsideResults>) {
        this.name = username
        this.email = email
        this.photo = photo
        this.listFav = listFav
    }

}