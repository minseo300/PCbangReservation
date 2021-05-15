package gachon.inclass.pcbangreservation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowSeat  extends AppCompatActivity {

    Button btnGoBack;
    TextView nameText;
    SeatlistAdapter adapter;
    ArrayList<String> seatsNumber;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showseat);

        Intent intent = getIntent();
        String name = intent.getStringExtra("PCbangName");

        nameText = (TextView)findViewById(R.id.chosenPCbangName);
        nameText.setText(name);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

        seatsNumber = new ArrayList<>();
        ref = database.getReference("PC bangs").child(name).child("seat");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("time")&&snapshot.hasChild("number")){
                    String number =snapshot.child("number").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();
                    if(time.equals("0")) {
                        Log.v("names", "Seat number " +number + " is available");
                        seatsNumber.add("Seat number " +number + " is available");
                    }
                    else{
                        if(time.compareTo(strNow) < 0) {
                            ref.child(number).child("time").setValue("0");
                            seatsNumber.add("Seat number " +number + " is available");
                        }
                        else
                            seatsNumber.add("Seat number " +number + " is not available");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("time")&&snapshot.hasChild("number")){
                    String number =snapshot.child("number").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();
                    if(time.equals("0")) {
                        seatsNumber.set(Integer.parseInt(number) - 1, "Seat number " + number + " is available");
                    }
                    else
                        seatsNumber.set(Integer.parseInt(number) - 1, "Seat number " + number + " is not available");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("time")&&snapshot.hasChild("number")){
                    String number =snapshot.child("number").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();
                    if(time.equals("0")) {
                        seatsNumber.set(Integer.parseInt(number) - 1, "Seat number " + number + " is available");
                    }
                    else
                        seatsNumber.set(Integer.parseInt(number) - 1, "Seat number " + number + " is not available");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapter.notifyDataSetChanged();
            }
        });

        RecyclerView rcView = findViewById(R.id.SeatrcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SeatlistAdapter(getApplicationContext(),seatsNumber,name);
        rcView.setAdapter(adapter);


        btnGoBack = (Button)findViewById(R.id.goBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
