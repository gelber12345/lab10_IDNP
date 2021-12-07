package com.idnp.lab10;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.idnp.lab10.databinding.ActivityMapsGpsBinding;

import org.w3c.dom.Text;

public class MapsGPS extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 102;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback mlocationCallback;

    private LocationSettingsRequest.Builder builder;

    public LocationRequest mLocationRequest;

    Marker marker;

    private GoogleMap mMap;
    private ActivityMapsGpsBinding binding;

    TextView txt_valor_la, txt_valor_lo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsGpsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txt_valor_la=findViewById(R.id.txtValorLatitud);
        txt_valor_lo=findViewById(R.id.txtValorLongitud);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerUltimaUbicacion();
        mlocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    agregarMarcador(location.getLatitude(),location.getLongitude());
                }

            };
        };
        mLocationRequest = createLocationRequest();
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        checkLocationSetting(builder);
    }

    private void agregarMarcador(double lat, double lng) {

        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 19);
        if (marker != null) marker.remove();
        marker = mMap.addMarker(new MarkerOptions()
                .position(coordenadas)
                .title("Mi posicion"));
        mMap.animateCamera(miUbicacion);
        txt_valor_la.setText(""+lat);
        txt_valor_lo.setText(""+lng);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private void obtenerUltimaUbicacion() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                dialogoSolicitarPermisoGPS();
            }
        }
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setSmallestDisplacement(30);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    private void checkLocationSetting(LocationSettingsRequest.Builder builder) {

        builder.setAlwaysShow(true);
        SettingsClient cliente = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = cliente.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                iniciarActualizacionesUbicacion();

            }
        });
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {

            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                resolvable.startResolutionForResult(
                                        MapsGPS.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                            } catch (ClassCastException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    public void iniciarActualizacionesUbicacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        fusedLocationClient.requestLocationUpdates(mLocationRequest,
                mlocationCallback,
                null /* Looper */);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                iniciarActualizacionesUbicacion();
            } else {
                checkLocationSetting(builder);
            }
        }
    }
    private void dialogoSolicitarPermisoGPS(){
        if (ActivityCompat.checkSelfPermission(MapsGPS.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsGPS.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsGPS.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }
}