package com.shengq.notificationmanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.shengq.notificationmanager.R
import com.shengq.notificationmanager.logic.amap.OPISearch
import com.shengq.notificationmanager.logic.dao.*
import com.shengq.notificationmanager.logic.network.OKHttpUpdateHttpService
import com.shengq.notificationmanager.logic.network.XUpdateServiceParser
import com.shengq.notificationmanager.logic.network.utils.SettingSPUtils
import com.shengq.notificationmanager.ui.adapter.CarPlanAdapter
import com.shengq.notificationmanager.logic.model.MainModel
import com.shengq.notificationmanager.logic.toast
import com.xuexiang.xhttp2.XHttp
import com.xuexiang.xhttp2.XHttpSDK
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION
import com.xuexiang.xupdate.utils.UpdateUtils
import com.xuexiang.xutil.app.PathUtils
import com.xuexiang.xutil.net.NetworkUtils
import com.xuexiang.xutil.tip.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(),
    PopupMenu.OnMenuItemClickListener, View.OnClickListener{
    companion object {
        //模板数据
//        var car01 = CarPlan("1314路","松岗塘下涌综合场站","皇岗口岸"
//            ,"07:40","洪桥头","松岗人民医院","暂无车次",1)

    }
    lateinit var mainModel: MainModel
    var arrayTest = arrayListOf<CarPlan>()
    var location:String = "暂无定位"
    private lateinit var busPlanDao:BusPlanDao
    lateinit var list: List<BusPlan>

    val locationText = R.id.location_now
    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what){
                locationText->{
                    location_now.text = msg.data.getString("address")
                }
            }
        }
    }
    //获取android权限
    private fun checkPublishPermission() {
        val permissions: MutableList<String> = mutableListOf()
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.INTERNET
            )
        ) {
            permissions.add(Manifest.permission.INTERNET)
        }

        if (permissions.size != 0) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray()
                , 0x01
            )
        }
    }
    private fun loadDatabaseInit(){
            thread {
                list = busPlanDao.loadAllBusPlan()
                list.forEach {
                    var carPlan = CarPlan(it.id,it.busName,it.startSite,it.endSite,it.startAddress,it.time,"","","",it.direction)
                    arrayTest.add(carPlan)
                }
            }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("权限",requestCode.toString())
    }
    @SuppressLint("RestrictedApi", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        busPlanDao = AppDatabase.getDatabase(applicationContext).BusPlanDao()
        loadDatabaseInit()

        val o = OPISearch(applicationContext).getLocation()
        o.startLocation()

        mainModel = ViewModelProvider(this).get(MainModel::class.java)
        val layoutManager = GridLayoutManager(this,1)
        recyclerView.layoutManager = layoutManager
        val adapter = CarPlanAdapter(arrayTest, busPlanDao,mainModel,this)
        recyclerView.adapter = adapter
        var view = this.window.decorView
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == -1){
            val mySnackbar = Snackbar.make(view,
                R.string.Authority,16000)
            mySnackbar.setAction("确定",this)
            mySnackbar.show()
        }

        val message = Message()
        Log.d("定位",OPISearch.address)
        thread{
            Log.d("定位",OPISearch.address)
            while (true){
                if (OPISearch.address.isNotEmpty()){
                    message.what =
                        R.id.location_now
//                    message.data.putString("address",OPISearch.address)
                    message.data.putString("address","景德镇市")
                    Log.d("定位",OPISearch.address)
                    mHandler.sendMessage(message)
                    OPISearch(applicationContext).getLocation().stopLocation()
                    break
                }
            }
        }
        initXHttp()
        initUpdate()

        //启动公交信息更新服务

        //通知栏功能代码
        val notificationLayout = RemoteViews("com.shengq.notificationmanager",
            R.layout.notificat
        )
        val notificationExpanded = RemoteViews("com.shengq.notificationmanager",
            R.layout.notificat
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        val channel = NotificationChannel("normal","距离站点提醒",
            NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)
        var notification  = NotificationCompat.Builder(this,"normal")
            .setContentTitle("公交助手")
            .setSmallIcon(R.drawable.ic_bus)
            .setAutoCancel(true)
            .setOngoing(true)
            .setColor(Color.GREEN)
            .setColorized(true)
            .setStyle(NotificationCompat.BigTextStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationExpanded)
            .setVisibility(VISIBILITY_PUBLIC)
        //自动开启通知
        manager.notify(1,notification.build())


//        updateNotice.setOnClickListener {
//            notificationLayout.setTextViewText(notificat_carName1,"888")
//            notification.bigContentView.setTextViewText(notificat_carName1,"666")
//            notification.bigContentView.setTextViewText(notificat_carName1,"777")
//            Toast.makeText(this,"8888",Toast.LENGTH_SHORT).show()
//            manager.notify(1,notification.build())
//        }
    }

    override fun onStart() {
//        NetIntentService.startActionBaz(this,arrayTest,"2")

        //注册广告并通知服务更新车辆信息
        var dataReceiver = MainReceiver()
        var filter = IntentFilter();// 创建IntentFilter对象
        filter.addAction("com.shengq.notificationmanager.ui.service");
        registerReceiver(dataReceiver, filter);// 注册Broadcast Receiver
        var myIntent =  Intent();//创建Intent对象
        myIntent.action = "com.shengq.notificationmanager.ui.service";
        myIntent.putExtra("cmd", "UPDATE_BUS");
        sendBroadcast(myIntent)
        super.onStart();
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar,menu)
        return true
    }
    // toolbar 点击事件处理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_open -> {
                //创建PopipMenu菜单
                var bt:View = findViewById(R.id.add_open)
                val popup = PopupMenu(this,bt)
                val inflater: MenuInflater = popup.menuInflater
                inflater.inflate(R.menu.tool_list, popup.menu)
                popup.setOnMenuItemClickListener(this@MainActivity)
                popup.show()
            }

        }
        return super.onOptionsItemSelected(item)
    }
    // +号弹出点击事件处理
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            return when(item.itemId){
                R.id.add_car ->{
                    val intent = Intent(this,
                        AddPlanActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"add_car",Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.updateBtn ->{
                    toast("正在检测更新")
                    val url: String = "http://47.102.86.236/xupdate/"
                    if (NetworkUtils.isUrlValid(url)) {
                        SettingSPUtils.get().serviceURL = url
                    }
                    XUpdate.newBuild(applicationContext)
                        .apkCacheDir(PathUtils.getExtDownloadsPath())
                        .updateHttpService(XUpdateServiceParser.getUpdateHttpService())
                        .isGet(false)
                        .updateUrl(XUpdateServiceParser.getVersionCheckUrl())
                        .updateParser(XUpdateServiceParser())
                        .update()
                    true
                }
                else -> false
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        checkPublishPermission()
        var o = OPISearch(applicationContext)
        var location = o.getLocation()
        location.startLocation()
        val message = Message()
        Thread{
            Log.d("定位",OPISearch.address)
            while (true){
                if (OPISearch.address.isNotEmpty()){
                    message.what =
                        R.id.location_now
                    message.data.putString("address",OPISearch.address)
                    Log.d("定位",OPISearch.address)
                    mHandler.sendMessage(message)
                    location.stopLocation()
                    break
                }
            }
        }.start()
    }


    private fun initUpdate() {
        XUpdate.get()
            .debug(true)
            .isWifiOnly(false) //默认设置只在wifi下检查版本更新
            .isGet(true) //默认设置使用get请求检查版本
            .isAutoMode(false) //默认设置非自动模式，可根据具体使用配置
            .param("versionCode", UpdateUtils.getVersionCode(this)) //设置默认公共请求参数
            .param("appKey", packageName)
            .setOnUpdateFailureListener { error ->
                Log.d("错误", error.toString())
                //设置版本更新出错的监听
                error.printStackTrace()
                if (error.code != CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                    ToastUtils.toast(error.toString())
                }
            }
            .supportSilentInstall(false) //设置是否支持静默安装，默认是true
            .setIUpdateHttpService(OKHttpUpdateHttpService()) //这个必须设置！实现网络请求功能。
            .init(application) //这个必须初始化

    }
    private fun initXHttp() {
        XHttpSDK.init(application) //初始化网络请求框架，必须首先执行
        XHttpSDK.debug("XHttp") //需要调试的时候执行
        XHttp.getInstance().setTimeout(20000)
    }
    inner class MainReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

        }
    }

}