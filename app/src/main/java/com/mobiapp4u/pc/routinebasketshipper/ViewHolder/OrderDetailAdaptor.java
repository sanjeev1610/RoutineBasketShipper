package com.mobiapp4u.pc.routinebasketshipper.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiapp4u.pc.routinebasketshipper.Model.Order;
import com.mobiapp4u.pc.routinebasketshipper.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView pro_name,pro_qua,pro_price,pro_dis;

    public MyViewHolder(View itemView) {
        super(itemView);
        pro_dis = (TextView)itemView.findViewById(R.id.pro_discount);
        pro_name = (TextView)itemView.findViewById(R.id.pro_name);
        pro_price = (TextView)itemView.findViewById(R.id.pro_price);
        pro_qua = (TextView)itemView.findViewById(R.id.pro_quantity);
    }
}

public class OrderDetailAdaptor extends RecyclerView.Adapter<MyViewHolder> {
    List<Order> listOrder;

    public OrderDetailAdaptor(List<Order> listOrder) {
        this.listOrder = listOrder;
    }

    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_order_detail,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = listOrder.get(position);
        holder.pro_qua.setText(String.format("Quantity : %s",order.getQuantity()));
        holder.pro_price.setText(String.format("Price : %s", order.getPrice()));
        holder.pro_name.setText(String.format("Name : %s",order.getProductName()));
        holder.pro_dis.setText(String.format("Discount : %s",order.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }
}
