package com.testapp2.alex.themoviedb.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.testapp2.alex.themoviedb.Json.InsideResults
import com.testapp2.alex.themoviedb.MainActivity
import com.testapp2.alex.themoviedb.R
import org.jetbrains.anko.Android
import org.jetbrains.anko.find
import org.w3c.dom.Text

class Search_adapter(context:Context, list:List<InsideResults>) : BaseAdapter() {

    var context : Context = context
    var filmList : List<InsideResults>? = null
    var inflator : LayoutInflater? = null

    init {
        filmList = list
        inflator  = LayoutInflater.from(context)
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var view : View = inflator!!.inflate(R.layout.list_item, p2, false)
        var film_name : TextView
        var film_img : ImageView

       film_name = view?.findViewById(R.id.film_name)
       film_img = view?.findViewById(R.id.film_image)

       var film : InsideResults
       film = getItem(p0)!!

        film_name.setText(film.original_title)
        if(film.poster_path != "" || film.poster_path != null){
        Glide.with(context).load("https://image.tmdb.org/t/p/w640"+film.poster_path).into(film_img)}
        else{film_img.setImageResource(android.R.drawable.btn_minus)}


        return view
    }

    override fun getItem(p0: Int): InsideResults? {
        return filmList?.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return filmList!!.size
    }
}

