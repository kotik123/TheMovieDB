package com.testapp2.alex.themoviedb.Json

/**
 * Created by alex on 08.09.2017.
 */
class JsonClass(val page: Int, val total_results : Int, val total_pages : Int, val results : MutableList<InsideResults>)

class InsideResults(val id: Int,  val title: String, val poster_path : String, val original_title: String)