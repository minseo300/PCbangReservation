package gachon.inclass.pcbangreservation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PCbangListAdapter  extends RecyclerView.Adapter<PCbangListAdapter.PcbangListViewHolder>{

    Context mContext;
    ArrayList<String> dataList;

    PCbangListAdapter(Context c, ArrayList<String> list) {
        mContext = c;
        dataList = list;
    }

    @NonNull
    @Override
    public PcbangListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listviewitem, parent, false);

        PcbangListViewHolder cvh =new  PcbangListViewHolder(view,mContext);
        return cvh;
    }
    @Override
    public void onBindViewHolder(@NonNull PcbangListViewHolder holder, int position) {
        holder.onBind((String)dataList.get(position));
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }


    class PcbangListViewHolder extends RecyclerView.ViewHolder{
        public TextView txt_name;
        Context ctx;

        public PcbangListViewHolder(@NonNull View itemView, Context c) {
            super(itemView);
            ctx = c;
            txt_name =itemView.findViewById(R.id.items);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(ctx, ShowSeat.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("PCbangName", txt_name.getText().toString());
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
