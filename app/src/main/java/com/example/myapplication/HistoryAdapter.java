package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
  private ArrayList<HistoryItem> listItems;
  private Context context;

  public HistoryAdapter(ArrayList<HistoryItem> listItems, Context context) {
    this.listItems = listItems;
    this.context = context;
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    View inflate = inflater.inflate(R.layout.history_item, viewGroup, false);
    return new Holder(inflate);
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int i) {
    holder.date_title.setText(listItems.get(i).getTime());
    holder.txt_time.setText(listItems.get(i).getMoved());
    holder.txt_distance.setText(listItems.get(i).getDistance());
  }

  @Override
  public int getItemCount() {
    return listItems.size();
  }


  public class Holder extends RecyclerView.ViewHolder {
    TextView date_title, txt_distance, txt_time;
    Button btn_details;
    public Holder(@NonNull View itemView) {
      super(itemView);
      date_title = itemView.findViewById(R.id.date_title);
      txt_distance = itemView.findViewById(R.id.txt_distance);
      txt_time = itemView.findViewById(R.id.txt_time);
      btn_details = itemView.findViewById(R.id.chitiet);
      
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(context, RouteDetails.class);
          intent.putExtra("sendFromAdapter", date_title.getText());
          context.startActivity(intent);
        }
      });
    }
  }
}
