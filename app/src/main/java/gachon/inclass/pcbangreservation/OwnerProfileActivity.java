package gachon.inclass.pcbangreservation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
    private TextView textViewPCbangAddress;
    private Button buttonLogout;
    private TextView textivewDelete;
    private Button buttonUploadSeatgrid;
    private ImageView seatImage;
    SeatlistAdapterOwner adapter;
    ArrayList<ListViewItemOwner> seatNums = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_profile);

        //initializing views
        textViewPCbangName = (TextView) findViewById(R.id.textviewPCbangName);
        textViewPCbangAddress = (TextView) findViewById(R.id.textviewPCbangAddress);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        textivewDelete = (TextView) findViewById(R.id.textviewDelete);
        buttonUploadSeatgrid = (Button) findViewById(R.id.buttonUploadSeatgrid);
        seatImage = (ImageView) findViewById(R.id.imageView);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        //????????? ????????? ?????? ?????? ???????????? null ???????????? ??? ??????????????? ???????????? ????????? ??????????????? ??????.
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //????????? ?????????, null??? ????????? ?????? ??????
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
                        textViewPCbangAddress.setText(snapshot.child("address").getValue().toString());
                        DatabaseReference ref_seat = ref.child(snapshot.child("address").getValue().toString());
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

                                    int removedIndex = 0;
                                    for(int i=0; i<seatNums.size(); i++){
                                        if(seatNums.get(i).getSeat_num() == number){
                                            seatNums.remove(i);
                                            removedIndex = i;
                                            break;
                                        }
                                    }
                                    seatNums.add(removedIndex, item);
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
                        adapter = new SeatlistAdapterOwner(getApplicationContext(), seatNums, textViewPCbangAddress.getText().toString());
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

        adapter = new SeatlistAdapterOwner(getApplicationContext(), seatNums, textViewPCbangAddress.getText().toString());
        rcView.setAdapter(adapter);


        //logout button event
        buttonLogout.setOnClickListener(this);
        textivewDelete.setOnClickListener(this);
        buttonUploadSeatgrid.setOnClickListener(this);

    }


    public void onClick(View view) {

        if (view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //??????????????? ???????????? ??????????????? ????????????. ???????????? ???????????? ?????? ????????? ??????.
        if(view == textivewDelete) {
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(OwnerProfileActivity.this);
            alert_confirm.setMessage("?????? ????????? ?????? ??????????").setCancelable(false).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(OwnerProfileActivity.this, "????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        }
                                    });
                        }
                    }
            );
            alert_confirm.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(OwnerProfileActivity.this, "??????", Toast.LENGTH_LONG).show();
                }
            });
            alert_confirm.show();
        }
        if(view == buttonUploadSeatgrid){
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(galleryIntent, 1);
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
                    String filename = textViewPCbangName.getText().toString() + "_seats.jpg";
                    StorageReference storageRef = storage.getReference().child(filename);
                    Uri file = getImageUri(getApplicationContext(), img);
                    Log.d("uri", String.valueOf(file));
                    UploadTask uploadTask = storageRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "?????? ????????????!", Toast.LENGTH_SHORT);
                        }
                    });

//                    File saveFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/seats_img");
//                    if(!saveFile.isDirectory()){
//                        saveFile.mkdir();
//                    }
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try {
                                        Log.d("uri", String.valueOf(uri));
                                        Glide.with(getApplicationContext()).load(uri).into(seatImage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

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
