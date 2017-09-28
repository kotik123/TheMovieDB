package com.testapp2.alex.themoviedb.LoginRegistrationProfileActivity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.firebase.client.Firebase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

import com.testapp2.alex.themoviedb.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.testapp2.alex.themoviedb.Realm.UserProfile
import com.testapp2.alex.themoviedb.SideBarActivity
import io.realm.Realm
import org.jetbrains.anko.longToast
import kotlin.properties.Delegates


class RegistrationActivity : SideBarActivity(), FirebaseAuth.AuthStateListener {


    lateinit var mAuth: FirebaseAuth
    lateinit var regName : EditText
    lateinit var regEmail : EditText
    lateinit var regPassword : EditText
    lateinit var regButton : Button
    lateinit var databaseReference : DatabaseReference
    private var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_registration)

        //init side bar
        var inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var contentView = inflater.inflate(R.layout.activity_registration, null, false)
        mDrawerLayout.addView(contentView, 0)

        //init Firebase DB
        databaseReference = FirebaseDatabase.getInstance().getReference("User")

        regName = findViewById(R.id.editRegistrationName)
        regEmail = findViewById(R.id.editRegistrationEmail)
        regPassword = findViewById(R.id.editRegistrationPassword)
        regButton = findViewById(R.id.buttonRegistrationConfirm)

        mAuth = FirebaseAuth.getInstance()

        regButton.setOnClickListener {
            createUser(regEmail.text.toString(), regPassword.text.toString(), regName.text.toString())
        }



    }

    private fun createUser(email : String, password : String, uname : String){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task: Task<AuthResult> ->
            if (task.isSuccessful) {
                //Registration OK
                val firebaseUser = mAuth.currentUser
                longToast("registration successfully")
                databaseReference.child(firebaseUser!!.uid).setValue(UserProfile(email,uname,"", mutableListOf()))
                signUp(email, password)
            } else {
                longToast("nihua ne viwlo")
            }
        }

    }




    override fun onAuthStateChanged(p0: FirebaseAuth) {
        var user : FirebaseUser = p0.currentUser!!

        if (user != null){
            longToast(user.uid)
        }
        else{
            longToast("zalupa")
        }
    }

    private fun signUp(email : String, password : String){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->

            if (task.isSuccessful){
                longToast("thanks for login")
                loggedItems()
                val inten = Intent(this, ProfileActivity::class.java)
                startActivity(inten)
            }
            else{
                longToast("something goes wrong")
                startActivity(Intent(this, ProfileActivity::class.java))
            }

        }
    }


    override fun onStart() {
        super.onStart()
    }

}
