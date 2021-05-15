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
import java.util.ArrayList;

public class ShowSeat  extends AppCompatActivity {

    Button btnGoBack;
    TextView nameText,totalSeat;
    PCbangListAdapter adapter;
    ArrayList<String> seatsNumber;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showseat);

        Intent intent = getIntent();
        String name = intent.getStringExtra("PCbangName");

        nameText = (TextView)findViewById(R.id.chosenPCbangName);
        totalSeat = (TextView)findViewById(R.id.totalSeat);
        nameText.setText(name);

        ref = database.getReference("PC bangs").child(name).child("seats");
        seatsNumber = new ArrayList<>();

        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Toast.makeText(getApplicationContext(),task.getResult().getValue().toString(),Toast.LENGTH_SHORT).show();
                String s = task.getResult().getValue().toString();
                totalSeat.setText(s);
            }
        });

        String s = totalSeat.getText().toString();
        int total = Integer.parseInt(s);

        for(int i =1; i < total + 1; i++){
            ref = database.getReference("PC bangs").child(name).child("seat").child(Integer.toString(i));
            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    String s = task.getResult().getValue().toString();
                    Log.v("names",s);
                }
            });
        }

        RecyclerView rcView = findViewById(R.id.SeatrcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PCbangListAdapter(getApplicationContext(),seatsNumber);
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
