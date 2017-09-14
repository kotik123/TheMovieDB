package com.testapp2.alex.themoviedb

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.testapp2.alex.themoviedb.Json.JsonClass
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.uiThread
import java.net.HttpURLConnection
import java.net.URL
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.support.design.internal.NavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import com.testapp2.alex.themoviedb.Adapters.Search_adapter
import com.testapp2.alex.themoviedb.Json.InsideResults
import com.testapp2.alex.themoviedb.Realm.Films
import io.realm.Realm
import org.jetbrains.anko.longToast
import kotlin.properties.Delegates
import io.realm.RealmResults
import javax.annotation.meta.When


class MainActivity : AppCompatActivity() {

    val TAG : String = "MainActivity"
    var searchList : MutableList<InsideResults> = mutableListOf()
    lateinit var listViewField : ListView
    lateinit var filmNameField : EditText
    lateinit var mDrawerLayout : DrawerLayout
    lateinit var mToggle : ActionBarDrawerToggle
    lateinit var adapter : Search_adapter
    lateinit var navigationView : NavigationView
    private var realm: Realm by Delegates.notNull()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init views
        filmNameField = findViewById(R.id.filmNameField)
        listViewField = findViewById(R.id.listViewField)

        //init navigation_view_items
        navigationView = findViewById<NavigationView>(R.id.nav_menu) as NavigationView

        //init side bar menu
        mDrawerLayout = findViewById(R.id.drawerLayout)
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)

        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)






        //init realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        //clear realm
//       realm.executeTransaction {
//            realm.deleteAll()
//       }

        if (realm.isEmpty) {
            jsonConvert("popular_films") }
        else{
            loadRealmPopular() }


        //Enter search action in EditText
        filmNameField.setOnEditorActionListener { textView, i, keyEvent ->
            //check enter on keyboard
            if(i.equals(5)) {
                val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0)
                true
            }
            else {false}

        }

        filmNameField!!.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                jsonConvert(filmNameField.text.toString())
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if(filmNameField!!.text.toString().length == 0) {
//                    searchList!!.clear()
//                    loadRealmPopular()
//                }
            }
        })


        // listview search listener
        listViewField!!.setOnItemClickListener { adapterView, view, i, l ->
            //where to send
            val inten = Intent(this, OpenSearchActivity::class.java)

            //send via string
            inten.putExtra("film", adapter!!.getItem(i)!!.id.toString())
            startActivity(inten)
        }
    }

    fun jsonConvert(str : String, popular: Int = 0){
        //val dialog = indeterminateProgressDialog("This a progress dialog")
        //dialog.show()
        asyncTest({
            val gson = Gson()
            val list1 = gson.fromJson<JsonClass>(it)
            searchList = list1.results
            adapter = Search_adapter(this, searchList!!)
            listViewField!!.adapter = adapter
            if (str == "popular_films"){
                    saveToRealm(1)
                }
            //dialog.hide()
        }, str)
    }

    fun asyncTest(callbackResult: (String) -> Unit, str: String){
        doAsync {

            var connection : HttpURLConnection
            var result : String  = ""
            //popular
            if (str == "popular_films") {
                connection = URL("https://api.themoviedb.org/3/movie/popular?api_key=ec7dfec9c6111586488e8211d3122b53&language=ru-RU&page=1").openConnection() as HttpURLConnection
            }
            else {
                connection = URL("https://api.themoviedb.org/3/search/movie?api_key=ec7dfec9c6111586488e8211d3122b53&language=en-US&page=1&include_adult=false&query=$str").openConnection() as HttpURLConnection
            }
            result = connection.inputStream.bufferedReader().readText()
            print(result)
            uiThread {
                callbackResult(result)
            }
        }
    }

    fun addToRealm(id: Int, nm : String, ornm :String, pc : String, popular: Int){
        realm.executeTransaction {
            val film1 = realm.createObject(Films::class.java , id)
            film1.name = nm
            film1.original_title = ornm
            film1.picture = pc
            film1.popOrNot = popular
        }
    }

    fun saveToRealm(popular: Int){
        for (i in searchList!!){
            addToRealm(i.id ,i.title, i.original_title, i.poster_path, popular)
        }
    }

    fun loadRealmPopular(){
        for (i in realm.where(Films::class.java).equalTo("popOrNot", 1 as Int).findAll()){
            searchList!!.add(InsideResults(i.id, i.name , i.picture, i.original_title))
        }
        adapter = Search_adapter(this, searchList!!)
        listViewField!!.adapter = adapter
    }

    //side bar menu actions



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        if (mToggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
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




