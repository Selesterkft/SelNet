package hu.selester.selnet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.fragments.SignatureFragment
import hu.selester.selnet.fragments.VerifyLoginFragment

class MainActivity : AppCompatActivity() {

    val TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, VerifyLoginFragment())
            .addToBackStack("App").commit()

    }

}