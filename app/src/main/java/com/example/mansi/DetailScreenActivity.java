package com.example.mansi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mansi.Adapters.ViewPagerAdapter;
import com.example.mansi.Entities.QuoteEntity;
import com.example.mansi.SqlDBHelper.DatabaseHandler;

import java.util.ArrayList;

public class DetailScreenActivity extends AppCompatActivity {

    ArrayList<QuoteEntity> _quoteList = new ArrayList<QuoteEntity>();
    public static int SelectedQuoteIndex;

    private int _selectedAuthorId;
    private int _selectedCategoryId;
    private int _selectedSortOrder;
    private int _selectedSortOrderFromMainActivity;

    private ViewPager viewPager;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SelectedQuoteIndex = extras.getInt("index");
            _quoteList = extras.getParcelableArrayList("quotes");
        }
       setViewPager();
    }

    private ArrayList<QuoteEntity> getQuoteList() {

        SharedPreferences userSelection = getSharedPreferences("UserSelection", Context.MODE_PRIVATE);
        _selectedAuthorId = userSelection.getInt("selectedAuthorId", 1);
        _selectedCategoryId = userSelection.getInt("selectedCategoryId", 1);
        if (_selectedSortOrderFromMainActivity != 2) {
            _selectedSortOrderFromMainActivity = userSelection.getInt("selectedSortOrder", 1);
        }

        DatabaseHandler db = new DatabaseHandler(this);
        ArrayList<QuoteEntity> quoteList = db.GetFilteredQuotes(_selectedAuthorId, _selectedCategoryId, _selectedSortOrderFromMainActivity, "");
        db.close();

        return quoteList;
    }

    private void setViewPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ViewPagerAdapter(this, _quoteList);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(SelectedQuoteIndex);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                SelectedQuoteIndex = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("selectedQuoteIndex", SelectedQuoteIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

}
