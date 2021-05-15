package gachon.inclass.pcbangreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ShowSeat  extends AppCompatActivity {

    Button btnGoBack;
    TextView nameText;
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

        nameText.setText(name);


        ref = database.getReference("PC bangs").child(name);
        seatsNumber = new ArrayList<>();


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
