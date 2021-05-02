package gachon.inclass.pcbangreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Reservation extends AppCompatActivity {

    Button cancelB;
    Button reservationB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        cancelB = (Button)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });

        reservationB = (Button)findViewById(R.id.reservationB);
        reservationB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //reservation 버튼이 눌리면 database에 그 좌석의 시간 1시간 추가하기 -> firebase는 realtime이라서 따로 pc방이나 서버에서 건드릴 부분 x
    }

}
