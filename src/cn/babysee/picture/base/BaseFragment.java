package cn.babysee.picture.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;

public class BaseFragment extends Fragment {

    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }
}
