package gachon.inclass.pcbangreservation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final String TAG = "ProfileActivity";

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private TextView textivewDelete;
    PCbangListAdapter adapter;
    ArrayList<String> pcbangNames;
    Button pay;
    Button account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //initializing views
        textViewUserEmail = (TextView) findViewById(R.id.textviewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        textivewDelete = (TextView) findViewById(R.id.textviewDelete);
        pay = (Button)findViewById(R.id.payment);
        account = (Button)findViewById(R.id.account);


        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //유저가 있다면, null이 아니면 계속 진행
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //textViewUserEmail의 내용을 변경해 준다.
        textViewUserEmail.setText("반갑습니다.\n"+ user.getEmail()+"으로 로그인 하였습니다.");

        pcbangNames = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("PC bangs");

        RecyclerView rcView = findViewById(R.id.pcbangrcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("name")){
                    String names =snapshot.child("name").getValue().toString();
                    Log.v("names",names);
                    pcbangNames.add(names);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("name")){
                    String names = snapshot.child("name").getValue().toString();
                    pcbangNames.add(names);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("name")){
                    String names =snapshot.child("name").getValue().toString();
                    pcbangNames.add(names);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                adapter.notifyDataSetChanged();
            }
        });

        pcbangNames.add("Hello");
        adapter = new PCbangListAdapter(getApplicationContext(),pcbangNames);
        rcView.setAdapter(adapter);


        //logout button event
        buttonLogout.setOnClickListener(this);
        textivewDelete.setOnClickListener(this);
        pay.setOnClickListener(this);
        account.setOnClickListener(this);

    }


    public void onClick(View view) {

        if(view == pay){
            startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
        }

        if (view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //회원탈퇴를 클릭하면 회원정보를 삭제한다. 삭제전에 컨펌창을 하나 띄워야 겠다.
        if(view == textivewDelete) {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ProfileActivity.this);
            alert_confirm.setMessage("정말 계정을 삭제 할까요?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ProfileActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    });
                        }
                    }
            );
            alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(ProfileActivity.this, "취소", Toast.LENGTH_LONG).show();
                }
            });
            alert_confirm.show();
        }

//        // 식당추천 카테고리로 연결해주는 임시 버튼
//        if(view == buttonRest){
//
//            Intent restIntent = new Intent(getApplicationContext(), SetLocationActivity.class);
//            startActivity(restIntent);
//        }
    }
}