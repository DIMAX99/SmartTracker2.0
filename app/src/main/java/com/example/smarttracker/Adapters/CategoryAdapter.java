package com.example.smarttracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttracker.MainActivity;
import com.example.smarttracker.Model.CategoryModel;
import com.example.smarttracker.R;
import com.example.smarttracker.Utils.DatabaseHandler;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryModel> categorylist;
    private DatabaseHandler db;
    private Context context;
    private MainActivity activity;

    public CategoryAdapter(DatabaseHandler db,MainActivity activity){
        this.db=db;
        this.activity=activity;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.open();
        final CategoryModel item = categorylist.get(position);
        holder.category.setText(item.getName());

    }

    @Override
    public int getItemCount() {
        return categorylist.size();
    }
    public Context getContext() {
        return activity;
    }
    public void setcategory(List<CategoryModel> categorylist){
        this.categorylist=categorylist;
        notifyDataSetChanged();
    }

    public void updateCategories() {
        categorylist.clear();
        categorylist.addAll(db.getAllCategories());
        notifyDataSetChanged();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView category;
        ViewHolder(View view){
            super(view);
            category=view.findViewById(R.id.showcategory);
        }
    }
}
