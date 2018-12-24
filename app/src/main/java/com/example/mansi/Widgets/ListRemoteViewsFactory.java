/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mansi.Widgets;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.mansi.Entities.CategoryEntity;
import com.example.mansi.R;
import com.example.mansi.Widgets.SharedPrefUtil;

import java.util.ArrayList;


public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<CategoryEntity> category;


    public ListRemoteViewsFactory(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.d("lalaslssss",""+category);

        category = SharedPrefUtil.loadCategories(mContext);
        Log.d("lalaslssss",""+category);

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return category.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(mContext.getPackageName(), R.layout.category_list_item);
        Log.d("lalaslssss",""+category.get(position).getCategoryName());

        row.setTextViewText(R.id.category_item_text, category.get(position).getCategoryName());
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
