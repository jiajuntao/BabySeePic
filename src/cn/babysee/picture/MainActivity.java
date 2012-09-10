package cn.babysee.picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends Activity implements OnClickListener {
    
    private View title;
    private View view1;
    private View view2;
    private View view3;
    private View view4;
    
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = getApplicationContext();
        
        title = findViewById(R.id.title);
        title.setOnClickListener(this);
        view1 = findViewById(R.id.item_1);
        view1.setOnClickListener(this);
        view2 = findViewById(R.id.item_2);
        view2.setOnClickListener(this);
        view3 = findViewById(R.id.item_3);
        view3.setOnClickListener(this);
        view4 = findViewById(R.id.item_4);
        view4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title:
//                startActivity(new Intent(context, WebViewActivity.class));
                break;
            case R.id.item_1:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 0));
                break;
            case R.id.item_2:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 1));
                break;
            case R.id.item_3:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 2));
                break;
            case R.id.item_4:
                startActivity(new Intent(context, ViewPicActivity.class).putExtra("item", 3));
                break;

            default:
                break;
        }
    }
}
