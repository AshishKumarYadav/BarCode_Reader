package com.ashish.idflow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.ashish.idflow.database.DatabaseReference;
import com.ashish.idflow.database.ScanTable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    String TAG=MainActivity.class.getSimpleName();

    Button camBtn;
    int CAMERA_PIC_REQUEST=111;
    boolean isPermissionForAllGranted=false;
    private IntentIntegrator qrScan;
    LottieAnimationView animationView;
    BottomSheetBehavior bottomSheetBehavior;
    CardView linearLayout;
    ListView listView;
    ArrayList<String> arrayList;
    List<ScanTable> tableList;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camBtn=findViewById(R.id.btn);
        animationView=findViewById(R.id.animation_view);
        linearLayout =findViewById(R.id.bottomSheet);
        listView=findViewById(R.id.listView);

        animationView.setSpeed(0.3f);

        bottomSheetBehavior=BottomSheetBehavior.from(linearLayout);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                       break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission();
            }
        });

        listView.setNestedScrollingEnabled(true);
        arrayList=new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,R.layout.list_item,R.id.item,arrayList);
        listView.setAdapter(adapter);
        setListAdapter();
    }
    void setListAdapter(){

        if (tableList!=null){

            tableList.clear();
            arrayList.clear();
        }

        tableList=fetchScanData();

        for (int i=0;i<tableList.size();i++){

            arrayList.add(" ' "+tableList.get(i).getData()+" ' "+" on "+tableList.get(i).getTimeStamp());

        }

        adapter.notifyDataSetChanged();


    }
    void initScan(){

        qrScan = new IntentIntegrator(this);
        qrScan.setPrompt("Scan a bar code");
        qrScan.setBeepEnabled(true);
        qrScan.setCaptureActivity(CaptureActivityPortrait.class);
        qrScan.setOrientationLocked(true);
        qrScan.initiateScan();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){

            if (result.getContents() == null) {
                Toast.makeText(this, "Scan Unsuccessful", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                if (result.getContents().length()>10){

                    camBtn.setVisibility(View.GONE);
                    animationView.setVisibility(View.VISIBLE);

                    insertToDb(result.getContents());

                    new Handler().postDelayed(new Runnable() {  //Import android.os.handler
                        @Override
                        public void run() {
                            if (animationView.isAnimating()) {
                                // Do something.
                                animationView.clearAnimation();

                                camBtn.setVisibility(View.VISIBLE);
                                animationView.setVisibility(View.GONE);
                            }
                        }
                    }, 4000);

                }else {


                    camBtn.setVisibility(View.GONE);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.setAnimation(R.raw.failure);

                    new Handler().postDelayed(new Runnable() {  //Import android.os.handler
                        @Override
                        public void run() {
                            if (animationView.isAnimating()) {
                                // Do something.
                                animationView.clearAnimation();

                                camBtn.setVisibility(View.VISIBLE);
                                animationView.setVisibility(View.GONE);
                            }
                        }
                    }, 4000);

                }

                Log.d(TAG,"scanData "+result.getContents());
                Toast.makeText(this, "Result "+result.getContents(), Toast.LENGTH_LONG).show();
            }

        }else if (resultCode==RESULT_CANCELED){

            Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();

        }

    }

    private void checkLocationPermission(){
        // Here, thisActivity is the current activity
        if ( ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) +  ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"SHOW_PERM_LOC 4");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Log.d(TAG,"SHOW_PERM_LOC 5 ");
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Permission Denied");
                builder.setMessage("Without Location & Camera permissions the application will not be able to Start Ride. Are you sure you want to deny this permission?");
                builder.setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.setNegativeButton("Give Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA},
                                2020);
                    }
                });
                builder.show();

            } else {
                Log.d(TAG,"SHOW_PERM_LOC 6");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA},
                        2020);


            }
        }else{
            Log.d(TAG,"SHOW_PERM_LOC 8");
            initScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==2020){

            if (grantResults.length>0) {
                boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean locationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (locationPermission && cameraPermission){

                    initScan();
                    //Toast.makeText(this, "Location & camera permission granted", Toast.LENGTH_LONG).show();

                }else {

                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content), "Enable Location & Camera Permissions from settings", Snackbar.LENGTH_LONG)
                            .setTextColor(getResources().getColor(R.color.white))
                            .setBackgroundTint(getResources().getColor(R.color.teal_700))
                            .setActionTextColor(getResources().getColor(R.color.error))
                            .setAction("Enable", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                    snackbar.show();
                   // Toast.makeText(this, " Permissions denied", Toast.LENGTH_LONG).show();

                }

                isPermissionForAllGranted=true;

            } else {

                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }

        }

    }

    void insertToDb(String content){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy - h:mm:ss a", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());

        ScanTable table=new ScanTable();
        table.setData(content);
        table.setTimeStamp(currentDateAndTime);

        DatabaseReference.getDatabase(this).dockDao().insert(table);

        if (tableList!=null){

            tableList.clear();
        }
        setListAdapter();


    }

    private List<ScanTable> fetchScanData(){
        List<ScanTable> scanTableList = DatabaseReference.getDatabase(this).dockDao().getAll();
        return scanTableList;

    }

}