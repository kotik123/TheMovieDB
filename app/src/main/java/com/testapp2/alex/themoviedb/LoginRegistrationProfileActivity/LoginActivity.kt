package com.testapp2.alex.themoviedb.LoginRegistrationProfileActivity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.testapp2.alex.themoviedb.MainActivity

import com.testapp2.alex.themoviedb.R
import com.testapp2.alex.themoviedb.SideBarActivity
import io.realm.Realm
import org.jetbrains.anko.longToast
import kotlin.properties.Delegates

class LoginActivity : SideBarActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var logEmail : EditText
    lateinit var logPassword : EditText
    lateinit var logButton : Button
    private var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)

        //init side bar
        var inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var contentView = inflater.inflate(R.layout.activity_login, null, false)
        mDrawerLayout.addView(contentView, 0)

        //init realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        mAuth = FirebaseAuth.getInstance()

        logEmail = findViewById(R.id.editLoginEmail)
        logPassword = findViewById(R.id.editLoginPassword)
        logButton = findViewById(R.id.buttonLoginConfirm)

        logButton.setOnClickListener {
            signUp(logEmail.text.toString(), logPassword.text.toString())
        }

    }

    private fun signUp(email : String, password : String){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task: Task<AuthResult> ->

            if (task.isSuccessful){
                longToast("thanks for login")
                loggedItems()
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                longToast("something goes wrond")
            }

        }
    }
}
