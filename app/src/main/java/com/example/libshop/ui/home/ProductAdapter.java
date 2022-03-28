package com.example.libshop.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libshop.Model.ProductModel;
import com.example.libshop.R;
import com.example.libshop.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductAdapter_Holder> {
    private Context mContext;
    private List<ProductModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    
    public ProductAdapter(Context mContext, List<ProductModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public ProductAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_product_item,parent,false); //connecting to cardview
        return new ProductAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getProductPhotoUrl();
        Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mL4ItemImageView);
        String dsTitle = mData.get(position).getProductName();
        //String dsBio = mData.get(position).getProductBio();
        int diViews = 1234;
        String dsBio = mData.get(position).getProductBio();
        holder.mL4ItemTitleText.setText(dsTitle);
        holder.mL4ItemBioText.setText(dsBio);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class ProductAdapter_Holder extends RecyclerView.ViewHolder {

        ImageView mL4ItemImageView;
        TextView mL4ItemTitleText;
        TextView mL4ItemBioText;


        public ProductAdapter_Holder(@NonNull View itemView) {
            super(itemView);

            mL4ItemImageView = (ImageView) itemView.findViewById(R.id.card_category_imagecard);
            mL4ItemTitleText = (TextView)itemView.findViewById(R.id.card_category_title);
            mL4ItemBioText = (TextView)itemView.findViewById(R.id.card_category_bio);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface .onProductItemClick(getAdapterPosition());
                }
            });

        }
    }



}
