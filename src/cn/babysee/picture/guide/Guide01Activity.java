/*
 * Copyright 2012 YiXuanStudio Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.babysee.picture.guide;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import cn.babysee.base.BaseActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;
import cn.babysee.utils.FileUtils;

public class Guide01Activity extends BaseActivity {

    private static final String TAG = "BookListActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private TextView mContntView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_content_view);
        mContext = getApplicationContext();

        mContntView = (TextView) findViewById(R.id.content);

        String str = FileUtils.getAssetFile(mContext, "books/ren_zi_chu_result");
        
        String filePath = getIntent().getStringExtra("filePath");
        mContntView.setText(str);
    }
}