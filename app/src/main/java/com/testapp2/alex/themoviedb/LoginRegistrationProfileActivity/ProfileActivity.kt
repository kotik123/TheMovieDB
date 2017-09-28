package com.testapp2.alex.themoviedb.LoginRegistrationProfileActivity

import android.app.Activity
import android.content.Context
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.testapp2.alex.themoviedb.R
import com.testapp2.alex.themoviedb.Realm.UserProfile
import com.testapp2.alex.themoviedb.SideBarActivity
import com.vansuita.pickimage.bean.PickResult
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.listeners.IPickResult
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.longToast


class ProfileActivity : SideBarActivity(), IPickResult, OnFailureListener, OnSuccessListener<Any>{


    private val TAG: String = "ProfileActivity"
    lateinit var storageRef : StorageReference
    lateinit var storage : FirebaseStorage
    lateinit var userInfo : UserProfile
    lateinit var databaseRef: DatabaseReference
    lateinit var mAuth: FirebaseAuth
    lateinit var uImage: ImageView
    lateinit var uName: EditText
    lateinit var uButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init side bar
        var inflater = this@ProfileActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var contentView = inflater.inflate(R.layout.activity_profile, null, false)
        mDrawerLayout.addView(contentView, 0)

        //init views
        uImage = findViewById(R.id.imageProfile)
        uName = findViewById(R.id.editProfileName)
        uButton = findViewById(R.id.buttonProfileSave)

        //firebase
        mAuth = FirebaseAuth.getInstance()

        //firebase db
        databaseRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid)

        //firabse storage
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        val dialog = indeterminateProgressDialog("This a progress dialog")



        databaseRef.addValueEventListener(object:ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                longToast("Sign out succes")
            }

            override fun onDataChange(p0: DataSnapshot?) {


                userInfo = p0!!.getValue(UserProfile::class.java)
                uName.setText(userInfo.name)
                if (userInfo.photo != ""){dialog.show()
                Glide.with(applicationContext).load(userInfo.photo).into(uImage)}
                dialog.hide()
            }

            }
        )


        //load image
        uImage.setOnClickListener{
            PickImageDialog.build(PickSetup()).show(this@ProfileActivity)
            //FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userInfo)
        }


        //Enter search action in EditText
        uName.setOnEditorActionListener { textView, i, keyEvent ->
            //check enter on keyboard
            if (i == 5 || i == 6 || keyEvent.keyCode == 66) {
                val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                userInfo.name = uName.text.toString()
                FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userInfo)
                true
            } else {
                false
            }

        }


    }



    override fun onPickResult(p0: PickResult?) {
        if (p0!!.error == null) {
            try {
                uImage.setImageBitmap(p0.bitmap)
                    try{
                        //set_name_of_file
                        val image = storageRef.child("images/${FirebaseAuth.getInstance().currentUser!!.uid}")
                        //upload file
                        var uploadTask = image.putFile(p0!!.uri)

                        uploadTask.addOnSuccessListener {
                            userInfo.photo = uploadTask.getResult().downloadUrl.toString()
                            FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(userInfo)
                        }

                    }
                    catch(e:Exception){
                        longToast(e.toString())
                    }
                }
            catch (e : Exception){
                longToast(e.toString())
            }
        } else {
            longToast("SOME TROUBLES")
        }
    }

    override fun onFailure(p0: java.lang.Exception) {
    }

    override fun onSuccess(p0: Any) {
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
