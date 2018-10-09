package com.siang.pc.sleep;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.siang.pc.adapter.FragmentPagerAdapter_main;
import com.siang.pc.fragment.Fragment_data_day;
import com.siang.pc.fragment.Fragment_data_month;
import com.siang.pc.fragment.Fragment_tips;
import com.siang.pc.fragment.Fragment_data_week;
import com.siang.pc.fragment.Fragment_analysis;

public class Activity_main_data extends FragmentActivity implements View.OnClickListener,ViewPager.OnPageChangeListener {

    //variable
    private PieChart pieChart;

    private List<Integer> datas = new ArrayList<Integer>();

    private Queue<Integer> data0Q = new LinkedList<Integer>();

    private int flag = 0;

    private ViewPager myviewpagerData;
    private ViewPager myviewpagerAnalysis;
    //选项卡中的按钮
    private Button btnDay;
    private Button btnWeek;
    private Button btnMonth;
    private Button btnYourSleep;
    private Button btnSleepTips;
    private FloatingActionButton btnBluetooth;
    //作为指示标签的按钮
    private Button cursorData;
    private Button cursorAnalysis;
    //所有标题按钮的数组
    private Button[] btnArgsData;
    private Button[] btnArgs2Analysis;
    //fragment的集合，对应每个子页面
    private ArrayList<Fragment> fragmentsData;
    private ArrayList<Fragment> fragmentsAnalysis;

    //通过include其他界面layout实现界面切换
    private View include1;
    private View include2;
    private View include3;
    private ImageButton imgButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    //private ImageButton imgButton2;

    //bluetooth
    private static final int REQUEST_ENABLE_BT=2;
    //check bluetooth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setButton();
        initView();
        buildFragmentAdapter();
        myviewpagerData.addOnPageChangeListener(this);
        myviewpagerAnalysis.addOnPageChangeListener(this);
    }

    public void findView() {//唤起menu边栏的view
        include1 = (View) findViewById(R.id.include_data);
        include2 = (View) findViewById(R.id.include_analysis);
        include3 = (View) findViewById(R.id.include_connect);
        imgButton = (ImageButton) findViewById(R.id.top_account);//边栏唤起的icon
        //imgButton2 = (ImageButton) findViewById(R.id.imgButtonBack);
        drawerLayout = (DrawerLayout) findViewById(R.id.draw);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        setDrawerLeftEdgeSize(this, drawerLayout, 0.2f);
    }

    public void setButton() {
        imgButton.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //在这里处理item的点击事件
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_data_collection:
                        include1.setVisibility(View.VISIBLE);
                        include2.setVisibility(View.GONE);
                        include3.setVisibility(View.VISIBLE);
                        myviewpagerData.setVisibility(View.VISIBLE);
                        myviewpagerAnalysis.setVisibility(View.GONE);
                        break;
                    case R.id.nav_analysis_advices:
                        include1.setVisibility(View.GONE);
                        include2.setVisibility(View.VISIBLE);
                        include3.setVisibility(View.GONE);
                        myviewpagerData.setVisibility(View.GONE);
                        myviewpagerAnalysis.setVisibility(View.VISIBLE);
                        /*Intent intent = new Intent(Activity_main_data.this, Activity_analysis.class);
                        startActivity(intent);*/
                        break;
                }
                return true;
            }
        });
    }

    public static void setDrawerLeftEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            Field leftDraggerField = drawerLayout.getClass().getDeclaredField("mLeftDragger");
            leftDraggerField.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) leftDraggerField.get(drawerLayout);
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (dm.widthPixels * displayWidthPercentage)));
        } catch (Exception e) {
        }
    }


    public void buildFragmentAdapter() {
        //data collection界面
        fragmentsData = new ArrayList<Fragment>();
        fragmentsData.add(new Fragment_data_day());
        fragmentsData.add(new Fragment_data_week());
        fragmentsData.add(new Fragment_data_month());
        FragmentPagerAdapter_main adapter = new FragmentPagerAdapter_main(getSupportFragmentManager(), fragmentsData);
        myviewpagerData.setAdapter(adapter);
        myviewpagerData.setOffscreenPageLimit(2);//缓存页面数目
        //analysis and advice界面
        fragmentsAnalysis = new ArrayList<Fragment>();
        fragmentsAnalysis.add(new Fragment_analysis());
        fragmentsAnalysis.add(new Fragment_tips());
        FragmentPagerAdapter_main adapter2 = new FragmentPagerAdapter_main(getSupportFragmentManager(), fragmentsAnalysis);
        myviewpagerAnalysis.setAdapter(adapter2);
        myviewpagerAnalysis.setOffscreenPageLimit(2);
    }

    //初始化布局
    public void initView(){
        myviewpagerData = (ViewPager)this.findViewById(R.id.myviewpager_data);

        btnDay = (Button)this.findViewById(R.id.btn_day);
        btnWeek = (Button)this.findViewById(R.id.btn_week);
        btnMonth = (Button)this.findViewById(R.id.btn_month);
        //button字体样式设置
        btnDay.setTypeface(null, Typeface.NORMAL);
        btnWeek.setTypeface(null, Typeface.NORMAL);
        btnMonth.setTypeface(null, Typeface.NORMAL);
        //初始化按钮数组
        btnArgsData = new Button[]{btnDay, btnWeek, btnMonth};
        //指示标签设置为加粗
        cursorData = btnDay;
        cursorData.setTypeface(null, Typeface.BOLD);

        btnDay.setOnClickListener(this);
        btnWeek.setOnClickListener(this);
        btnMonth.setOnClickListener(this);

        myviewpagerAnalysis = (ViewPager)this.findViewById(R.id.myviewpager_analysis);

        btnYourSleep = (Button)this.findViewById(R.id.btn_your_sleep);
        btnSleepTips = (Button)this.findViewById(R.id.btn_sleep_tips);

        btnYourSleep.setTypeface(null, Typeface.NORMAL);
        btnSleepTips.setTypeface(null, Typeface.NORMAL);
        //初始化按钮数组
        btnArgs2Analysis = new Button[]{btnYourSleep, btnSleepTips};
        //指示标签设置为加粗
        cursorAnalysis = btnYourSleep;
        cursorAnalysis.setTypeface(null, Typeface.BOLD);

        btnYourSleep.setOnClickListener(this);
        btnSleepTips.setOnClickListener(this);

        //蓝牙连接按钮
        btnBluetooth =(FloatingActionButton)this.findViewById(R.id.floatingActionButton_bluetooth);
        btnBluetooth.setOnClickListener(this);

    }
    @Override
    public void onClick(View btnChoose) {
        switch (btnChoose.getId()) {
            case R.id.btn_day:
                myviewpagerData.setCurrentItem(0);
                break;
            case R.id.btn_week:
                myviewpagerData.setCurrentItem(1);
                break;
            case R.id.btn_month:
                myviewpagerData.setCurrentItem(2);
                break;
            case R.id.btn_your_sleep:
                myviewpagerAnalysis.setCurrentItem(0);
                break;
            case R.id.btn_sleep_tips:
                myviewpagerAnalysis.setCurrentItem(1);
                break;
            case R.id.top_account:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.floatingActionButton_bluetooth:
                Toast.makeText(this,"bluetooth connecting",Toast.LENGTH_SHORT).show();
                bluetoothConnect();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        if (myviewpagerData.getVisibility() == View.VISIBLE) {
            //还原加粗
            cursorData.setTypeface(null, Typeface.NORMAL);
            //设置加粗
            cursorData = btnArgsData[arg0];
            cursorData.setTypeface(null, Typeface.BOLD);
        }
        else {
            //还原加粗
            cursorAnalysis.setTypeface(null, Typeface.NORMAL);
            //设置加粗
            cursorAnalysis = btnArgs2Analysis[arg0];
            cursorAnalysis.setTypeface(null, Typeface.BOLD);
        }
    }

    public void bluetoothConnect(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        //open bluetooth
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            if(onActivityResult(REQUEST_ENABLE_BT);)
//            onActivityResult(REQUEST_ENABLE_BT,bluetoothResult,);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        //todo waiting for rebuild
//        if (pairedDevices.size() > 0) {
//            // Loop through paired devices
//            String[] pairedDevicesString={};
//            for (BluetoothDevice device : pairedDevices) {
//                ArrayAdapter<String> mArrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,pairedDevicesString);
//                // Add the name and address to an array adapter to show in a ListView
//                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//            }
//        }
    }
}
