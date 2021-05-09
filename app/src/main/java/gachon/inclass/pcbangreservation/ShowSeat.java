package gachon.inclass.pcbangreservation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ShowSeat  extends AppCompatActivity {

    Button btnGoBack;
    TextView nameText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showseat);

        Intent intent = getIntent();
        String name = intent.getStringExtra("PCbangName");

        nameText = (TextView)findViewById(R.id.chosenPCbangName);

        nameText.setText(name);

        btnGoBack = (Button)findViewById(R.id.goBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
