package gachon.inclass.pcbangreservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

public class ShowReservedSeat  extends Activity {

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    DatabaseReference PCref;

    TextView txt;
    Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_showreserved);

        button = (Button)findViewById(R.id.showRtoProfile);
        txt = (TextView)findViewById(R.id.reservedSeat);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String[] emailID = user.getEmail().split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strNow = sdfNow.format(date);

        ref = database.getReference("Users").child(DBEmail);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String address = snapshot.child("reservedAddress").getValue().toString();
                String text = snapshot.child("reserved").getValue().toString();
                if(text.equals(""))
                    txt.setText("예약된 좌석이 없습니다."); //예약된 좌석이 없는 경우

                else {//현재 예약되어 있는 좌석이 있는 경우
                    String[] texts = text.split(":");
                    PCref = database.getReference("PC bangs").child(address).child("seat").child(texts[1]);


                    PCref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            String time = task.getResult().getValue().toString();
                            if (time.compareTo(strNow) < 0) {
                                ref.child("reserved").setValue("");
                                txt.setText("예약된 좌석이 없습니다.");
                            }
                            else
                                txt.setText(text);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                finish();
            }
        });

    }
}
