package gachon.inclass.pcbangreservation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowSeat  extends AppCompatActivity {

    Button btnGoBack;
    TextView nameText;
    ImageView imageview;
    SeatlistAdapter adapter;
    ArrayList<String> seatsNumber;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showseat);

        Intent intent = getIntent();
        String name = intent.getStringExtra("PCbangName");
        String address = intent.getStringExtra("address");

        Log.v("test","Get intent from profile Activity: address of PC bang is "+address +", name of PC bang is "+ name);

        nameText = (TextView)findViewById(R.id.chosenPCbangName);
        nameText.setText(name);
        imageview = (ImageView)findViewById(R.id.imageView);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

        seatsNumber = new ArrayList<>();
        ref = database.getReference("PC bangs").child(address).child("seat");

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

        adapter = new SeatlistAdapter(getApplicationContext(),seatsNumber,address);
        rcView.setAdapter(adapter);


        btnGoBack = (Button)findViewById(R.id.goBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        Log.d("으아",name+"_seats.jpg");
        StorageReference storageRef = storage.getReference().child(name+"_seats.jpg");
        storageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                            Log.d("uri", String.valueOf(uri));
                            Glide.with(getApplicationContext()).load(uri).into(imageview);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }
}
