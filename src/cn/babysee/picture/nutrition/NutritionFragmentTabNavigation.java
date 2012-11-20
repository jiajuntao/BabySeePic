package cn.babysee.picture.nutrition;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import cn.babysee.picture.R;
import cn.babysee.picture.base.BaseFragmentActivity;
import cn.babysee.picture.env.StatServiceEnv;

import com.baidu.mobstat.StatService;
import com.viewpagerindicator.TabPageIndicator;

public class NutritionFragmentTabNavigation extends BaseFragmentActivity {

    private static String[] CONTENT = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutrition_tab_navigation);

        CONTENT = new String[] { getString(R.string.nutrition_tab_1),
                getString(R.string.nutrition_tab_2), getString(R.string.nutrition_tab_3) };

        FragmentPagerAdapter adapter = new NutritionAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                //打点
                switch (position) {
                    case 0:
                        StatService.onEvent(mContext, StatServiceEnv.NUTRION_PHASE1_EVENT_ID,
                                StatServiceEnv.NUTRION_PHASE1_LABEL, 1);
                        break;
                    case 1:
                        StatService.onEvent(mContext, StatServiceEnv.NUTRION_PHASE2_EVENT_ID,
                                StatServiceEnv.NUTRION_PHASE2_LABEL, 1);
                        break;
                    case 2:
                        StatService.onEvent(mContext, StatServiceEnv.NUTRION_PHASE3_EVENT_ID,
                                StatServiceEnv.NUTRION_PHASE3_LABEL, 1);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    class NutritionAdapter extends FragmentPagerAdapter {

        public NutritionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 2) {
                return new NutritionFragment();
            }
            return NutritionFragment2.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
}
