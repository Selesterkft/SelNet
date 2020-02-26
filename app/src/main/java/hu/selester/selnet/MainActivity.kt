package hu.selester.selnet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.fragments.SignatureFragment
import hu.selester.selnet.fragments.VerifyLoginFragment
import hu.selester.selnet.objects.SessionClass

class MainActivity : AppCompatActivity() {

    val TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        SessionClass.setValue("WSUrl", getString(R.string.root_url))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, VerifyLoginFragment())
            .addToBackStack("App").commit()

    }

}