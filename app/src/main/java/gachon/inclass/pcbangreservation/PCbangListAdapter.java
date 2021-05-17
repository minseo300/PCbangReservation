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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PCbangListAdapter  extends RecyclerView.Adapter<PCbangListAdapter.PcbangListViewHolder>{

    Context mContext;
    ArrayList<ListViewItem> dataList;

    PCbangListAdapter(Context c, ArrayList<ListViewItem> list) {
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
        holder.onBind((String)dataList.get(position).getStore_name(),(String)dataList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        if (dataList != null)
            return dataList.size();
        else
            return 0;
    }


    class PcbangListViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView address;
        Context ctx;

        public PcbangListViewHolder(@NonNull View itemView, Context c) {
            super(itemView);
            ctx = c;
            name =itemView.findViewById(R.id.store_list_name);
            address=itemView.findViewById(R.id.address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(ctx,ShowSeat.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("PCbangName", name.getText().toString());

                        ctx.startActivity(intent);
                    }
                }
            });
        }

        public void onBind(String dataTxt,String addressTxt) {
            name.setText(dataTxt);
            address.setText(addressTxt);
        }


    }
}
