package com.cialc.recycler;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cialc.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter <Adapter.ViewHolderItems> {

    private Context context;
    private ArrayList<Dispositivo> listDispositivos;
    private ListClickItem listClickItem;

    public Adapter(Context context, ArrayList<Dispositivo> listDispositivos, ListClickItem listClickItem) {
        this.context = context;
        this.listDispositivos = listDispositivos;
        this.listClickItem = listClickItem;
    }

    @NonNull
    @Override
    public ViewHolderItems onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item,parent,false);
        return new ViewHolderItems(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItems holder, int position) {
        holder.hostname.setText(listDispositivos.get(position).getHostname());
        holder.image.setImageBitmap(listDispositivos.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return listDispositivos.size();
    }

    public class ViewHolderItems extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout item = (LinearLayout) itemView.findViewById(R.id.item_layout);
        TextView hostname = (TextView) itemView.findViewById(R.id.txtHostname);
        ImageView image = (ImageView) itemView.findViewById(R.id.imgItem);

        public ViewHolderItems(@NonNull View itemView) {
            super(itemView);
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickItem = getAdapterPosition();
            Dispositivo itemSelect = new Dispositivo(listDispositivos.get(clickItem).getHostname(),
                                                    listDispositivos.get(clickItem).getIpAddress(),
                                                    listDispositivos.get(clickItem).getImage());

            listClickItem.onListItemClick(v.getId(),clickItem,itemSelect);

        }
    }

    public interface ListClickItem{
        void onListItemClick(int id, int clickItem, Dispositivo device);
    }
}
