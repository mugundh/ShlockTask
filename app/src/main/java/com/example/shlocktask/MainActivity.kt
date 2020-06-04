package com.example.shlocktask

import CoreClient
import ServiceGenerator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shlocktask.model.InspectionList
import com.example.shlocktask.sqlDb.UserDbHelper
import com.example.shlocktask.utils.NetworkChangeReceiver
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {
    private var alertmDialog: Dialog? = null
    lateinit var delete: MenuItem
    lateinit var inspectionListView:RecyclerView
    lateinit var noData: TextView
    lateinit var  userDBHelper:UserDbHelper
    lateinit var sqLiteDatabase:SQLiteDatabase
    lateinit var networkChangeReceiver: NetworkChangeReceiver
    lateinit var inspectionListAdapter: InspectionListAdapter
    lateinit var recipeList :List<InspectionList.InspectionItem>
    lateinit var inspectionItem:InspectionList.InspectionItem
    var preposition:Int=-1
    var position:Int?=null
    var inspectionList :ArrayList<InspectionList.InspectionItem> = ArrayList()
    private val p = Paint()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDBHelper= UserDbHelper(this)
       sqLiteDatabase = userDBHelper.writableDatabase
        setContentView(R.layout.activity_main)
        networkChangeReceiver=NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, IntentFilter(Context.NETWORK_STATS_SERVICE))
        inspectionListView=findViewById<RecyclerView>(R.id.employee_list)
        inspectionListView.layoutManager= LinearLayoutManager(this)
        noData=findViewById(R.id.nodataTxt)

        inspectionListAdapter=InspectionListAdapter(
            this@MainActivity,
            inspectionList
        )
        inspectionListView.adapter=inspectionListAdapter
        enableSwipe()
    }

    override fun onResume() {
        super.onResume()
        if(networkChangeReceiver.isNetworkAvailable(this)){
            callEmployeeData()
            inspectionListAdapter.updateList(getAllData())
        }
        else{
            if(inspectionList.size<0){
                noData.visibility= View.VISIBLE
            }
            else{
                noData.visibility= View.GONE
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        delete = menu.findItem(R.id.ascending)
        delete.setOnMenuItemClickListener {
            successAlert(this,"Success","Are you sure, Do you want to delete this item permanently",false,true,"Delete","Cancel")
            true
        }
        return true
    }
    private fun callEmployeeData() {

        val client: CoreClient =
                ServiceGenerator(this).createService(CoreClient::class.java)

        val coreResponse: Call<InspectionList?>? = client.coreDetails()
//        showDialog()
        coreResponse!!.enqueue(
                object : Callback<InspectionList?> {
                    override fun onResponse(
                            call: Call<InspectionList?>,
                            response: Response<InspectionList?>
                    ) {
//                        closeDialog()
                        if (response.isSuccessful) {
                            val inspectionList: InspectionList? = response.body()
                            if (inspectionList != null) {
                               var  inspectionArray :JSONArray= JSONArray(inspectionList.getInspectionsResult)
                                val listType: Type = object :
                                    TypeToken<List<InspectionList.InspectionItem?>?>() {}.type
                                recipeList = Gson().fromJson(inspectionArray.toString(), listType)
                                println("hhhhh==="+recipeList[0].hdn_ScheduleID)
                                userDBHelper.eraseData(sqLiteDatabase)
                                writeInDB()
                            } else {

                                Toast.makeText(this@MainActivity,"Error occured2", Toast.LENGTH_LONG).show()
                            }
                        } else {

                            Toast.makeText(this@MainActivity,"Error occured3", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(
                            call: Call<InspectionList?>,
                            t: Throwable
                    ) {
                        t.printStackTrace()
//                        closeDialog()
                        Toast.makeText(this@MainActivity,"Server Error", Toast.LENGTH_LONG).show()

                        // (this != null)
                        //((MainActivity) this).closeProgressDialog();
                    }
                })

    }
    fun writeInDB()
    {
        this.runOnUiThread(Runnable {
            for (item in recipeList)
            userDBHelper.addInformation(item, sqLiteDatabase)
        })
        inspectionListAdapter.updateList(getAllData())
    }
    fun getAllData(): ArrayList<InspectionList.InspectionItem>
    {
        inspectionList.clear()
        var cursor:Cursor=userDBHelper.getInformations(sqLiteDatabase)
        cursor.moveToFirst()
        while (cursor.moveToNext())
        {
            var index1:Int=cursor.getColumnIndex(UserDbHelper.column1)
            var index2:Int=cursor.getColumnIndex(UserDbHelper.column2)
            var index3:Int=cursor.getColumnIndex(UserDbHelper.column3)
            var index4:Int=cursor.getColumnIndex(UserDbHelper.column4)
            var index5:Int=cursor.getColumnIndex(UserDbHelper.column5)
            var index6:Int=cursor.getColumnIndex(UserDbHelper.column6)
            var id:String=cursor.getString(index1)
            var hdn_ScheduleCode:String=cursor.getString(index2)
            var LTE_INSP_Label1:String=cursor.getString(index3)
            var LTE_INSP_Label2:String=cursor.getString(index4)
            var LTE_INSP_Label3:String=cursor.getString(index5)
            var LTE_INSP_Label5:String=cursor.getString(index6)
            println("jjjjjj=="+LTE_INSP_Label5)
            var inspectionItem:InspectionList.InspectionItem= InspectionList.InspectionItem()
            inspectionItem.hdn_ScheduleID=id
            inspectionItem.LTE_INSP_Label1=LTE_INSP_Label1
            inspectionItem.LTE_INSP_Label2=LTE_INSP_Label2
            inspectionItem.LTE_INSP_Label3=LTE_INSP_Label3
            inspectionItem.LTE_INSP_Label5=LTE_INSP_Label5
            print("label1"+LTE_INSP_Label2)
            inspectionList.add(inspectionItem)
        }
        print("mjmjmjmj"+inspectionList.size)
        return inspectionList
    }
    private fun enableSwipe() {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT) {

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags: Int = ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT
                    return makeMovementFlags(dragFlags, swipeFlags)
                }


                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
return false
                }


                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    position = viewHolder.adapterPosition
                    inspectionItem =inspectionList[position!!]
                    print("direction==="+inspectionItem.hdn_ScheduleID)
                    if (direction == ItemTouchHelper.LEFT||direction==ItemTouchHelper.RIGHT) {
                        showDeleteOption(true)
                        print("aaaaaaaaa")
                    } else {
                       showDeleteOption(false)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    val icon: Bitmap
                    var bool:Boolean=isCurrentlyActive
                    if(dX==0.0f){
                        showDeleteOption(false)
                    }
                    if (actionState == 1) {
                        val itemView = viewHolder.itemView
                        val height =
                            itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        if (dX > 0) {
                            print("11111")
                            p.setColor(Color.parseColor("#388E3C"))
                            icon = BitmapFactory.decodeResource(resources, R.drawable.delete)
                            val icon_dest = RectF(
                                itemView.left.toFloat() + width,
                                itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width,
                                itemView.bottom.toFloat() - width
                            )

                            c.drawBitmap(icon, null, icon_dest, p)
                        } else {
                            print("22222")
                            p.color = Color.parseColor("#D32F2F")
                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            icon = BitmapFactory.decodeResource(resources, R.drawable.delete)
                            val icon_dest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)

                        }
                    }
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX/4,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(inspectionListView)
    }

    fun successAlert(
        mcontext: Context?,
        title: String,
        message: String,
        status:Boolean,
        btn_visible: Boolean,
        success_txt: String,
        failure_txt: String
    ) {
        try {
            if (alertmDialog != null && alertmDialog!!.isShowing()) {
                alertmDialog!!.dismiss()
            }
            val view =
                View.inflate(mcontext, R.layout.success_alert, null)
            alertmDialog = Dialog(mcontext!!)
            alertmDialog!!.setContentView(view)
            alertmDialog!!.setCancelable(false)
            alertmDialog!!.show()
            val messageTxt: AppCompatTextView = alertmDialog!!.findViewById(R.id.alert_message_txt)
            val successBtn: AppCompatTextView = alertmDialog!!.findViewById(R.id.button_success)
            val failureBtn: AppCompatTextView = alertmDialog!!.findViewById(R.id.button_failure)
            val btnlay: RelativeLayout =
                alertmDialog!!.findViewById(R.id.alert_btn_lay)
            val failureImg: ImageView =
                alertmDialog!!.findViewById(R.id.failed_img)
            val successImg: ImageView =
                alertmDialog!!.findViewById(R.id.success_img)
            if (!status) {
                successImg.visibility = View.GONE
                failureImg.visibility = View.VISIBLE
            } else {
                successImg.visibility = View.VISIBLE
                failureImg.visibility = View.GONE
            }
            if (!message.equals("", ignoreCase = true)) messageTxt.setText(message)
            if (!success_txt.equals("", ignoreCase = true)) successBtn.setText(success_txt)
            if (!failure_txt.equals(
                    "",
                    ignoreCase = true
                )
            ) failureBtn.setText(failure_txt) else failureBtn.setVisibility(View.GONE)
            btnlay.visibility = if (btn_visible) View.VISIBLE else View.GONE
            successBtn.setOnClickListener {
                alertmDialog!!.dismiss()
                deleteInspection(inspectionItem)

            }
            failureBtn.setOnClickListener { alertmDialog!!.dismiss()
            inspectionListAdapter.notifyDataSetChanged()
            showDeleteOption(false)}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun deleteInspection(inspectioItem:InspectionList.InspectionItem){

        if(userDBHelper.deleteInception(sqLiteDatabase,inspectioItem.hdn_ScheduleID)){
            inspectionListAdapter.updateList(getAllData())
            print("\nsuccess delete\n")

        }else
            print("\nFailure delete")
    }
    fun showDeleteOption(show:Boolean){
        delete.isVisible = show

    }
}

