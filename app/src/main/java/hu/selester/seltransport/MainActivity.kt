package hu.selester.seltransport

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hu.selester.seltransport.Fragments.LoginFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,LoginFragment()).addToBackStack("App").commit()

    }
}
