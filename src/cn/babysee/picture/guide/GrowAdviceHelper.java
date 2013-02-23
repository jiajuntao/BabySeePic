package cn.babysee.picture.guide;



import android.content.Context;

public class GrowAdviceHelper extends AdviceHelper {

    public GrowAdviceHelper(Context context) {
        super(context);
    }

    @Override
    void init() {
        filePath = "guide/grow_advice";
    }
}
