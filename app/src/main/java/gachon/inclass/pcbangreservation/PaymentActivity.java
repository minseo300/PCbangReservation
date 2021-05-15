package gachon.inclass.pcbangreservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends Activity {
    Button okB,cancelB;
    EditText charged;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payment);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String email = user.getEmail();

        String[] emailID = email.split("\\.");
        String DBEmail = emailID[0]+"_"+emailID[1];

        ref = database.getReference("Users");

        charged = (EditText)findViewById(R.id.wantForCharge);

        okB = findViewById(R.id.btnPay);
        okB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = charged.getText().toString();
                ref.child(DBEmail).child("payment").setValue(money);
                Toast.makeText(getApplicationContext(),money+" 원이 충전되었습니다!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancelB = findViewById(R.id.payCancel);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }

}
