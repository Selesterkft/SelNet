package hu.selester.seltransport.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.UsersTable
import hu.selester.seltransport.databinding.FrgLoginBinding
import hu.selester.seltransport.dialogs.NewUserDialogFragment
import hu.selester.seltransport.dialogs.TelNumberDialogFragment
import hu.selester.seltransport.utils.AppUtils
import hu.selester.seltransport.utils.KeyboardUtils
import org.json.JSONObject

class LoginFragment : Fragment(), TelNumberDialogFragment.OnItemSelected,
    NewUserDialogFragment.OnOkPressed {

    lateinit var mBinding: FrgLoginBinding
    var mSmsLoopCnt = 0
    private val mLoopTimeMilliSec: Long = 3000
    private val mReceiveSmsRequest = 103
    lateinit var mDb: SelTransportDatabase
    private val mSmsHandler = Handler()
    private val mSmsRunnable = Runnable { smsLoop() }
    private lateinit var mTelephoneNumber: String
    private val mTag = "LoginFragment"
    private lateinit var mRegistrationToken: String

    override fun onUserSelected(id: Long) {
        val user = mDb.usersDao().getValidUserById(id)
        if (user.size != 1) {
            AppUtils.toastShort(
                context,
                "Hiba az azonosítás közben"
            )
        } else {
            AppUtils.setSharedPreferences(requireContext(), "logged_user", user[0].id!!.toString())
            val activity = requireActivity() as MainActivity
            activity.setLoggedInUser(user[0].telephoneNumber)
            findNavController().navigate(R.id.action_loginFragment_to_transportListFragment)
        }
    }

    override fun onNewUser() {
        val newUserDialog = NewUserDialogFragment()
        newUserDialog.setTargetFragment(this, 2)
        newUserDialog.show(parentFragmentManager, "NewUserDialogFragment")
    }

    override fun onNewUserOkPressed(telNumber: String) {
        mTelephoneNumber = telNumber
        if (mDb.usersDao().getValidUserByNumber(telNumber).isNotEmpty()) {
            AppUtils.toast(
                context, "Ez a telefonszám már létezik, kérem válassza ki a listából!"
            )
        } else if (telNumber.length < 7) {
            AppUtils.toast(context, "Kérem valós telefonszámot adjon meg!")
            onNewUser()
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.RECEIVE_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECEIVE_SMS),
                    mReceiveSmsRequest
                )
            } else {
                registerNewUser()
            }
        }
    }

    private fun chooseTelNumber() {
        val chooseDialog = TelNumberDialogFragment()
        chooseDialog.setTargetFragment(this, 1)
        chooseDialog.show(parentFragmentManager, "TelNumberDialogFragment")
    }

    private fun onLoginBtnPressed() {
        if (mDb.usersDao().getValidUsers().isEmpty()) {
            onNewUser()
        } else {
            chooseTelNumber()
        }
    }

    private fun registerNewUser() {
        mBinding.loginBtn.visibility = View.GONE
        mBinding.loginProgressBar.visibility = View.VISIBLE
        mBinding.loginStatusMessage.visibility = View.VISIBLE
        mBinding.loginStatusMessage.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
        mBinding.loginStatusMessage.text = "Várakozás a regisztrációs SMS-re..."
        AppUtils.setSharedPreferences(requireContext(), "registrationSmsReceived", "0")
        registrationRequest()
        mSmsLoopCnt = 0
        mSmsHandler.postDelayed(mSmsRunnable, mLoopTimeMilliSec)
        KeyboardUtils.hideKeyboard(requireActivity())
    }

    private fun registrationRequest() {
        val headerObject = JSONObject()
        headerObject.put("taskType", "RGS")
        headerObject.put("interface", "1001")
        headerObject.put("sender", "droid")
        headerObject.put("recipient", "CP")

        val bodyObject = JSONObject()
        bodyObject.put("confirmationType", "SMS")
        bodyObject.put("userName", mTelephoneNumber)
        bodyObject.put("phoneNumber", mTelephoneNumber)

        val request = JSONObject()
        request.put("token", mRegistrationToken)
        request.put("header", headerObject)
        request.put("body", bodyObject)

        val url = resources.getString(R.string.root_url) + "/userRegistration.php"
        val que = Volley.newRequestQueue(this.context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, request,
            Response.Listener { jsonRoot ->
                Log.i(mTag, "")
                try {
                    mRegistrationToken = jsonRoot.getJSONObject("newToken").getString("token")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Log.e(mTag, "registrationRequest: JSON request error: ${error.printStackTrace()}")
            }
        )
        que.add(jsonObjectRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDb = SelTransportDatabase.getInstance(requireContext())!!
        mBinding = FrgLoginBinding.inflate(inflater)

        mRegistrationToken = resources.getString(R.string.reg_token)
        mBinding.loginBtn.setOnClickListener { onLoginBtnPressed() }
        return mBinding.root
    }

    private fun smsLoop() {
        try {
            if (AppUtils.getSharedPreferences(
                    requireActivity(),
                    "registrationSmsReceived"
                ) == "1"
            ) {
                restoreLoginBtn()

                val headerObject = JSONObject()
                headerObject.put("taskType", "RGSE")
                headerObject.put("interface", "1001")
                headerObject.put("sender", "droid")
                headerObject.put("recipient", "CP")

                val bodyObject = JSONObject()
                bodyObject.put("userName", mTelephoneNumber)
                val registrationKey =
                    AppUtils.getSharedPreferences(requireActivity(), "registrationKey")
                bodyObject.put("registrationKey", registrationKey)

                val request = JSONObject()
                request.put("token", mRegistrationToken)
                request.put("header", headerObject)
                request.put("body", bodyObject)

                val url = resources.getString(R.string.root_url) + "/userRegistrationEnd.php"
                val que = Volley.newRequestQueue(this.context)
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, request,
                    Response.Listener { jsonRoot ->
                        Log.i(mTag, "")
                        try {
                            // mRegistrationToken =
                            //    jsonRoot.getJSONObject("newToken").getString("token")
                            val activeUserId = mDb.usersDao().insertUser(
                                UsersTable(
                                    null,
                                    mTelephoneNumber,
                                    jsonRoot.getJSONObject("result").getString("masterKey"),
                                    registrationKey
                                )
                            )
                            AppUtils.setSharedPreferences(
                                requireContext(),
                                "logged_user",
                                activeUserId.toString()
                            )
                            findNavController().navigate(R.id.action_loginFragment_to_transportListFragment)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error ->
                        Log.e(
                            mTag,
                            "registrationRequest: JSON request error: ${error.printStackTrace()}"
                        )
                    }
                )
                que.add(jsonObjectRequest)
            } else {
                mSmsLoopCnt++
                if (mSmsLoopCnt > 15) {
                    restoreLoginBtn()

                    mBinding.loginStatusMessage.text =
                        "Nem érkezett regisztrációs SMS a kérésre!\nKérem ellenőrizze a telefonszámot!"
                    mBinding.loginStatusMessage.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.errorColor
                        )
                    )
                } else {
                    Log.i(mTag, "LOOP")
                    mSmsHandler.postDelayed(mSmsRunnable, mLoopTimeMilliSec)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun restoreLoginBtn() {
        mBinding.loginProgressBar.visibility = View.GONE
        mBinding.loginBtn.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == mReceiveSmsRequest) {
            when {
                grantResults.isEmpty() -> {
                    AppUtils.toast(
                        requireContext(),
                        "Nem tudjuk regisztrálni,\nha nem engedélyezi bejövő SMS-ek figyelését!"
                    )
                    mSmsLoopCnt = 100
                    restoreLoginBtn()
                    mSmsHandler.removeCallbacks(mSmsRunnable)
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    registerNewUser()
                }
                else -> {
                    AppUtils.toast(
                        requireContext(),
                        "Nem tudjuk regisztrálni,\nha nem engedélyezi bejövő SMS-ek figyelését!"
                    )
                    mSmsLoopCnt = 100
                    restoreLoginBtn()
                    mSmsHandler.removeCallbacks(mSmsRunnable)
                }
            }
        }
    }
}