package com.mobiapp4u.pc.routinebasketshipper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mobiapp4u.pc.routinebasketshipper.Common.Common;
import com.mobiapp4u.pc.routinebasketshipper.Model.Shipper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {
    MaterialEditText shipperPhone,shipperPwd;
    FButton btnSignIn;

    FirebaseDatabase fd;
    DatabaseReference shippers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fd = FirebaseDatabase.getInstance();
        shippers = fd.getReference(Common.SHIPPER_TABLE);

        shipperPhone = (MaterialEditText)findViewById(R.id.edit_shipper_phone);
        shipperPwd = (MaterialEditText)findViewById(R.id.edit_shipper_pwd);
        btnSignIn = (FButton)findViewById(R.id.btn_signIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shippers.child(shipperPhone.getText().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Shipper shipper1 = dataSnapshot.getValue(Shipper.class);
                                    if(shipper1.getPassword().equals(shipperPwd.getText().toString())){
                                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                                        Common.currentShipper = shipper1;
                                        finish();
                                    }else {
                                        Toast.makeText(MainActivity.this,"Password does not match",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MainActivity.this,""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

    }
}
