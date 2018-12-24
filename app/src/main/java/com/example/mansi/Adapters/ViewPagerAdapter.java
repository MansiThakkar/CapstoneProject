package com.example.mansi.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


import com.example.mansi.DetailScreenActivity;
import com.example.mansi.Entities.QuoteEntity;

import com.example.mansi.R;
import com.example.mansi.SqlDBHelper.DatabaseHandler;


public class ViewPagerAdapter extends PagerAdapter {

    private Context _context;
    private ArrayList<QuoteEntity> _quoteList;
    QuoteEntity selectedQuote;
    LayoutInflater inflater;
    Context context;

    public ViewPagerAdapter(Context context, ArrayList<QuoteEntity> quoteList)
    {
        _context = context;
        _quoteList = quoteList;
    }

    @Override
    public int getCount() {
        return _quoteList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == ((View) o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.fragment_single_quote, container, false);



        selectedQuote = _quoteList.get(position);

        final TextView tvQuoteText = (TextView) itemView.findViewById(R.id.quote_text);
        final TextView tvAuthorName = (TextView) itemView.findViewById(R.id.author_name);
        final TextView tvCategory = (TextView) itemView.findViewById(R.id.category);

        tvQuoteText.setText("\"" + selectedQuote.getQuoteText() + "\"");
        tvAuthorName.setText("- " + selectedQuote.getAuthorName());
        tvCategory.setText("Category: " + selectedQuote.getCategoryName());



        ImageView imgShare = (ImageView) itemView.findViewById(R.id.imgShare);
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedQuote = _quoteList.get(DetailScreenActivity.SelectedQuoteIndex);
                String shareContent = selectedQuote.getQuoteText() + "\n" + "- " + selectedQuote.getAuthorName();

                shareIt(shareContent);
            }
        });

        ImageView imgCopy = (ImageView) itemView.findViewById(R.id.imgCopy);
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedQuote = _quoteList.get(DetailScreenActivity.SelectedQuoteIndex);
                ClipboardManager clipboard = (ClipboardManager) _context.getSystemService(_context.CLIPBOARD_SERVICE);
                String content = selectedQuote.getQuoteText() + "\n" + "- " + selectedQuote.getAuthorName();
                ClipData clip = ClipData.newPlainText("Quote", content);
                clipboard.setPrimaryClip(clip);

                //Toast.makeText(_context,"Copied to Clipboard",Toast.LENGTH_LONG).show();
                Toast.makeText(context, context.getString(R.string.CopiedtoClipboard), Toast.LENGTH_LONG).show();

            }
        });


        container.addView(itemView);


        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void shareIt(String shareContent)
    {

        // get available share intents
        List<Intent> targets = new ArrayList<Intent>();
        Intent template = new Intent(Intent.ACTION_SEND);
        template.setType("text/plain");
        List<ResolveInfo> candidates = _context.getPackageManager().queryIntentActivities(template, 0);

        // remove facebook which has a broken share intent
        for (ResolveInfo candidate : candidates) {
            String packageName = candidate.activityInfo.packageName;
            if (!packageName.equals("com.facebook.katana")) {
                Intent target = new Intent(Intent.ACTION_SEND);
                target.setType("text/plain");
                target.putExtra(Intent.EXTRA_TEXT, shareContent);
                target.setPackage(packageName);
                targets.add(target);
            }
        }
        Intent chooser = Intent.createChooser(targets.remove(0), "Share Via");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.toArray(new Parcelable[]{}));
        _context.startActivity(chooser);

    }

    public Bitmap screenShot(View view) {

        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "image001.jpg";

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth()*2,
                view.getHeight()*2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setBitmap(bitmap);
        canvas.scale(2.0f,2.0f);
        view.draw(canvas);

        OutputStream fout = null;
        File imageFile = new File(mPath);

        try {
            fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return bitmap;
    }


}
