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

        // 코드 계속 ...

        ListView listview ;
        CustomChoiceListViewAdapter adapter;

        // Adapter 생성
        adapter = new CustomChoiceListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        //여기서 나중에 pc방 좌석별 이름 받아와서 text넣으면 됩니다.
        // 첫 번째 아이템 추가.
        adapter.addItem("seat1") ;
        // 두 번째 아이템 추가.
        adapter.addItem("seat2") ;
        // 세 번째 아이템 추가.
        adapter.addItem("seat3") ;


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
