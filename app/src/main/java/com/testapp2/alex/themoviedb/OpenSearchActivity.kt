package com.testapp2.alex.themoviedb

import android.graphics.Color
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.testapp2.alex.themoviedb.Adapters.Search_adapter
import com.testapp2.alex.themoviedb.Json.FilmInfo
import com.testapp2.alex.themoviedb.Json.InsideResults
import com.testapp2.alex.themoviedb.Json.JsonClass
import com.testapp2.alex.themoviedb.Realm.Films
import io.realm.Realm
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import org.w3c.dom.Text
import java.net.HttpURLConnection
import java.net.URL
import kotlin.properties.Delegates

open class OpenSearchActivity : AppCompatActivity() {

    var TAG = "OPEN SEARCH ACITIVY"
    lateinit var searchFilmName : TextView
    lateinit var searchFilmDescription : TextView
    lateinit var searchFilmImage : ImageView
    lateinit var favoriteButton : Button
    private var realm: Realm by Delegates.notNull()
    var value : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_search)

        val realm = Realm.getDefaultInstance()

        val extra : Bundle = intent.extras
            if (extra != null){
                value = extra.getString("film")
            }

        //init buttons/views
        searchFilmName = findViewById<TextView>(R.id.searchFilmName)
        searchFilmDescription = findViewById<TextView>(R.id.searchFilmDescription)
        searchFilmImage = findViewById<ImageView>(R.id.searchFilmImage)
        favoriteButton = findViewById(R.id.favoriteButton)
        try {
            when (realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst()!!.favorites) {
                true -> favoriteButton.setText("Добавлено в избранное")
                false -> favoriteButton.setText("Добавить в избранное")
            }
        }
        catch (e: Exception){
            longToast("not yet in realm")
        }

        //check exist object in realm or not
        if (realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst() != null && realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst()!!.descritpion != ""){
            longToast("ALL RIGHT ITS EXIST")
            val tempObj = realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst()
            searchFilmName!!.setText(tempObj!!.original_title)
            searchFilmDescription!!.setText(tempObj!!.descritpion)
            searchFilmDescription!!.movementMethod = ScrollingMovementMethod()
            Glide.with(this).load("https://image.tmdb.org/t/p/w640"+tempObj!!.picture).into(searchFilmImage)
        }
        else{
            longToast("NOTHING HERE")
            jsonConvert(value)}


        favoriteButton.setOnClickListener {
            //check is film in favorites or not and change it
            if(realm.where(Films::class.java).equalTo("id", value.toInt()).equalTo("favorites", true).findFirst() != null){
                realm.executeTransaction {
                    realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst()!!.favorites = false
                }
                favoriteButton.setText("Добавить в избранное")
            }
            else{
                realm.executeTransaction {
                    realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst()!!.favorites = true
                }
                favoriteButton.setText("Добавлено в избранное")
            }
        }


    }




    fun jsonConvert(str : String){
        val dialog = indeterminateProgressDialog("Loading Description")
        dialog.show()
        asyncTest({
            val gson = Gson()
            val list1 = gson.fromJson<FilmInfo>(it)
            searchFilmName!!.setText(list1.original_title)
            searchFilmDescription!!.setText(list1.overview)
            searchFilmDescription!!.movementMethod = ScrollingMovementMethod()
            Glide.with(this).load("https://image.tmdb.org/t/p/w640"+list1.poster_path).into(searchFilmImage)
            dialog.hide()
            realm = Realm.getDefaultInstance()
                if (realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst() != null) {
                    updateRealm(list1.overview)
                }
                else{
                    addToRealm(value.toInt(), list1.title, list1.original_title, list1.poster_path, list1.overview)
                }
        }, str)
    }

    fun asyncTest(callbackResult: (String) -> Unit, str: String){
        doAsync {

            //popular
            val connection = URL("https://api.themoviedb.org/3/movie/$str?api_key=ec7dfec9c6111586488e8211d3122b53&language=en-US").openConnection() as HttpURLConnection
            var result = connection.inputStream.bufferedReader().readText()
            uiThread {
                callbackResult(result)
            }
        }
    }

    fun addToRealm(id: Int, nm : String, ornm :String, pc : String, desc : String){
        realm.executeTransaction {
            val film1 = realm.createObject(Films::class.java , id)
            film1.name = nm
            film1.original_title = ornm
            film1.picture = pc
            film1.descritpion = desc
        }
    }

    fun updateRealm(descp : String){
        realm.executeTransaction {
            val film = realm.where(Films::class.java).equalTo("id", value.toInt()).findFirst()
            film!!.descritpion = descp
        }

    }

    override fun onDestroy() {
        Log.d(TAG, "on destroy")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        super.onRestart()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }
}

