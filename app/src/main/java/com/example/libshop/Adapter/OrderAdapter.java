package com.example.libshop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libshop.Model.OrderModel;
import com.example.libshop.R;
import com.example.libshop.RecylerviewClickInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderAdapter  extends RecyclerView.Adapter<OrderAdapter.OrderAdapter_Holder> {
    private Context mContext;
    private List<OrderModel> mData;
    private RecylerviewClickInterface recylerviewClickInterface;

    public OrderAdapter(Context mContext, List<OrderModel> mData, RecylerviewClickInterface recylerviewClickInterface) {
        this.mContext = mContext;
        this.mData = mData;
        this.recylerviewClickInterface = recylerviewClickInterface;
    }

    @NonNull
    @Override
    public OrderAdapter_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_order_item, parent, false); //connecting to cardview
        return new OrderAdapter_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter_Holder holder, int position) {
        //String dPhotoURL = mData.get(position).getProductPhotoUrl();
        //Picasso.get().load(dPhotoURL).fit().centerCrop().into(holder.mL4ItemImageView);
        String dsTitle = mData.get(position).getOrder_UID();
        Date date =   mData.get(position).getDiTime();
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mma  dd/MMM/yy");
        String dateText = df2.format(date);
        holder.mL4ItemBioText.setText(dateText);

        int diViews = 1234;
        //String dsBio = mData.get(position).getProductBio();
        holder.mL4ItemTitleText.setText("Order ID: "+dsTitle.substring(0,6).toLowerCase());

        //holder.mL4ItemBioText.setText(dsBio);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class OrderAdapter_Holder extends RecyclerView.ViewHolder {

        ImageView mL4ItemImageView;
        TextView mL4ItemTitleText;
        TextView mL4ItemBioText;


        public OrderAdapter_Holder(@NonNull View itemView) {
            super(itemView);

            mL4ItemImageView = (ImageView) itemView.findViewById(R.id.card_order_imagecard);
            mL4ItemTitleText = (TextView) itemView.findViewById(R.id.card_order_title);
            mL4ItemBioText = (TextView) itemView.findViewById(R.id.card_order_bio);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recylerviewClickInterface.onItemClick(getAdapterPosition());
                }
            });

        }
    }


}
