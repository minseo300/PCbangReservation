package gachon.inclass.pcbangreservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SeatlistAdapterOwner extends RecyclerView.Adapter<SeatlistAdapterOwner.SeatListViewHolder>{

    Context mContext;
    ArrayList<ListViewItemOwner> dataList;

    SeatlistAdapterOwner(Context c, ArrayList<ListViewItemOwner> list) {
        mContext = c;
        dataList = list;
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
            // 보류2: 여기까지 될지 안될지 모르겠어서 시간수정기능 아직 추가안함
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        Intent intent = new Intent(ctx, Reservation.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.putExtra("seats", txt_name.getText().toString());
//                        intent.putExtra("name",name);
//                        ctx.startActivity(intent);
//                    }
//                }
//            });
        }

        public void onBind(String numText, String timeTxt) {
            num.setText(numText);
            time.setText(timeTxt);
        }


    }
}
