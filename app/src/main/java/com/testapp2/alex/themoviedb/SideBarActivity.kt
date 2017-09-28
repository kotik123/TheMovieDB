package com.testapp2.alex.themoviedb

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.AbsListView
import com.google.firebase.auth.FirebaseAuth
import com.testapp2.alex.themoviedb.Adapters.Search_adapter
import com.testapp2.alex.themoviedb.LoginRegistrationProfileActivity.LoginActivity
import com.testapp2.alex.themoviedb.LoginRegistrationProfileActivity.ProfileActivity
import com.testapp2.alex.themoviedb.LoginRegistrationProfileActivity.RegistrationActivity
import org.jetbrains.anko.itemsSequence
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity

open class SideBarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var adapter: Search_adapter
    lateinit var navigationView: NavigationView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_bar)

        //init navigation_view_items
        navigationView = findViewById<NavigationView>(R.id.nav_menu) as NavigationView

        //firebase
        mAuth = FirebaseAuth.getInstance()

        if(mAuth.currentUser == null){
            regItems()
        }
        else{
            loggedItems()
        }




        //init side bar menu
        mDrawerLayout = findViewById(R.id.side_bar)
        mToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navigationView.bringToFront()

        navigationView.setNavigationItemSelectedListener { menuItem ->

            toActivity(menuItem)

            true
        }


    }

    fun toActivity(menuItem: MenuItem){


        if (menuItem.itemId == R.id.nav_1 && this.javaClass != MainActivity::class.java) {
            mDrawerLayout.closeDrawer(navigationView)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else if (menuItem.itemId == R.id.nav_2 && this.javaClass != FavoritesActivity::class.java)
        {
            mDrawerLayout.closeDrawer(navigationView)
            startActivity(Intent(this, FavoritesActivity::class.java))
            finish()
        }
        else if (menuItem.itemId == R.id.nav_3 && this.javaClass != RegistrationActivity::class.java)
        {
            mDrawerLayout.closeDrawer(navigationView)
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
        else if (menuItem.itemId == R.id.nav_4 && this.javaClass != LoginActivity::class.java)
        {
            mDrawerLayout.closeDrawer(navigationView)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else if (menuItem.itemId == R.id.nav_5 && this.javaClass != ProfileActivity::class.java)
        {
            mDrawerLayout.closeDrawer(navigationView)
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        else if (menuItem.itemId == R.id.nav_6)
        {
            mDrawerLayout.closeDrawer(navigationView)
            mAuth.signOut()
            regItems()
            startActivity(Intent(this, MainActivity::class.java))
        }
        else{
            mDrawerLayout.closeDrawer(navigationView)
        }


    }

    open fun regItems(){
        navigationView.menu.findItem(R.id.nav_5).setVisible(false)
        navigationView.menu.findItem(R.id.nav_6).setVisible(false)
        navigationView.menu.findItem(R.id.nav_2).setVisible(false)
        navigationView.menu.findItem(R.id.nav_3).setVisible(true)
        navigationView.menu.findItem(R.id.nav_4).setVisible(true)
    }

    open fun loggedItems(){
        navigationView.menu.findItem(R.id.nav_5).setVisible(true)
        navigationView.menu.findItem(R.id.nav_2).setVisible(true)
        navigationView.menu.findItem(R.id.nav_6).setVisible(true)
        navigationView.menu.findItem(R.id.nav_3).setVisible(false)
        navigationView.menu.findItem(R.id.nav_4).setVisible(false)

    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    //side bar menu actions
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        if (mToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {

        super.onPostCreate(savedInstanceState, persistentState)
    }

}
