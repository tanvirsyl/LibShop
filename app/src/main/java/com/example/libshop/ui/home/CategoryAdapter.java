package com.example.libshop.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libshop.Model.CategoryModel;
import com.example.libshop.R;
import com.example.libshop.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapter_Holder> {
    private Context mContext;
    private List<CategoryModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public CategoryAdapter(Context mContext, List<CategoryModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public CategoryAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_category_item,parent,false); //connecting to cardview
        return new CategoryAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getCategoryPhotoUrl();
        Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mL4ItemImageView);
        String dsTitle = mData.get(position).getCategoryName();
        int diViews = 1234;
        String dsBio = mData.get(position).getCategoryBio();
        holder.mL4ItemTitleText.setText(dsTitle);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class CategoryAdapter_Holder extends RecyclerView.ViewHolder {

        ImageView mL4ItemImageView;
        TextView mL4ItemTitleText;


        public CategoryAdapter_Holder(@NonNull View itemView) {
            super(itemView);

            mL4ItemImageView = (ImageView) itemView.findViewById(R.id.card_category_imagecard);
            mL4ItemTitleText = (TextView)itemView.findViewById(R.id.card_category_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    recylerviewClickInterface.onItemLongClick(getAdapterPosition());
                    return false;
                }
            });
        }
    }



}
