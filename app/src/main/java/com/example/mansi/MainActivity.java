package com.example.mansi;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mansi.Adapters.QuoteListAdapter;
import com.example.mansi.Constant.QuoteDBContract;
import com.example.mansi.Entities.AuthorEntity;
import com.example.mansi.Entities.CategoryEntity;
import com.example.mansi.Entities.QuoteEntity;
import com.example.mansi.SqlDBHelper.DatabaseHandler;
import com.example.mansi.Utils.NotificationUtils;
import com.example.mansi.Utils.QuotesAsyncLoader;
import com.example.mansi.Widgets.WidgetService;
import com.example.mansi.data.DataBaseHelper;
import com.example.mansi.data.RequestProvider;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.GridLayout.HORIZONTAL;

public class MainActivity extends AppCompatActivity implements QuoteListAdapter.SelectedQuote, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int QUOTE_LOADER = 100;
    private static final int AUTHOR_LOADER = 101;
    private static final int CATEGORY_LOADER = 102;
    private int _selectedAuthorId;
    private int _selectedCategoryId;
    private int _selectedSortOrder;
    SearchView searchView;
    MenuItem searchMenuItem;
    QuoteListAdapter adapter;
    Context context;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    @BindView(R.id.recylerview_all_quotes)
    RecyclerView recylerview_all_quotes;
    HashMap<Integer, QuoteEntity> quoteEntityHashMap;
    ArrayList<QuoteEntity> contentQuoteList;

    Cursor quoteCursor;
    Cursor authorCursor;
    Cursor categoryCursor;

/*
    HashMap<Integer, AuthorEntity> authorEntityMap;
    HashMap<Integer, CategoryEntity> categoryEntityMap;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        quoteEntityHashMap = new HashMap<>();

        contentQuoteList = new ArrayList<>();

        initData();
        MobileAds.initialize(this, getString(R.string.banner_home_footer));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(context, context.getString(R.string.adclose), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(context, context.getString(R.string.onAdFailedToLoad), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(context, context.getString(R.string.onAdLeftApplication), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

        });


        initWidget();

        txtRegId = findViewById(R.id.txt_reg_id);
        txtMessage = findViewById(R.id.txt_push_message);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(context, context.getString(R.string.pushnotification), Toast.LENGTH_LONG).show();
                    txtMessage.setText(message);
                }
            }
        };

        displayFirebaseRegId();
//        initLoader();


    }

    private void initData() {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
    }

    private void initWidget() {
        DatabaseHandler db = new DatabaseHandler(this);
        final ArrayList<CategoryEntity> categoryList = db.GetAllCategories();
        db.close();

        Log.d("Categoriessssss", "" + categoryList.size());
        WidgetService.updateWidget(MainActivity.this, categoryList);

    }

    private void initLoader() {
        getSupportLoaderManager().initLoader(QUOTE_LOADER, null, this);
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    @Override
    public void onClick(ArrayList<QuoteEntity> quoteEntities, int position) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(MainActivity.this, DetailScreenActivity.class);
        bundle.putInt("index", position);
        bundle.putParcelableArrayList("quotes", quoteEntities);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new QuotesAsyncLoader(getBaseContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.e("DATA_COUNT", String.valueOf(cursor.getCount()));
        initRecyclerView(getQuoteListFromCursor(cursor));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private ArrayList<QuoteEntity> getQuoteListFromCursor(Cursor cursor) {
        ArrayList<QuoteEntity> quoteList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                QuoteEntity quote = new QuoteEntity();
                quote.setQuoteId(cursor.getInt(0));
                quote.setQuoteText(cursor.getString(1));
                quote.setCategoryId(cursor.getInt(2));
                quote.setAuthorName(cursor.getString(4));
                quote.setCategoryName(cursor.getString(5));
                quote.setAuthorId(cursor.getInt(6));
                quoteList.add(quote);
            } while (cursor.moveToNext());
        }

        return quoteList;
    }

    private class AsyncTaskRunner extends AsyncTask<Void, Void, Boolean> {

        //  ProgressDialog progressDialog;

/*
        @Override
        protected ArrayList<QuoteEntity> doInBackground(ArrayList<QuoteEntity>... arrayLists) {
            SharedPreferences userSelection = getSharedPreferences("UserSelection", Context.MODE_PRIVATE);
            _selectedAuthorId = userSelection.getInt("selectedAuthorId", 1);
            _selectedCategoryId = userSelection.getInt("selectedCategoryId", 1);
            _selectedSortOrder = userSelection.getInt("selectedSortOrder", 1);

            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            String _queryString = "";
            quoteList = db.GetFilteredQuotes(_selectedAuthorId, _selectedCategoryId, _selectedSortOrder, _queryString);
//            loadAuthorDate(db);
//            loadCategoryDate(db);
            db.close();

            return quoteList;
        }
*/

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String destinationPath = "/data/data/" + getPackageName() + "/databases";
                File file = new File(destinationPath);

                if (!file.exists()) {
                    Log.e("exist", "exist");
                    file.mkdirs();
                    file.createNewFile();

                    CopyDB(getBaseContext().getAssets().open("quotedb"),
                            new FileOutputStream(destinationPath + "/quotedb"));
                } else {

                    CopyDB(getBaseContext().getAssets().open("quotedb"),
                            new FileOutputStream(destinationPath + "/quotedb"));
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) initLoader();
        }

/*     @Override
        protected void onPostExecute(ArrayList<QuoteEntity> quoteEntities) {
            super.onPostExecute(quoteEntities);
            insertQuote(quoteEntities);
            initLoader();
        }*/

        /*       private void loadAuthorDate(DatabaseHandler databaseHandler) {
                   for (AuthorEntity entity : databaseHandler.GetAllAuthors()) {
                       authorEntityMap.put(entity.getAuthorId(), entity);
                   }

               }

               private void loadCategoryDate(DatabaseHandler databaseHandler) {
                   for (CategoryEntity entity : databaseHandler.GetAllCategories()) {
                       categoryEntityMap.put(entity.getCategoryId(), entity);
                   }

               }
       */
        private void insertQuote(List<QuoteEntity> quoteList) {
            List<ContentValues> values = new ArrayList<>();
            for (QuoteEntity e : quoteList) {

                quoteEntityHashMap.put(e.getQuoteId(), e);

                ContentValues contentValues = new ContentValues();

                contentValues.put(DataBaseHelper.QuoteTable.COLUMN_QUOTE_ID, e.getQuoteId());
                contentValues.put(DataBaseHelper.QuoteTable.COLUMN_QUOTE_TEXT, e.getQuoteText());
                contentValues.put(DataBaseHelper.QuoteTable.COLUMN_AUTHOR_ID, e.getAuthorId());

                contentValues.put(DataBaseHelper.QuoteTable.COLUMN_CATEGORY_ID, e.getCategoryId());
                values.add(contentValues);
            }
            getContentResolver().bulkInsert(RequestProvider.BASE_CONTENT_URI, values.toArray(new ContentValues[0]));
        }

    }

    private void initRecyclerView(ArrayList<QuoteEntity> quoteEntities) {
        adapter = new QuoteListAdapter(this, quoteEntities);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recylerview_all_quotes.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(MainActivity.this, HORIZONTAL);
        recylerview_all_quotes.addItemDecoration(itemDecor);
        recylerview_all_quotes.setAdapter(adapter);
    }


    private void CopyDB(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSubmitButtonEnabled(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                Log.d("Queryyyy", query);
                adapter.getFilter().filter(query);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.mnuAuthor:
                showAuthorDialog();
                return true;
            case R.id.mnuCategory:
                showCategoryDialog();
                return true;

            case R.id.mnuSortOrder:
                showSortedQuotes(item);
                return true;
//            case R.id.mnuRandom:
//                showRandomQuotes();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showAuthorDialog() {

        DatabaseHandler db = new DatabaseHandler(this);
        final ArrayList<AuthorEntity> authorList = db.GetAllAuthors();
        db.close();

        final String[] authorArr = new String[authorList.size()];
        int counter = 0;
        for (AuthorEntity authorEntity : authorList) {
            authorArr[counter] = authorEntity.getAuthorName();
            counter++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Author");
        builder.setItems(authorArr, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), authorArr[which], Toast.LENGTH_LONG).show();
                _selectedAuthorId = authorList.get(which).getAuthorId();
                saveSelectedAuthorId();
                if (adapter != null)
                    adapter.filterAuthor(_selectedAuthorId);


            }

        });

        builder.show();

    }

    private void showCategoryDialog() {

        DatabaseHandler db = new DatabaseHandler(this);
        final ArrayList<CategoryEntity> categoryList = db.GetAllCategories();
        db.close();

        final String[] categoryArr = new String[categoryList.size()];
        int counter = 0;
        for (CategoryEntity categoryEntity : categoryList) {
            categoryArr[counter] = categoryEntity.getCategoryName();
            counter++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(categoryArr, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(getBaseContext(), categoryArr[which], Toast.LENGTH_LONG).show();
                _selectedCategoryId = categoryList.get(which).getCategoryId();
                saveSelectedCategoryId();
                if (adapter != null)
                    adapter.filterCat(_selectedCategoryId);
            }

        });

        builder.show();
    }

    private void showRandomQuotes() {
        Intent intent = new Intent(this, DetailScreenActivity.class);
        intent.putExtra("selectedQuoteIndex", 0);
        intent.putExtra("selectedSortOrder", 2);
        startActivity(intent);
    }

    private void showSortedQuotes(MenuItem item) {

        if (item.getTitle() == "Ascending Order") {
            _selectedSortOrder = 0;
            saveSelectedSortOrder();
            /*    AsyncTaskRunner runner = new AsyncTaskRunner();
             *//*  String sleepTime = time.getText().toString();*//*
            runner.execute();*/
            if (adapter != null)
                adapter.sort(true);
            setSortOrderMenu(item, 0);
        } else {
            _selectedSortOrder = 1;
            saveSelectedSortOrder();
            if (adapter != null)
                adapter.sort(false);
//            AsyncTaskRunner runner = new AsyncTaskRunner();
//            /*  String sleepTime = time.getText().toString();*/
//            runner.execute();
            setSortOrderMenu(item, 1);
        }

    }


    private void setSortOrderMenu(MenuItem item, int sortOrder) {
        if (sortOrder == 1) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp));
            item.setTitle("Ascending Order");
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp));
            item.setTitle("Descending Order");
        }
    }

    private void saveSelectedAuthorId() {
        SharedPreferences userSelection = getSharedPreferences("UserSelection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSelection.edit();
        editor.putInt("selectedAutihorId", _selectedAuthorId);
        editor.commit();
    }

    private void saveSelectedCategoryId() {
        SharedPreferences userSelection = getSharedPreferences("UserSelection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSelection.edit();
        editor.putInt("selectedCategoryId", _selectedCategoryId);
        editor.commit();
    }

    private void saveSelectedSortOrder() {
        SharedPreferences userSelection = getSharedPreferences("UserSelection", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSelection.edit();
        editor.putInt("selectedSortOrder", _selectedSortOrder);
        editor.commit();
    }




}
