package hu.selester.selnet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.model.LatLng
import hu.selester.selnet.adapters.TransportListAdapter
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.database.tables.CompaniesTable
import hu.selester.selnet.helper.HelperClass
import hu.selester.selnet.helper.MySingleton
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import hu.selester.selnet.database.tables.TaskActionsTable
import hu.selester.selnet.database.tables.TasksTable
import kotlinx.android.synthetic.main.frg_transports_list.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class TransportsListFragment : Fragment(), TransportListAdapter.RowClickListener {

    lateinit var rootView: View
    lateinit var db: SelTransportDatabase
    private var taskList: List<TasksTable> = emptyList()
    var isOnclick = false
    var isAction = false
    var taskId = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.frg_transports_list, container, false)
        rootView.transports_list_orderList.layoutManager = LinearLayoutManager(context)
        if (isOnclick) {
            loadListData()
        } else {
            rootView.transports_list_orderList.adapter = TransportListAdapter(
                context!!,
                taskList,
                this
            )
        }
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = SelTransportDatabase.getInstance(context!!)!!
        if (!arguments!!.getBoolean("isOnClick")) {
            loadData()
        } else {
            isOnclick = true
            isAction = arguments!!.getBoolean("isAction")
            taskId = arguments!!.getLong("taskId")
        }
    }

    override fun click(id: Long, type: Int) {
        when (type) {
            TransportListAdapter.TYPE_TASK -> {
                val bundle = bundleOf("isOnClick" to true, "isAction" to false, "taskId" to id)
                findNavController().navigate(R.id.action_transportsListFragment_self, bundle)
            }
            TransportListAdapter.TYPE_ACTION -> {
                val action = db.tasksActionsDao().getById(id)
                if (action.size != 1) {
                    throw ArrayIndexOutOfBoundsException("There should be only one matching action")
                }
                when (action[0].Procedure) {
                    "Nested" -> {
                        val bundle =
                            bundleOf("isOnClick" to true, "isAction" to true, "taskId" to id)
                        findNavController().navigate(
                            R.id.action_transportsListFragment_self,
                            bundle
                        )
                    }
                    "Signature" -> {
                        val bundle = bundleOf()
                        findNavController().navigate(
                            R.id.action_transportsListFragment_to_signatureFragment,
                            bundle
                        )
                    }
                    "Photo" -> {
                        val bundle = bundleOf()
                        findNavController().navigate(
                            R.id.action_transportsListFragment_to_transPhotoFragment,
                            bundle
                        )
                    }
                    "Info" -> {
                        val bundle = bundleOf(
                            "name" to getNameForAction(action[0]),
                            "address" to getAddressForAction(action[0]),
                            "short_info" to getShortInfoForAction(action[0]),
                            "long_info" to getLongInfoForAction(action[0])
                        )
                        findNavController().navigate(
                            R.id.action_transportsListFragment_to_transportInfoFragment,
                            bundle
                        )
                    }
                    else -> throw java.lang.IllegalArgumentException("Unknown procedure type")
                }

            }
            else -> throw IllegalArgumentException("Invalid View type")
        }
        SessionClass.setValue("taskId", id.toString())
    }

    private fun loadData() {
        val map = HashMap<String, String>()
        map["Phone"] = HelperClass.getSharedPreferences(context!!, "phoneNumber")
        map["Regkey"] = HelperClass.getSharedPreferences(context!!, "verifyID")
        Log.i("tag", map.toString())
        val url = resources.getString(R.string.root_url) + "/WhatIsmyTasks2"
        Log.i("URL", url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, JSONObject(map as Map<*, *>),
            Response.Listener { jsonRoot ->
                Log.i("TAG", jsonRoot.toString())
                try {
                    val resultJSON = JSONObject(jsonRoot.getString("WhatIsmyTasks2Result"))
                    val messageJSON = resultJSON.getJSONObject("message")
                    val headJSON = messageJSON.getJSONObject("head")
                    if (headJSON.getString("operation") != "task_list") {
                        HelperClass.toast(context, "Hiba a kommunikációban!")

                    } else {
                        val details = messageJSON.getJSONObject("details").getJSONArray("task")
                        processData(details)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    HelperClass.toast(context, "Hiba a kommunikációban!")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
    }

    private fun processData(jsonData: JSONArray) {
        db.tasksDao().deleteAllData()
        db.tasksActionsDao().deleteAllData()
        for (compNum in 0 until jsonData.length()) {
            val jsonCompanies = jsonData.getJSONObject(compNum)
            db.companiesDao().insertCompanies(
                CompaniesTable(
                    jsonCompanies.getLong("id"),
                    jsonCompanies.getString("company_name"),
                    jsonCompanies.getLong("order_id"),
                    jsonCompanies.getString("order_number")
                )
            )
            //Log.i("TAG", jsonCompanies.getString("ID") + " - " + jsonCompanies.getString("Company_Code") + " - " + jsonCompanies.getString("Order_id") + " - " + jsonCompanies.getString("Order_Number") )

            var address: String
            val city: String
            val zip: String
            val cord: LatLng

            val addressJSON = jsonCompanies.getJSONObject("address")
            city = addressJSON.getString("city")
            zip = addressJSON.getString("zip")
            address = addressJSON.getString("addr_road") + " " +
                    addressJSON.getString("addr_ps_type") + " " +
                    addressJSON.getString("addr_housenr") + " " +
                    addressJSON.getString("addr_building") + " " +
                    addressJSON.getString("addr_stairway") + " " +
                    addressJSON.getString("addr_floor") + " " +
                    addressJSON.getString("addr_door")
            address = address.trim().replace(" +".toRegex(), " ")
            val coordJson = jsonCompanies.getJSONObject("coord")
            cord = LatLng(coordJson.getDouble("lat"), coordJson.getDouble("lon"))
            Log.i("TAG", address + " - " + cord.latitude + " : " + cord.longitude)
            Log.i("TAG", "TASK COUNT: " + db.tasksDao().getCount())

            val taskId = db.tasksDao().insertTask(
                TasksTable(
                    null,
                    jsonCompanies.getLong("order_id"),
                    jsonCompanies.getString("company_name"),
                    jsonCompanies.getString("short_info"),
                    jsonCompanies.getString("long_info"),
                    address,
                    zip,
                    city,
                    cord.latitude,
                    cord.longitude
                )
            )

            val actionsNode = jsonCompanies.getJSONObject("actions")
            val actionArray = actionsNode.getJSONArray("action")
            for (actionIt in 0 until actionArray.length()) {
                loadAction(actionArray.getJSONObject(actionIt), actionIt, taskId, false)
            }
        }
        taskList = db.tasksDao().getAllData()
        loadListData()
    }

    private fun loadAction(action: JSONObject, actionIt: Int, taskId: Long, isSubTask: Boolean) {
        val isNested = action.getString("nested") != "0"
        if (!isNested) {
            db.tasksActionsDao().insertAction(
                TaskActionsTable(
                    null,
                    taskId,
                    actionIt,
                    isSubTask,
                    action.getString("name"),
                    action.getString("procedure")
                )
            )
        } else {
            val actionId = db.tasksActionsDao().insertAction(
                TaskActionsTable(
                    null,
                    taskId,
                    actionIt,
                    isSubTask,
                    action.getString("name"),
                    "Nested"
                )
            )
            val nestedArray = action.getJSONObject("procedures").getJSONArray("action")
            for (nestedIt in 0 until nestedArray.length()) {
                loadAction(nestedArray.getJSONObject(nestedIt), nestedIt, actionId, true)
            }
        }
    }

    private fun loadListData() {
        rootView.transports_list_orderList.layoutManager = LinearLayoutManager(context)
        if (!isOnclick) {
            rootView.transports_list_orderList.adapter = TransportListAdapter(
                context!!,
                taskList,
                this
            )
        } else {
            rootView.transports_list_orderList.adapter = TransportListAdapter(
                context!!,
                db.tasksActionsDao().getActionsForTask(taskId),
                this
            )
        }
    }

    private fun getNameForAction(action: TaskActionsTable): String {
        return getTaskForAction(action).company
    }

    private fun getAddressForAction(action: TaskActionsTable): String {
        return getTaskForAction(action).address
    }

    private fun getLongInfoForAction(action: TaskActionsTable): String {
        return getTaskForAction(action).longInfo
    }

    private fun getShortInfoForAction(action: TaskActionsTable): String {
        return getTaskForAction(action).shortInfo
    }

    private fun getTaskForAction(action: TaskActionsTable): TasksTable {
        return if (!action.IsSubTask) {
            val task = db.tasksDao().getData(action.TaskId)
            if (task.size != 1) {
                throw ArrayIndexOutOfBoundsException("There should be only one matching task")
            }
            task[0]
        } else {
            val parentAction = db.tasksActionsDao().getById(action.TaskId)
            if (parentAction.size != 1) {
                throw ArrayIndexOutOfBoundsException("There should be only one matching task")
            }
            getTaskForAction(parentAction[0])
        }
    }

    /*

    override fun Click(login_code:String) {
        super.Click(login_code)
        checkLoginParameter(login_code)
    }

    fun checkLoginParameter(code: String){
        val url = resources.getString(R.string.root_url) + "/WEB_REASTAPI_USERVALIDATE_LOG_IN_TRAN/" + code
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val json = JSONObject(jsonRoot.getString("WEB_REASTAPI_USERVALIDATE_LOG_IN_TRANResult"))
                    if (json.getInt("ERROR_CODE") == -1) {
                        SessionClass.setValue("workCode", code)
                        SessionClass.setValue("ORD_L_ID", json.getString("ORD_L_ID"))
                        activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, WorkDatasFragment()).addToBackStack("App").commit()
                    } else {
                        HelperClass.toast(context, json.getString("ERROR_TEXT"))
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    HelperClass.toast(context,"Hiba a kommunikációban!")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
    }

    fun loadListData(){
        val terminal = SessionClass.getValue("terminal")
        val account = SessionClass.getValue("ACCOUNT")
        val password = SessionClass.getValue("PASSWORD")
        val url = resources.getString(R.string.root_url) + "/Get_Tran_TASKS/" + account + "/" + password + "/" + terminal
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val jsonText = jsonRoot.getString("Get_Tran_TASKSResult")
                    if(jsonText.substring(0,1) == "["){
                        val json = JSONArray(jsonText)
                        var dataList = mutableListOf<OrderNums>()
                        for(i in 0..(json.length()-1)){
                            dataList.add(OrderNums(null,json.getJSONObject(i).getString("LOGIN_CODE"),json.getJSONObject(i).getString("ORD_NUM")))
                        }
                        val adapter = TransportListAdapter(context!!,dataList, this)
                        rootView.transports_list_orderList.adapter = adapter
                    }else{
                        val json = JSONObject(jsonText)
                        if (json.getInt("ERROR_CODE") != -1) {
                            HelperClass.toast(context, json.getString("ERROR_TEXT"))
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    HelperClass.toast(context,"Hiba a kommunikációban!")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
    }
    */
}
