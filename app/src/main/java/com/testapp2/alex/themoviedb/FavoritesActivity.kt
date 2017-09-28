package com.testapp2.alex.themoviedb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import com.bumptech.glide.Glide
import com.firebase.client.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.testapp2.alex.themoviedb.Adapters.Search_adapter
import com.testapp2.alex.themoviedb.Json.InsideResults
import com.testapp2.alex.themoviedb.Realm.Films
import com.testapp2.alex.themoviedb.Realm.UserProfile
import io.realm.Realm
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast
import kotlin.properties.Delegates

open class FavoritesActivity : SideBarActivity() {

    private var TAG = "FavoritesActivity"
    lateinit var favListView : ListView
    //private var realm: Realm by Delegates.notNull()
    private lateinit var adapter: Search_adapter
    private var favoriteList: MutableList<InsideResults> = mutableListOf()
    lateinit var mAuth : FirebaseAuth
    lateinit var userInfo : UserProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init side bar
        var inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var contentView = inflater.inflate(R.layout.activity_favorites, null, false)
        mDrawerLayout.addView(contentView, 0)

        //firebase auth
        mAuth = FirebaseAuth.getInstance()



        favListView = findViewById(R.id.favorite_list)
        //realm = Realm.getDefaultInstance()

        loadFromProfile()


        // listview search listener
        favListView.setOnItemClickListener { adapterView, view, i, l ->
            //where to send
            val inten = Intent(this, OpenSearchActivity::class.java)

            //send via string
            inten.putExtra("film", adapter.getItem(i)!!.id.toString())
            startActivity(inten)
        }


        registerForContextMenu(favListView)

    }


    //LOADING REALM FAVORITES
//    private fun loadRealmFav() {
//
//        favoriteList.clear()
//
//        var list = realm.where(Films::class.java).equalTo("favorites", true).findAll()
//
//        for (i in realm.where(Films::class.java).equalTo("favorites", true).findAll()) {
//            favoriteList.add(InsideResults(i.id, i.name, i.picture, i.original_title))
//        }
//        adapter = Search_adapter(this, favoriteList)
//        favListView.adapter = adapter
//    }

    private fun loadFromProfile(){


        favoriteList.clear()
        var databaseRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid)

        databaseRef.addValueEventListener(object: ValueEventListener {

            override fun onCancelled(p0: DatabaseError?) {
                longToast("Error")
            }

            override fun onDataChange(p0: DataSnapshot?) {
                userInfo = p0!!.getValue(UserProfile::class.java)

                try {
                    var templist = userInfo.listFav
                    templist.removeAt(0)
                    adapter = Search_adapter(applicationContext, userInfo.listFav)
                    favListView.adapter = adapter
                }
                catch (e:Exception){
                    longToast("error with list")
                }


            }

        }
        )

        //FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userInfo)
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v!!.id == R.id.favorite_list){
            menu!!.add(Menu.NONE, 0, 0, "Delete from list");
            //realm.insertOrUpdate(realm.where(Films::class.java).equalTo("id", ))
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        //DELETE FAVORITE FROM REALM
//        var tempObj = realm.where(Films::class.java).equalTo("id", adapter.getItem(item!!.itemId)!!.id).findFirst()
//
//        realm.executeTransaction {
//            tempObj!!.favorites = false
//            realm.insertOrUpdate(tempObj)
//        }
//        loadRealmFav()
        for(i in userInfo.listFav){if(i.id==item!!.itemId){userInfo.listFav.remove(i)}}
        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userInfo)
        return super.onContextItemSelected(item)
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
