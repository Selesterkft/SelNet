package hu.selester.selnet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hu.selester.selnet.Fragments.VerifyLoginFragment

class MainActivity : AppCompatActivity() {

    val TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, VerifyLoginFragment())
            .addToBackStack("App").commit()

    }

}