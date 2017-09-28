package com.testapp2.alex.themoviedb

import android.os.Bundle
import android.widget.*
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.testapp2.alex.themoviedb.Json.JsonClass
import java.net.HttpURLConnection
import java.net.URL
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import com.testapp2.alex.themoviedb.Adapters.Search_adapter
import com.testapp2.alex.themoviedb.Json.InsideResults
import com.testapp2.alex.themoviedb.Realm.Films
import io.realm.Realm
import kotlin.properties.Delegates
import org.jetbrains.anko.*


open class MainActivity : SideBarActivity(), AbsListView.OnScrollListener {



    private val TAG: String = "MainActivity"
    private var searchList: MutableList<InsideResults> = mutableListOf()
    private var tempList : MutableList<InsideResults> = mutableListOf()
    private lateinit var listViewField: ListView
    private lateinit var filmNameField: EditText
    private lateinit var adapter: Search_adapter
    private var page : Int = 1
    private var preLastElement : Int = 0
    private var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)


        //init side bar
        var inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var contentView = inflater.inflate(R.layout.activity_main, null, false)
        mDrawerLayout.addView(contentView, 0)


        //init views
        filmNameField = findViewById(R.id.filmNameField)
        listViewField = findViewById(R.id.listViewField)



        //init realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        //clear realm
//       realm.executeTransaction {
//            realm.deleteAll()
//       }

        if (realm.isEmpty) {
            jsonConvert("popular_films", page)
        } else {
            loadRealmPopular()
        }


        //Enter search action in EditText
        filmNameField.setOnEditorActionListener { textView, i, keyEvent ->
            //check enter on keyboard
            if (i.equals(5)) {
                val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                true
            } else {
                false
            }

        }

        filmNameField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                jsonConvert(filmNameField.text.toString(), page)
                page = 1
                preLastElement = 0
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (tempList.size == 0 ){tempList = searchList}
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(filmNameField!!.text.toString().length == 0) {
                    searchList.clear()
                    searchList.addAll(tempList)
                    //loadRealmPopular()
                    loadPopularPageAfterSearch(searchList)
                    tempList.clear()
                }
            }
        })


        // listview search listener
        listViewField.setOnItemClickListener { adapterView, view, i, l ->
            //where to send
            val inten = Intent(this, OpenSearchActivity::class.java)

            //send via string
            inten.putExtra("film", adapter.getItem(i)!!.id.toString())
            startActivity(inten)
        }

        // listview bottom detection
        listViewField.setOnScrollListener(this)
    }

    private fun jsonConvert(str: String = "popular_films", page: Int) {
        //val dialog = indeterminateProgressDialog("This a progress dialog")
        //dialog.show()
        asyncTest({
            val gson = Gson()
            val list1 = gson.fromJson<JsonClass>(it)


            if (page == 1){
            searchList = list1.results
            adapter = Search_adapter(this, searchList)
            listViewField.adapter = adapter}
            else{
            searchList.addAll(list1.results)
            adapter.notifyDataSetChanged()
            }


            if (str == "popular_films" && page == 1) {
                saveToRealm(1)
            }
            //dialog.hide()
        }, str)
    }


    private fun asyncTest(callbackResult: (String) -> Unit, str: String) {
        doAsync {

            var connection: HttpURLConnection
            var result: String = ""
            //popular
            if (str == "popular_films") {
                connection = URL("https://api.themoviedb.org/3/movie/popular?api_key=ec7dfec9c6111586488e8211d3122b53&language=ru-RU&page=$page").openConnection() as HttpURLConnection
            } else {
                connection = URL("https://api.themoviedb.org/3/search/movie?api_key=ec7dfec9c6111586488e8211d3122b53&language=en-US&page=1&include_adult=false&query=$str&page=$page").openConnection() as HttpURLConnection
            }
            result = connection.inputStream.bufferedReader().readText()
            print(result)
            uiThread {
                callbackResult(result)
            }
        }
    }

    private fun addToRealm(id: Int, nm: String, ornm: String, pc: String, popular: Int) {
        realm.executeTransaction {
            val film1 = realm.createObject(Films::class.java, id)
            film1.name = nm
            film1.original_title = ornm
            film1.picture = pc
            film1.popOrNot = popular
        }
    }


    private fun saveToRealm(popular: Int) {
        for (i in searchList) {
            addToRealm(i.id, i.title, i.original_title, i.poster_path, popular)
        }
    }

    private fun loadRealmPopular() {
        for (i in realm.where(Films::class.java).equalTo("popOrNot", 1.toInt()).findAll()) {
            searchList.add(InsideResults(i.id, i.name, i.picture, i.original_title))
        }
        adapter = Search_adapter(this, searchList)
        listViewField.adapter = adapter
    }

    private fun loadPopularPageAfterSearch(list: MutableList<InsideResults>){
        adapter = Search_adapter(this, list)
        listViewField.adapter = adapter
        page = list.size / 20
    }

    private fun loadNextPage(){

        var filmName = "popular_films"
        if (filmNameField.text.toString() != ""){filmName = filmNameField.text.toString()}
        longToast(filmNameField.text)

     try {
         page++
         jsonConvert(filmName, page)
     }
     catch (e:Exception){
         longToast(e.toString())
     }

    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {

    }

    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {
        if (p0!!.id == listViewField.id){
            var lastItem : Int = p1 + p2

            if (lastItem == p3){
                if (preLastElement != lastItem){
                    loadNextPage()
                    preLastElement = lastItem
                }
            }
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



