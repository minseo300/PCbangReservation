package gachon.inclass.pcbangreservation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class OwnerProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    //view objects
    private TextView textViewPCbangName;
    private Button buttonLogout;
    private TextView textivewDelete;
    private Button buttonUploadSeatgrid;
    SeatlistAdapterOwner adapter;
    ArrayList<ListViewItemOwner> seatNums;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);

        //initializing views
        textViewPCbangName = (TextView) findViewById(R.id.textviewPCbangName);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        textivewDelete = (TextView) findViewById(R.id.textviewDelete);
        buttonUploadSeatgrid = (Button) findViewById(R.id.buttonUploadSeatgrid);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //유저가 있다면, null이 아니면 계속 진행
        FirebaseUser owner = firebaseAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("PC bangs");

        String PCbangEmail = owner.getEmail();

        RecyclerView rcView = findViewById(R.id.pcbangSeatView);
        rcView.setLayoutManager(new LinearLayoutManager(this));

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("email")){
                    String email = snapshot.child("email").getValue().toString();
                    Log.v("names",email);
                    if(email.equals(PCbangEmail)) {
                        textViewPCbangName.setText(snapshot.child("name").getValue().toString());
                        DatabaseReference ref_seat = ref.child(textViewPCbangName.getText().toString()); // 보류1: 될지 모르겠음 (reference 안 reference 구조)
                        ref_seat.child("seat").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if(snapshot.hasChild("number") && snapshot.hasChild("time")){
                                    String number = snapshot.child("number").getValue().toString();
                                    String time = snapshot.child("time").getValue().toString();
                                    ListViewItemOwner item = new ListViewItemOwner(number, time);

                                    seatNums.add(item);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if(snapshot.hasChild("number") && snapshot.hasChild("time")){
                                    String number = snapshot.child("number").getValue().toString();
                                    String time = snapshot.child("time").getValue().toString();
                                    ListViewItemOwner item = new ListViewItemOwner(number, time);

                                    seatNums.add(item);
                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild("number") && snapshot.hasChild("time")){
                                    String number = snapshot.child("number").getValue().toString();
                                    String time = snapshot.child("time").getValue().toString();
                                    ListViewItemOwner item = new ListViewItemOwner(number, time);

                                    seatNums.add(item);
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
                        adapter = new SeatlistAdapterOwner(getApplicationContext(), seatNums);
                        rcView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new SeatlistAdapterOwner(getApplicationContext(), seatNums);
        rcView.setAdapter(adapter);


        //logout button event
        buttonLogout.setOnClickListener(this);
        textivewDelete.setOnClickListener(this);

    }


    public void onClick(View view) {

        if (view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //회원탈퇴를 클릭하면 회원정보를 삭제한다. 삭제전에 컨펌창을 하나 띄워야 겠다.
        if(view == textivewDelete) {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(OwnerProfileActivity.this);
            alert_confirm.setMessage("정말 계정을 삭제 할까요?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(OwnerProfileActivity.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(OwnerProfileActivity.this, "취소", Toast.LENGTH_LONG).show();
                }
            });
            alert_confirm.show();
        }
        if(view == buttonUploadSeatgrid){
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(galleryIntent, 1);

            File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    String filename = textViewPCbangName.getText().toString() + "_seats.jpg";
                    Uri file = getImageUri(getApplicationContext(), img);
                    Log.d("Uri", String.valueOf(file));
                    UploadTask uploadTask = storageRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "사진 등록완료!", Toast.LENGTH_SHORT);
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
