package gachon.inclass.pcbangreservation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Reservation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        //reservation 버튼이 눌리면 database에 그 좌석의 시간 1시간 추가하기 -> firebase는 realtime이라서 따로 pc방이나 서버에서 건드릴 부분 x
    }

}
