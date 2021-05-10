package gachon.inclass.pcbangreservation;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    Button okB,cancelB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        okB = findViewById(R.id.btnPay);

        cancelB = findViewById(R.id.payCancel);

    }
}
