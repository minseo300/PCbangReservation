package gachon.inclass.pcbangreservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Reservation extends Activity {
//예약 시간은 종료시간 넣어두고, 끝나는 시간이 되면 다시 0으로 바꿔서 저장
    Button cancelB;
    Button reservationB;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_reservation);

        reservationB = (Button)findViewById(R.id.reservationB);



        cancelB = (Button)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });


        //reservation 버튼이 눌리면 database에 그 좌석의 시간 1시간 추가하기 -> firebase는 realtime이라서 따로 pc방이나 서버에서 건드릴 부분 x
    }

}
