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

public class SeatlistAdapter   extends RecyclerView.Adapter<SeatlistAdapter.SeatListViewHolder>{

    Context mContext;
    ArrayList<String> dataList;
    String name;

    SeatlistAdapter(Context c, ArrayList<String> list, String names) {
        mContext = c;
        dataList = list;
        name = names;
    }

    @NonNull
    @Override
    public SeatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listviewitemseat, parent, false);

        SeatListViewHolder cvh =new  SeatListViewHolder(view,mContext);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SeatListViewHolder holder, int position) {
        holder.onBind((String)dataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }

    class SeatListViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_name;
        Context ctx;

        public SeatListViewHolder(@NonNull View itemView, Context c) {
            super(itemView);
            ctx = c;
            txt_name =itemView.findViewById(R.id.store_list_name); // 보류2: items로 되어있었는데 오류나서 일단 store_list_name으로 놓음

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(ctx, Reservation.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("seats", txt_name.getText().toString());
                        intent.putExtra("address",name);
                        ctx.startActivity(intent);
                    }
                }
            });
        }

        public void onBind(String dataTxt) {
            txt_name.setText(dataTxt);
        }

    }
}
