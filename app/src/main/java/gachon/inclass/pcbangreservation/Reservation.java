package gachon.inclass.pcbangreservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation extends Activity {
//예약 시간은 종료시간 넣어두고, 끝나는 시간이 되면 다시 0으로 바꿔서 저장
    Button cancelB;
    Button reservationB;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    TextView chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_reservation);

        Intent intent = getIntent();
        String seats = intent.getStringExtra("seats");
        String pcbangName = intent.getStringExtra("name");

        chosen = (TextView)findViewById(R.id.chosenSeat);
        Log.v("names","인텐트받아오기");
        String[] seatnumber = seats.split(" ");
        String seatNum = seatnumber[2];

        String available;

        chosen.setText(pcbangName+ " " + seatNum+ " 번 좌석");
        reservationB = (Button)findViewById(R.id.reservationB);

        ref = database.getReference("PC bangs").child(pcbangName).child("seat").child(seatNum);

        long now = System.currentTimeMillis();
        now = now +3600000;
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

        reservationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String available = snapshot.child("time").getValue().toString();
                        if(available.equals("0")){
                            ref.child("time").setValue(strNow);
                            Toast.makeText(getApplicationContext(),"예약되었습니다!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"이미 다른 사람이 예약했습니다.",Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        cancelB = (Button)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowSeat.class);
                intent.putExtra("PCbangName",pcbangName);
                startActivity(intent);
            }
        });


    }

}
