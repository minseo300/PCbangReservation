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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        chosen.setText(pcbangName+ " " + seatNum+ " 번 좌석");
        reservationB = (Button)findViewById(R.id.reservationB);

        ref = database.getReference("PC bangs").child(pcbangName).child("seat").child(seatNum);

        long now = System.currentTimeMillis();
        now = now +3600000;
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

        String[] hournow = strNow.split(" ");

        Toast.makeText(getApplicationContext(),strNow,Toast.LENGTH_LONG).show();

        reservationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
