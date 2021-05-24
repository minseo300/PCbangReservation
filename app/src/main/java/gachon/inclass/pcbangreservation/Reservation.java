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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    DatabaseReference nameRef;
    DatabaseReference reservedRef;
    TextView chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_reservation);

        Intent intent = getIntent();
        String seats = intent.getStringExtra("seats");
        String address = intent.getStringExtra("address");

        chosen = (TextView)findViewById(R.id.chosenSeat);
        String[] seatnumber = seats.split(" ");
        String seatNum = seatnumber[2];
        Toast.makeText(getApplicationContext(),seatNum,Toast.LENGTH_SHORT).show();

        Log.v("test","Start reservation activity intent by ShowSeat. Got 2 String. first one is seats number "+ seatNum+", address is " + address);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Log.v("names",user.getEmail());
        String[] emailID = user.getEmail().split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];


        reservationB = (Button)findViewById(R.id.reservationB);

        ref = database.getReference("PC bangs").child(address);
        myref = database.getReference("Users").child(DBEmail);

        long now = System.currentTimeMillis();
        now = now +3600000;
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

       int fee=0;

       nameRef = database.getReference("PC bangs").child(address).child("name");

        nameRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String name = task.getResult().getValue().toString();
                chosen.setText(name + ":" + seatNum );

            }
        });

        reservationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.getResult().child("reserved").getValue().toString().equals("")){
                            String addresses = task.getResult().child("reservedAddress").getValue().toString();
                            String reserved = task.getResult().child("reserved").getValue().toString();
                            String[] seatnumber = reserved.split(":");

                            reservedRef = database.getReference("PC bangs").child(addresses).child("seat").child(seatnumber[1]).child("time");
                            reservedRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    String strNow = sdfNow.format(date);

                                    String time = task.getResult().getValue().toString();
                                    if(time.equals("0")){
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
                                                                ref.child("seat").child(seatNum).child("time").setValue(strNow);
                                                                myref.child("reserved").setValue(chosen.getText().toString());
                                                                myref.child("payment").setValue(Integer.parseInt(mycharge) - Integer.parseInt(fee));
                                                                myref.child("reservedAddress").setValue(address);
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

                                    else{
                                        if (time.compareTo(strNow) < 0) {
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
                                                                    ref.child("seat").child(seatNum).child("time").setValue(strNow);
                                                                    myref.child("reserved").setValue(chosen.getText().toString());
                                                                    myref.child("payment").setValue(Integer.parseInt(mycharge) - Integer.parseInt(fee));
                                                                    myref.child("reservedAddress").setValue(address);
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
                                        else{
                                            Toast.makeText(getApplicationContext(),"이미 예약한 좌석이 있습니다",Toast.LENGTH_SHORT).show();
                                        }

                                    }


                                }
                            });
                        }

                        else{
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
                                                    ref.child("seat").child(seatNum).child("time").setValue(strNow);
                                                    myref.child("reserved").setValue(chosen.getText().toString());
                                                    myref.child("payment").setValue(Integer.parseInt(mycharge) - Integer.parseInt(fee));
                                                    myref.child("reservedAddress").setValue(address);
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
                    }
                });


            }
        });

        cancelB = (Button)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShowSeat.class);
                intent.putExtra("PCbangName",chosen.getText().toString());
                intent.putExtra("address",address);
                startActivity(intent);
                finish();
            }
        });


    }

}
