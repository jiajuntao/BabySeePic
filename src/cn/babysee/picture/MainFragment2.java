package cn.babysee.picture;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cn.babysee.base.BaseFragment;
import cn.babysee.picture.book.YuErZhiNanListActivity;
import cn.babysee.picture.book.ZhiliFangChengListActivity;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.picture.guide.GrowAdviceActivity;
import cn.babysee.picture.guide.Guide01Activity;
import cn.babysee.picture.guide.GuideListActivity;
import cn.babysee.picture.guide.IntlligenceControlTableActivity;
import cn.babysee.picture.guide.LeftRightBrainActivity;
import cn.babysee.picture.tools.BabyHeightActivity;

import com.baidu.mobstat.StatService;

public class MainFragment2 extends BaseFragment implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "MainFragment2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_tab2, container, false);
        v.findViewById(R.id.zhilifangcheng).setOnClickListener(this);
        v.findViewById(R.id.guideone).setOnClickListener(this);
        v.findViewById(R.id.grow_advice).setOnClickListener(this);
        v.findViewById(R.id.baby_heigth).setOnClickListener(this);
        v.findViewById(R.id.guide).setOnClickListener(this);
        v.findViewById(R.id.yuerzhinan).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.zhilifangcheng:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_TOOLS_EVENT_ID, StatServiceEnv.MAIN_TOOLS_LABEL, 1);
            startActivity(new Intent(mContext, ZhiliFangChengListActivity.class));
            break;
        case R.id.guideone:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_TOOLS_EVENT_ID, StatServiceEnv.MAIN_TOOLS_LABEL, 1);
            startActivity(new Intent(mContext, Guide01Activity.class));
            break;
        case R.id.grow_advice:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_GROW_ADVICE_EVENT_ID, StatServiceEnv.MAIN_GROW_ADVICE_LABEL, 1);
            startActivity(new Intent(mContext, GrowAdviceActivity.class));
            break;
        case R.id.baby_heigth:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_BABY_HEIGHT_EVENT_ID, StatServiceEnv.MAIN_BABY_HEIGHT_LABEL, 1);
            startActivity(new Intent(mContext, BabyHeightActivity.class));
            break;
        case R.id.guide:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_GUIDE_EVENT_ID, StatServiceEnv.MAIN_GUIDE_LABEL, 1);
            startActivity(new Intent(mContext, GuideListActivity.class));
            break;
        case R.id.yuerzhinan:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_YUERZHINAN_EVENT_ID,
                    StatServiceEnv.MAIN_YUERZHINAN_LATER_LABEL, 1);
            startActivity(new Intent(mContext, YuErZhiNanListActivity.class));
            break;

        default:
            break;
        }
    }
}
