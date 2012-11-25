package cn.babysee.picture;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cn.babysee.picture.base.BaseFragment;
import cn.babysee.picture.book.BaikeListActivity;
import cn.babysee.picture.book.YuErZhiNanListActivity;
import cn.babysee.picture.draw.DrawBoardActivity;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.picture.env.StatServiceEnv;
import cn.babysee.picture.game.GameListActivity;
import cn.babysee.picture.guide.GuideListActivity;
import cn.babysee.picture.nutrition.NutritionFragmentTabNavigation;
import cn.babysee.picture.test.TestListActivity;

import com.baidu.mobstat.StatService;

public class MainFragment extends BaseFragment implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;

    private String TAG = "MainFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_tab, container, false);
        v.findViewById(R.id.brush).setOnClickListener(this);
        v.findViewById(R.id.test).setOnClickListener(this);
        v.findViewById(R.id.game).setOnClickListener(this);
        v.findViewById(R.id.seepic).setOnClickListener(this);
        v.findViewById(R.id.guide).setOnClickListener(this);
        v.findViewById(R.id.nutrition).setOnClickListener(this);
        v.findViewById(R.id.yuerzhinan).setOnClickListener(this);
        v.findViewById(R.id.yuerbaike).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.brush:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_BABYDRAW_EVENT_ID, StatServiceEnv.MAIN_BABYDRAW_LABEL, 1);
            startActivity(new Intent(mContext, DrawBoardActivity.class));
            break;
        case R.id.game:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_BABYGAME_EVENT_ID, StatServiceEnv.MAIN_BABYGAME_LABEL, 1);
            startActivity(new Intent(mContext, GameListActivity.class));
            break;
        case R.id.test:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_TEST_EVENT_ID, StatServiceEnv.MAIN_TEST_LABEL, 1);
            startActivity(new Intent(mContext, TestListActivity.class));
            break;
        case R.id.seepic:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_SEE_PIC_EVENT_ID, StatServiceEnv.MAIN_SEE_PIC_LABEL, 1);
            startActivity(new Intent(mContext, SeePicActivity.class));
            break;
        case R.id.guide:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_GUIDE_EVENT_ID, StatServiceEnv.MAIN_GUIDE_LABEL, 1);
            startActivity(new Intent(mContext, GuideListActivity.class));
            break;
        case R.id.nutrition:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_NUTRITION_EVENT_ID, StatServiceEnv.MAIN_NUTRITION_LABEL,
                    1);
            startActivity(new Intent(mContext, NutritionFragmentTabNavigation.class));
            break;
        case R.id.yuerzhinan:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_YUERZHINAN_EVENT_ID,
                    StatServiceEnv.MAIN_YUERZHINAN_LATER_LABEL, 1);
            startActivity(new Intent(mContext, YuErZhiNanListActivity.class));
            break;
        case R.id.yuerbaike:
            StatService.onEvent(mContext, StatServiceEnv.MAIN_YUERBAIKE_LATER_EVENT_ID,
                    StatServiceEnv.MAIN_YUERBAIKE_LATER_LABEL, 1);
            startActivity(new Intent(mContext, BaikeListActivity.class));
            break;

        default:
            break;
        }
    }
}
