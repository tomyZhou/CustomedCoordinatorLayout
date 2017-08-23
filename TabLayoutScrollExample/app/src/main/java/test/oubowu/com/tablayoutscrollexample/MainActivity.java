package test.oubowu.com.tablayoutscrollexample;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页，有viewpager来管理切换fragment
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private RelativeLayout mAppBarLayout;

    /**
     * tablayout的高度
     */
    public static int mTabLayoutHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView() {

        initToolbar();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.measure(0, 0);
        mTabLayoutHeight = mTabLayout.getMeasuredHeight();

        mAppBarLayout = (RelativeLayout) findViewById(R.id.appbar);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 页面切换的话，tablayout回到原位
                mAppBarLayout.scrollBy(0, -mAppBarLayout.getScrollY());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        TextView tv = new TextView(this);
        tv.setText("点击跳回顶部");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EventBus通知此tag对于订阅事件者返回顶部
                EventBus.getDefault().post(new EventBusElement(), "scrollToTop");
            }
        });
        // toolbar添加一个可点击返回顶部的textview
        mToolbar.addView(tv);
        setSupportActionBar(mToolbar);
    }

    private void initData() {

        ArrayList<String> tabTitles = new ArrayList<>();
        tabTitles.add("AAAA");
        tabTitles.add("BBBB");
        tabTitles.add("CCCC");
        tabTitles.add("DDDD");
        tabTitles.add("EEEE");
        tabTitles.add("FFFF");
        tabTitles.add("GGGG");
        tabTitles.add("HHHH");

        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < tabTitles.size(); i++) {
            // tablayout添加标签
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(i)));
            fragments.add(new HomeSimpleFragment());
        }

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), fragments, tabTitles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Subscriber(tag = "scroollTabLayout")
    private void scroollTabLayout(EventBusElement element) {
        if(element.getAppBarOffset()>0){
            if((mAppBarLayout.getScrollY()+element.getAppBarOffset())<=mAppBarLayout.getHeight()){
                mAppBarLayout.scrollBy(0, element.getAppBarOffset());
            }else{
                mAppBarLayout.scrollBy(0,mTabLayoutHeight-mAppBarLayout.getScrollY());
            }
        }else if(element.getAppBarOffset()<0){
            if((mAppBarLayout.getScrollY()+element.getAppBarOffset())>0){
                mAppBarLayout.scrollBy(0, element.getAppBarOffset());
            }
            else{
                mAppBarLayout.scrollBy(0,-mAppBarLayout.getScrollY());
            }
        }
        Log.i("scrollYoffset", element.getAppBarOffset() + "");
        Log.i("scrollY", mAppBarLayout.getScrollY() + "");
        Log.i("scrollYheight", mAppBarLayout.getHeight() + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
