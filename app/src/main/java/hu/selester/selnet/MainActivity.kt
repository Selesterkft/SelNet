package hu.selester.selnet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.selester.selnet.Fragments.SignatureFragment

class MainActivity : AppCompatActivity() {

    val TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SignatureFragment())
            .addToBackStack("App").commit()

    }

}