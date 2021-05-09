package gachon.inclass.pcbangreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });


        //reservation 버튼이 눌리면 database에 그 좌석의 시간 1시간 추가하기 -> firebase는 realtime이라서 따로 pc방이나 서버에서 건드릴 부분 x
    }

}
