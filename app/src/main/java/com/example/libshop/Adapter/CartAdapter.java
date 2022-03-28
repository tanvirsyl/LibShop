package com.example.libshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libshop.Model.CartModel;
import com.example.libshop.R;
import com.example.libshop.RecylerviewClickInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartAdapter_Holder> {
    private Context mContext;
    private List<CartModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;
    public CartAdapter(Context mContext, List<CartModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public CartAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_pb_cart_items,parent,false); //connecting to cardview
        return new CartAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter_Holder holder, int position) {
        String dPhotoURL = mData.get(position).getProductPHOTO();
        Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mL4ItemImageView);
        String dsTitle = mData.get(position).getProductName();
        long dlPrice = mData.get(position).getProductiPrice();
        long dlQuantity = mData.get(position).getProductQuantity();
        int diViews = 1234;
        String dsBio = mData.get(position).getProductName();
        holder.mL4ItemTitleText.setText(dsTitle);
        holder.mL4ItemProductQuantity.setText(String.valueOf(dlQuantity)+" X "+String.valueOf(dlPrice));
        holder.mL4ItemProductTotalPrice.setText(String.valueOf(dlQuantity*dlPrice)+"TK ");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class CartAdapter_Holder extends RecyclerView.ViewHolder {

        ImageView mL4ItemImageView;
        TextView mL4ItemTitleText, mL4ItemProductQuantity ,mL4ItemProductTotalPrice;


        public CartAdapter_Holder(@NonNull View itemView) {
            super(itemView);

            mL4ItemImageView = (ImageView) itemView.findViewById(R.id.pb_cart_tem_img);
            mL4ItemTitleText = (TextView)itemView.findViewById(R.id.pb_cart_product_name);
            mL4ItemProductQuantity = (TextView)itemView.findViewById(R.id.pb_cart_product_price);
            mL4ItemProductTotalPrice = (TextView)itemView.findViewById(R.id.pb_cart_product_total_price);
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
