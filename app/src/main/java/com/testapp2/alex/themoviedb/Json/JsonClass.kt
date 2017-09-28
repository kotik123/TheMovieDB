package com.testapp2.alex.themoviedb.Json

/**
 * Created by alex on 08.09.2017.
 */
class JsonClass(val page: Int, val total_results : Int, val total_pages : Int, val results : MutableList<InsideResults>)

class InsideResults{

    var id : Int = 0
    var title : String = ""
    var poster_path : String = ""
    var original_title: String = ""
    var favorite : Boolean = false

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(id: Int, title: String, poster_path : String, original_title: String, favorite: Boolean = false) {
        this.id = id
        this.title= title
        this.poster_path = poster_path
        this.original_title = original_title
        this.favorite = favorite

    }

}

