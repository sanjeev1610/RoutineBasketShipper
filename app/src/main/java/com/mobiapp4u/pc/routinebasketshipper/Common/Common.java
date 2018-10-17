package com.mobiapp4u.pc.routinebasketshipper.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mobiapp4u.pc.routinebasketshipper.Model.Request;
import com.mobiapp4u.pc.routinebasketshipper.Model.Shipper;
import com.mobiapp4u.pc.routinebasketshipper.Model.ShippingInformation;
import com.mobiapp4u.pc.routinebasketshipper.Remote.IGeoCoordinates;
import com.mobiapp4u.pc.routinebasketshipper.Remote.RetrofitClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Common {

    public static final int REQUEST_CODE = 1000;
    public static final String SHIPPER_TABLE = "Shippers";
    public static final String SHIPPER_INFO_ORDER = "ShippingOrders";
    public static final String ORDER_NEED_SHIPPER = "OrdersNeedShipper";

    public static  Shipper currentShipper;
    public static Request currentRequest;
    public static String currentKey;

    public static final String baseUrl = "https://maps.googleapis.com";
    public static IGeoCoordinates getGeoCodeServices(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }


    public static String converCodeToStatus(String status){
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On My way";
        else if(status.equals("2"))
            return "Shipping";
        else
            return "Shipped";

    }
    public static String getDate(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd:MM:yyyy HH:mm",calendar).toString());
        return date.toString();
    }

    public static void createShippingOrder(String key, String phone, Location mLastLocation) {
        ShippingInformation shippingInformation = new ShippingInformation();
        shippingInformation.setOrderId(key);
        shippingInformation.setShipperPhone(phone);
        shippingInformation.setLat(mLastLocation.getLatitude());
        shippingInformation.setLng(mLastLocation.getLongitude());

        //create new item on shippin info table
        FirebaseDatabase.getInstance().getReference(SHIPPER_INFO_ORDER)
                .child(key)
                .setValue(shippingInformation)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR",e.getLocalizedMessage());
                    }
                });

    }

    public static void updateShippingInformation(String currentKey, Location mLastLocation) {
        Map<String,Object> update = new HashMap<>();
        update.put("lat",mLastLocation.getLatitude());
        update.put("lng",mLastLocation.getLongitude());

        FirebaseDatabase.getInstance().getReference(SHIPPER_INFO_ORDER)
                .child(currentKey)
                .updateChildren(update)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR",e.getLocalizedMessage());

                    }
                });
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight)
    {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth/(float)bitmap.getWidth();
        float scaleY = newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatric = new Matrix();
        scaleMatric.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatric);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }
}
