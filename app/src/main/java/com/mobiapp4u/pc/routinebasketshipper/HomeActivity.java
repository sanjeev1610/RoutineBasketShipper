package com.mobiapp4u.pc.routinebasketshipper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketshipper.Common.Common;
import com.mobiapp4u.pc.routinebasketshipper.Interface.ItemClickListner;
import com.mobiapp4u.pc.routinebasketshipper.Model.Request;
import com.mobiapp4u.pc.routinebasketshipper.Model.Token;
import com.mobiapp4u.pc.routinebasketshipper.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class HomeActivity extends AppCompatActivity {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    Location mLastLocation;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference shipperOrders;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED)
            {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE
            }, Common.REQUEST_CODE);

        }else {
            buildLocationRequest();
            buildLocationCallback();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
        }
        database = FirebaseDatabase.getInstance();
        shipperOrders = database.getReference(Common.ORDER_NEED_SHIPPER);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_order);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        loadAllOrderNeedShip(Common.currentShipper.getPhone());
    }

    private void loadAllOrderNeedShip(String phone) {
        DatabaseReference orderNeedChildShipper = shipperOrders.child(phone);
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(orderNeedChildShipper,Request.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderStatus.setText(Common.converCodeToStatus(model.getStatus()));
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {

                    }
                });
                viewHolder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, OrderDetail.class);
                        Common.currentRequest = adapter.getItem(position);
                        intent.putExtra("orderId",adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

                viewHolder.btnShipping.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mLastLocation!=null) {
                            Common.createShippingOrder(adapter.getRef(position).getKey(), Common.currentShipper.getPhone(), mLastLocation);
                            Common.currentRequest = model;
                            Common.currentKey = adapter.getRef(position).getKey();
                            startActivity(new Intent(HomeActivity.this, TrackingOrder.class));
                        }else{
                            Toast.makeText(HomeActivity.this,"Could'not get Location plz turn on Gps or set your location",Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_order_status,parent,false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void updateToken(String tokenref) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        Token token = new Token(tokenref,"false");
        tokens.child(Common.currentShipper.getPhone()).setValue(token);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case Common.REQUEST_CODE:
            {
                if(grantResults.length >0)
                {
                  if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                  {
                    buildLocationCallback();
                    buildLocationRequest();
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                    if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper());
                  }else {
                      Toast.makeText(HomeActivity.this,"You shouls assign permission",Toast.LENGTH_SHORT).show();
                  }
                }
            }
            break;
            default:
                break;
        }
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(10f)
                .setFastestInterval(3000)
                .setInterval(5000);

    }


    private void buildLocationCallback() {
     locationCallback = new LocationCallback(){
         @Override
         public void onLocationResult(LocationResult locationResult) {
             mLastLocation = locationResult.getLastLocation();
             Toast.makeText(HomeActivity.this,new StringBuilder("")
             .append(mLastLocation.getLatitude())
             .append("/")
             .append(mLastLocation.getLongitude()).toString(),Toast.LENGTH_SHORT).show();


         }
     };


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllOrderNeedShip(Common.currentShipper.getPhone());
    }

    @Override
    protected void onStop() {
        if(adapter!=null){
           adapter.stopListening();
        }
        if(fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        super.onStop();
    }
}


























