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
package cn.babysee.picture.book;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import cn.babysee.base.BaseActivity;
import cn.babysee.picture.R;
import cn.babysee.picture.env.AppEnv;

/**
 * 中国儿童智力方程
 */
public class ZhiLiFangChengContentActivity extends BaseActivity {

    private static final String TAG = "BookListActivity";

    private boolean DEBUG = AppEnv.DEBUG;

    private Context mContext;

    private IBookHelper mHelper;

    private TextView mContntView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_content_view);
        mContext = getApplicationContext();

        mContntView = (TextView) findViewById(R.id.content);

        mHelper = new ZhiLiFangChengHelper(mContext);

        String filePath = getIntent().getStringExtra("filePath");
        mContntView.setText(mHelper.getChapterSubContent(filePath));
    }
}