package com.example.mansi.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mansi.Entities.QuoteEntity;
import com.example.mansi.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuoteListAdapter extends RecyclerView.Adapter<QuoteListAdapter.MyViewHolder> implements Filterable {

    private List<QuoteEntity> quoteFiltered;
    private ArrayList<QuoteEntity> quoteEntities;

    public SelectedQuote selectedQuote;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuoteText;
        TextView tvAuthorName;
        TextView tvCategory;
        ImageView imgCopy;
        ImageView imgShare;
        ImageView imgAuthorImage;
        LinearLayout lin_quote;


        public MyViewHolder(View view) {
            super(view);
            tvQuoteText = (TextView) view.findViewById(R.id.quote_text);
            tvAuthorName = (TextView) view.findViewById(R.id.author_name);
            tvCategory = (TextView) view.findViewById(R.id.category);
            imgCopy = (ImageView) view.findViewById(R.id.imgCopy);
            //cbFavorite = (CheckBox) view.findViewById(R.id.cbFavorite);
            imgShare = (ImageView) view.findViewById(R.id.imgShare);
            imgAuthorImage = (ImageView) view.findViewById(R.id.author_image);
            lin_quote = (LinearLayout) view.findViewById(R.id.lin_quote);
        }
    }

    public QuoteListAdapter(SelectedQuote selectedQuote, ArrayList<QuoteEntity> quoteEntities) {
        this.quoteEntities = quoteEntities;
        this.quoteFiltered = (List<QuoteEntity>) quoteEntities.clone();
        this.selectedQuote = selectedQuote;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quote_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tvQuoteText.setText(quoteFiltered.get(position).getQuoteText());


        holder.tvAuthorName.setText(quoteFiltered.get(position).getAuthorName());

        holder.tvCategory.setText(quoteFiltered.get(position).getCategoryName());
        holder.lin_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedQuote.onClick((ArrayList<QuoteEntity>) quoteFiltered, position);
            }
        });




    }

    @Override
    public int getItemCount() {
        if (quoteFiltered == null)
            return 0;
        return quoteFiltered.size();
    }

    public interface SelectedQuote {
        public void onClick(ArrayList<QuoteEntity> quoteEntities, int position);
    }

    public void filterAuthor(int id) {
        quoteFiltered.clear();
        for (QuoteEntity row : quoteEntities) {
            if (row.getAuthorId() == id) {
                quoteFiltered.add(row);
            }
        }
        if (quoteFiltered.isEmpty())
            quoteFiltered.addAll(quoteEntities);
        notifyDataSetChanged();
    }

    public void filterCat(int id) {
        quoteFiltered.clear();
        for (QuoteEntity row : quoteEntities) {
            if (row.getCategoryId() == id) {
                quoteFiltered.add(row);
            }
        }
        if (quoteFiltered.isEmpty())
            quoteFiltered.addAll(quoteEntities);
        notifyDataSetChanged();
    }

    public void sort(final boolean isAsc) {
        Collections.sort(quoteFiltered, new Comparator<QuoteEntity>() {
            @Override
            public int compare(QuoteEntity quoteEntity, QuoteEntity t1) {
                return isAsc ? quoteEntity.getQuoteId() - t1.getQuoteId() : t1.getQuoteId() - quoteEntity.getQuoteId();
            }
        });
        Log.e("AAAAAAAAA", " " + quoteFiltered.get(0).getQuoteId());
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    quoteFiltered = quoteEntities;
                } else {
                    List<QuoteEntity> filteredList = new ArrayList<>();
                    for (QuoteEntity row : quoteEntities) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCategoryName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    quoteFiltered = filteredList;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = quoteFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                quoteFiltered = (ArrayList<QuoteEntity>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
