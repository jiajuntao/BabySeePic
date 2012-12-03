package cn.babysee.picture.tools;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.babysee.base.BaseActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.UIUtils;

public class BabyHeightActivity extends BaseActivity implements OnClickListener {

    private boolean DEBUG = AppEnv.DEBUG;
    private String TAG = "BabyHeightActivity";

    private TextView mFatherHeight;
    private TextView mMatherHeight;
    private TextView mBabyHeight;
    private RadioGroup mRadioGroup;
    private View mCalHeight;
    private int mBabyGenler = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools_height);

        mFatherHeight = (TextView) findViewById(R.id.father_height);
        mMatherHeight = (TextView) findViewById(R.id.mather_height);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                case R.id.boy:
                    mBabyGenler = 1;
                    break;
                case R.id.gril:
                    mBabyGenler = 0;
                    break;

                default:
                    break;
                }
            }
        });
        mBabyHeight = (TextView) findViewById(R.id.baby_heigth);
        mCalHeight = findViewById(R.id.cal_height);
        mCalHeight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //男孩未来身高(厘米)=(父亲身高+母亲身高)×1.078÷2 女孩未来身高(厘米)=(父亲身高×0.923+母亲身高)÷2 
                CharSequence fatherHeight = mFatherHeight.getText();
                if (TextUtils.isEmpty(fatherHeight)) {
                    UIUtils.showToast(mContext, "爸爸的身高还没有输入呦！", Toast.LENGTH_SHORT);
                    return;
                }

                CharSequence matherHeight = mMatherHeight.getText();
                if (TextUtils.isEmpty(matherHeight)) {
                    UIUtils.showToast(mContext, "妈妈的身高还没有输入呦！", Toast.LENGTH_SHORT);
                    return;
                }

                if (mBabyGenler == -1) {
                    UIUtils.showToast(mContext, "还没有选择宝宝的性别呦！", Toast.LENGTH_SHORT);
                    return;
                }

                int babyHeightInt = 0;

                float matherHeightFloat = Float.valueOf(matherHeight.toString());
                float fatherHeightFloat = Float.valueOf(fatherHeight.toString());

                if (mBabyGenler == 0) {
                    babyHeightInt = (int) ((fatherHeightFloat * 0.923 + matherHeightFloat) / 2);
                }

                if (mBabyGenler == 1) {
                    babyHeightInt = (int) ((fatherHeightFloat + matherHeightFloat) * 1.078 / 2);
                }
                mBabyHeight.setVisibility(View.VISIBLE);
                mBabyHeight.setText("您宝宝未来的身高可能为：" + babyHeightInt + "厘米");
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}
