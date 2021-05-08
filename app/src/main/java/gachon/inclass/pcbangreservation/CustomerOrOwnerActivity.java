package gachon.inclass.pcbangreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import javax.security.auth.login.LoginException;

public class CustomerOrOwnerActivity extends AppCompatActivity {

    ImageButton user;
    ImageButton pc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_or_owner);

        user=(ImageButton)findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        pc=(ImageButton)findViewById(R.id.pc);
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), OwnerLoginActivity.class));
            }
        });
    }
}