package cn.babysee.picture.widget;

import android.app.AlertDialog;
import android.content.Context;

public class DialogFactory extends AlertDialog {

    protected DialogFactory(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public DialogFactory(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        // TODO Auto-generated constructor stub
    }

    public DialogFactory(Context context, int theme) {
        super(context, theme);
        // TODO Auto-generated constructor stub
    }
    
    

}
