package com.idnp.lab10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    double latitud,longitud;
    TextView txtLatitud,txtLongitud;
    LocationManager locationManager;
    Button btn_map_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLatitud = findViewById(R.id.txtValorLatitud);
        txtLongitud= findViewById(R.id.txtValorLongitud);

        btn_map_gps=findViewById(R.id.btn_mapa);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        Log.d("Permiso","Entre a permisos");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        btn_map_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MapsGPS.class));
            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();

        txtLongitud.setText("" + longitud);
        txtLatitud.setText(""+latitud);

        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}