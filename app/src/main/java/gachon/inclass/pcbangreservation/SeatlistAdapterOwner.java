package gachon.inclass.pcbangreservation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SeatlistAdapterOwner extends RecyclerView.Adapter<SeatlistAdapterOwner.SeatListViewHolder>{

    Context mContext;
    ArrayList<ListViewItemOwner> dataList;
    private String PCbangAddress;

    SeatlistAdapterOwner(Context c, ArrayList<ListViewItemOwner> list, String address) {
        mContext = c;
        dataList = list;
        PCbangAddress = address;
    }

    @NonNull
    @Override
    public SeatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listviewitem_owner, parent, false);

        SeatListViewHolder cvh =new  SeatListViewHolder(view,mContext);
        return cvh;
    }
    @Override
    public void onBindViewHolder(@NonNull SeatListViewHolder holder, int position) {
        holder.onBind((String)dataList.get(position).getSeat_num(),(String)dataList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }

    class SeatListViewHolder extends RecyclerView.ViewHolder{
        public TextView num;
        public TextView time;
        Context ctx;

        public SeatListViewHolder(@NonNull View itemView, Context c) {
            super(itemView);
            ctx = c;
            num = itemView.findViewById(R.id.seat_num);
            time = itemView.findViewById(R.id.time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(ctx, OwnerSetSeat.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("SEATNUM", num.getText().toString());
                        intent.putExtra("SEATTIME",time.getText().toString());
                        intent.putExtra("ADDRESS", PCbangAddress);
                        Toast.makeText(c,time.getText().toString(),Toast.LENGTH_SHORT).show();
                        ctx.startActivity(intent);
                    }
                }
            });
        }

        public void onBind(String numText, String timeTxt) {
            num.setText(numText);
            time.setText(timeTxt);
        }


    }
}
