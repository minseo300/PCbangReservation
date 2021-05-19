package gachon.inclass.pcbangreservation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener, AutoPermissionsListener {

    private static final String TAG = "ProfileActivity";

    //LocationManager manager;
    //GPSListener gpsListener;

    //firebase auth object
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref;
    //view objects
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private Button Delete;
    PCbangListAdapter adapter;
    ArrayList<ListViewItem> pcbangNames;
    Button pay;
    Button account;

    Button map;
    ImageButton current_location;
    TextView my_location;

    Double latitude;
    Double longitude;
    LatLng currentPosition;
    private Location location;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private LocationRequest locationRequest;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초
    private FusedLocationProviderClient mFusedLocationClient;

    final Geocoder geocoder=new Geocoder(this);

    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location user_location=new Location("user location");
    Location store_location=new Location("store location");

    Button checkAccount,reservedSeat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        AutoPermissions.Companion.loadAllPermissions(ProfileActivity.this, 101);





        //initializing views
        my_location=(TextView)findViewById(R.id.my_location);
        current_location=(ImageButton)findViewById(R.id.current_location);
        textViewUserEmail = (TextView) findViewById(R.id.textviewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        Delete = (Button) findViewById(R.id.textviewDelete);
        pay = (Button)findViewById(R.id.payment);
        account = (Button)findViewById(R.id.account);

        map=(Button)findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
            }
        });
        mLayout = findViewById(R.id.activity_profile); //todo

        checkAccount = (Button)findViewById(R.id.payed);
        reservedSeat = (Button)findViewById(R.id.btnreserved);



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
        textViewUserEmail.setText(user.getEmail());

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("PC bangs");
        pcbangNames = new ArrayList<>();



        RecyclerView rcView = findViewById(R.id.pcbangrcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));

        //manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //gpsListener=new GPSListener();
        //AutoPermissions.Companion.loadAllPermissions(ProfileActivity.this, 101);

       current_location.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               //AutoPermissions.Companion.loadAllPermissions(ProfileActivity.this, 101);
               startLocationService();
               Log.v(">>>>>>>>>>Latitude: ",String.valueOf(latitude));
               Log.v(">>>>>>>>>>>>Longitude: ",String.valueOf(longitude));

               for(ListViewItem i:pcbangNames)
               {
                   ref.child(i.getStore_name()).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           Log.v("가게 이름: ",i.getStore_name());
                           String lat=String.valueOf(snapshot.child("latitude").getValue());
                           Log.v("가게 위도: ",lat);
                           String lon=String.valueOf(snapshot.child("longitude").getValue());
                           Log.v("가게 경도: ",lon);
                           store_location.setLatitude(Double.parseDouble(lat));
                           store_location.setLongitude(Double.parseDouble(lon));
                           float distance=user_location.distanceTo(store_location);
                           distance=distance/1000; //km단위
                           Log.v("거리: ",String.valueOf(distance));
                           if(distance>1)
                           {
                               Log.v("names",i.getStore_name());
                               pcbangNames.remove(i);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
               }
               if(pcbangNames.size()==0)
               {
                   Log.v("가까운 피씨방 없음","NULL");
               }
               for(ListViewItem i:pcbangNames)
               {
                   Log.v("이름: ",i.getStore_name());
               }

               adapter = new PCbangListAdapter(getApplicationContext(),pcbangNames);
               rcView.setAdapter(adapter);
           }
       });







//        //pcbanglist



        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChild("name")){
                    String names =snapshot.child("name").getValue().toString();
                    String store_lat=snapshot.child("latitude").getValue().toString();
                    String store_lon=snapshot.child("longitude").getValue().toString();
                    String address=snapshot.child("address").getValue().toString();
                    String detail = snapshot.child("detailed address").getValue().toString();
                    ListViewItem item=new ListViewItem(names,address,detail);

                    pcbangNames.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(snapshot.hasChild("name")){
                    String names = snapshot.child("name").getValue().toString();
                    String store_lat=snapshot.child("latitude").getValue().toString();
                    String store_lon=snapshot.child("longitude").getValue().toString();
                    String address=snapshot.child("address").getValue().toString();
                    String detail = snapshot.child("detailed address").getValue().toString();
                    ListViewItem item=new ListViewItem(names,address,detail);

                    pcbangNames.add(item);

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                if(snapshot.hasChild("name")){
                    String names =snapshot.child("name").getValue().toString();
                    String store_lat=snapshot.child("latitude").getValue().toString();
                    String store_lon=snapshot.child("longitude").getValue().toString();
                    String address=snapshot.child("address").getValue().toString();
                    String detail = snapshot.child("detailed address").getValue().toString();
                    ListViewItem item=new ListViewItem(names,address,detail);

                    pcbangNames.add(item);

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
//

        adapter = new PCbangListAdapter(getApplicationContext(),pcbangNames);
        rcView.setAdapter(adapter);


        //logout button event
        buttonLogout.setOnClickListener(this);
        //탈퇴
        Delete.setOnClickListener(this);
        //
        pay.setOnClickListener(this);
        account.setOnClickListener(this);
        checkAccount.setOnClickListener(this);
        reservedSeat.setOnClickListener(this);

    }




    public void onClick(View view) {

        if(view == reservedSeat){
            startActivity(new Intent(getApplicationContext(),ShowReservedSeat.class));
        }

        if(view == checkAccount){
            startActivity(new Intent(getApplicationContext(),ShowPayment.class));
        }

        if(view == pay){
            startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
        }

        if (view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //회원탈퇴를 클릭하면 회원정보를 삭제한다. 삭제전에 컨펌창을 하나 띄워야 겠다.
        if(view == Delete) {
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
        if(view==map)
        {
            finish();
            startActivity(new Intent(this,MapActivity.class));
        }


    }


    public void startLocationService() {
        String city;
        String country;
        LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            user_location.setLatitude(latitude);
            Log.v(">>Latitude: ",String.valueOf(latitude));
            longitude = location.getLongitude();
            Log.v(">>Longitude: ",String.valueOf(longitude));
            user_location.setLongitude(longitude);

            String message = "최근 위치 -> Latitude : " + latitude + "\nLongitude : " + longitude;
            List<Address> citylist=null;
            try {
                citylist= geocoder.getFromLocation(latitude,longitude,10);
                if(citylist != null) {
                    if(citylist.size() == 0){
                        Log.e("reverseGeocoding", "해당 도시 없음");
                    }
                    else {
                        city = citylist.get(0).getAdminArea();
                        country = citylist.get(0).getCountryName();
                        my_location.setText(citylist.get(0).getAddressLine(0));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //my_location.setText(message);

            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            Toast.makeText(getApplicationContext(), "내 위치확인 요청함", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {
        Toast.makeText(getApplicationContext(), "permissions denied : " + strings.length, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGranted(int i, String[] strings) {
        Toast.makeText(getApplicationContext(), "permissions granted : " + strings.length, Toast.LENGTH_SHORT).show();
    }

    class GPSListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            String message = "최근 위치 -> Latitude : " + latitude + "\nLongitude : " + longitude;


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    }
}