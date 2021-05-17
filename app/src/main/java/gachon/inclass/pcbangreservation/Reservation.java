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
import com.google.firebase.auth.FirebaseUser;
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
    DatabaseReference myref;
    DatabaseReference feeref;
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
        String[] seatnumber = seats.split(" ");
        String seatNum = seatnumber[2];

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.v("names",user.getEmail());
        String[] emailID = user.getEmail().split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];

        chosen.setText(pcbangName+ " " + seatNum+ " 번 좌석");
        reservationB = (Button)findViewById(R.id.reservationB);

        ref = database.getReference("PC bangs").child(pcbangName);
        myref = database.getReference("Users").child(DBEmail);

        long now = System.currentTimeMillis();
        now = now +3600000;
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

       int fee=0;



        reservationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String available = snapshot.child("seat").child(seatNum).child("time").getValue().toString();
                        String fee = snapshot.child("fee").getValue().toString();
                        if(available.equals("0")){
                            myref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String mycharge = snapshot.child("payment").getValue().toString();
                                    if(Integer.parseInt(mycharge) > Integer.parseInt(fee)){
                                        ref.child("time").setValue(strNow);
                                        myref.child("reserved").setValue(pcbangName +":" +seatNum);
                                        myref.child("payment").setValue(Integer.parseInt(mycharge) - Integer.parseInt(fee));
                                        Toast.makeText(getApplicationContext(),"예약되었습니다!",Toast.LENGTH_SHORT).show();

                                    }

                                    else
                                        Toast.makeText(getApplicationContext(),"잔액이 부족합니다.",Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"이미 다른 사람이 예약했습니다.",Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        finish();
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
                finish();
            }
        });


    }

}
