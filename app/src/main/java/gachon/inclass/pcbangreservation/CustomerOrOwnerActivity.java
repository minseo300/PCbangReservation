package gachon.inclass.pcbangreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

import javax.security.auth.login.LoginException;

public class CustomerOrOwnerActivity extends AppCompatActivity {

    ImageButton user;
    ImageButton pc;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_or_owner);

        //initializing firebase authentication object
//        firebaseAuth = FirebaseAuth.getInstance();
//        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
//        if(firebaseAuth.getCurrentUser() == null) {
//            finish();
//            startActivity(new Intent(this, ProfileActivity.class));
//        }

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