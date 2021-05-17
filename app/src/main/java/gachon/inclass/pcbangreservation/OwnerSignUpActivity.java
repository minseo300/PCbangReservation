package gachon.inclass.pcbangreservation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class OwnerSignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText fee;
    EditText detailed_address;
    EditText editTextAddress;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText etConfirmPW;
    EditText editTextPCbangName;
    EditText editTextNumber_of_seats;
    Button buttonSignup;
    TextView textviewSingin;
    TextView textviewMessage;
    ProgressDialog progressDialog;
    //define firebase object
    FirebaseAuth firebaseAuth;

    String name;
    int seats=0;
    String address;
    String latitude; //위도
    String longitude;//경도
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_sign_up);

        //initializig firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

//        if(firebaseAuth.getCurrentUser() != null){
//            //이미 로그인 되었다면 이 액티비티를 종료함
//            finish();
//            //그리고 profile 액티비티를 연다.
//            startActivity(new Intent(getApplicationContext(), ProfileActivity.class)); //추가해 줄 ProfileActivity
//        }
        //initializing views
        editTextEmail = (EditText) findViewById(R.id.signup_email);
        editTextPassword = (EditText) findViewById(R.id.signup_password);
        etConfirmPW=(EditText)findViewById(R.id.confirmPW);
        textviewSingin= (TextView) findViewById(R.id.textViewSignin);
        editTextPCbangName=(EditText)findViewById(R.id.pcbang_name);
        editTextAddress=(EditText)findViewById(R.id.address);
        editTextNumber_of_seats=(EditText)findViewById(R.id.number_of_seats);
        fee=(EditText)findViewById(R.id.fee);
        detailed_address=(EditText)findViewById(R.id.detailed_address);

        //textviewMessage = (TextView) findViewById(R.id.textviewMessage);
        buttonSignup = (Button) findViewById(R.id.signup_bt);
        progressDialog = new ProgressDialog(this);

        //button click event
        buttonSignup.setOnClickListener(this);
        textviewSingin.setOnClickListener(this);
    }
    //
//    //Firebse creating a new user 회원가입
    private void registerUser(){
        //사용자가 입력하는 email, password를 가져온다.
        final Geocoder geocoder=new Geocoder(this);
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        seats=Integer.parseInt(editTextNumber_of_seats.getText().toString()); //좌석 수
        name=editTextPCbangName.getText().toString();//피씨방 이름
        address=editTextAddress.getText().toString(); // 피씨방 주소        //email과 password가 비었는지 아닌지를 체크 한다.
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password를 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(etConfirmPW.getText().toString()))
        {
            Toast.makeText(OwnerSignUpActivity.this,"비밀번호 불일치",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6)
        {
            Toast.makeText(OwnerSignUpActivity.this,"비밀번호 최소 6자리 이상",Toast.LENGTH_SHORT).show();
            return;
        }
//        email과 password가 제대로 입력되어 있다면 계속 진행된다.
        progressDialog.setMessage("등록중입니다. 기다려 주세요...");
        progressDialog.show();

        //가입 요청 ---이메일!
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(OwnerSignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();

                    user.sendEmailVerification().addOnCompleteListener(OwnerSignUpActivity.this, new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                String email = user.getEmail();
                                String uid = user.getUid();
                                HashMap<Object,String> hashMap = new HashMap<>();
//
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name",name);
                                hashMap.put("seats",String.valueOf(seats));
                                hashMap.put("address",address);

                                List<Address> list=null;
                                try{
                                    list=geocoder.getFromLocationName(address,10); //지역이름, 읽을 개수
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                    Log.e("test","입출력 오류- 서버에서 주소 변환시 에러 발생");
                                }
                                for(Address t:list){
                                    latitude=String.valueOf(t.getLatitude());
                                    longitude=String.valueOf(t.getLongitude());
                                }

                                System.out.println(latitude);
                                System.out.println(longitude);
                                hashMap.put("latitude",latitude);
                                hashMap.put("longitude",longitude);
                                hashMap.put("detailed address",detailed_address.getText().toString());
                                hashMap.put("fee",fee.getText().toString());
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("PC bangs");
                                DatabaseReference ref =reference.child(name);

                                ref.setValue(hashMap);
                                for(int i=1;i<=seats;i++) {
                                    ref.child("seat").child(String.valueOf(i)).child("number").setValue(String.valueOf(i));
                                    ref.child("seat").child(String.valueOf(i)).child("time").setValue("0");
                                }

                                Toast.makeText(OwnerSignUpActivity.this,"이메일 인증 후 로그인 하세요",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), OwnerLoginActivity.class));
                            }
                            else{
                                Toast.makeText(OwnerSignUpActivity.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    //에러발생
                    //textviewMessage.setText("에러유형\n - 이미 등록된 이메일  \n -암호 최소 6자리 이상 \n - 서버에러");
                    Toast.makeText(OwnerSignUpActivity.this, "이메일 안보내지는 듯--등록 에러!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    //button click event
    public void onClick(View view) {
        if(view == buttonSignup) {

            registerUser();
        }

        if(view == textviewSingin) {

            startActivity(new Intent(this, OwnerLoginActivity.class));
        }
    }
}