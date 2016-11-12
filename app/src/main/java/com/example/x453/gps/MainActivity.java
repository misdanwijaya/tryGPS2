package com.example.x453.gps;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //variabel untuk diakses disemua element
    private static final int MY_PERMISSIONS_REQUEST = 99;//int bebas, maks 1 byte
    GoogleApiClient mGoogleApiClient ;
    //Location mLastLocation;
    LocationRequest mLocationRequest;
    TextView mLatText;
    TextView mLongText;
    TextView lokasi;

    //menginisasi objek GoogleApiClient
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void ambilLokasi() {
   /* mulai Android 6 (API 23), pemberian persmission
    dilakukan secara dinamik (tdk diawal)
    untuk jenis2 persmisson tertentu, termasuk lokasi
    */

        // cek apakah sudah diijinkan oleh user, jika belum tampilkan dialog
        if (ActivityCompat.checkSelfPermission (this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }
        //set agar setiap update lokasi maka UI bisa diupdate
        //setiap ada update maka onLocationChanged akan dipanggil
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    //jika terkoneksi
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ambilLokasi();
    }

    //meminta ijin akses
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ambilLokasi();
            } else {
                //permssion tidak diberikan, tampilkan pesan
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setMessage("Tidak mendapat ijin, tidak dapat mengambil lokasi");
                ad.show();
            }
            return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //ketika aplikasi dijalankan
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    //ketika aplikasi dimatikan
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //tampilkan hasil///
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //******************** tambahan mulai dari sini
        mLatText =  (TextView) findViewById(R.id.tvLat);
        mLongText =  (TextView) findViewById(R.id.tvLong);
        lokasi = (TextView) findViewById(R.id.tvLokasi);
        buildGoogleApiClient();
        createLocationRequest(); //<----- tambah

    }

    //mengatur waktu perubahan
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        //10 detik sekali minta lokasi (10000ms = 10 detik)
        mLocationRequest.setInterval(100000);
        //tapi tidak boleh lebih cepat dari 5 detik
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //untuk menampilakn perubahan
    @Override
    public void onLocationChanged(Location location) {
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setMessage("update lokasi");
        ad.show();
        mLatText.setText("Lat.:"+String.valueOf(location.getLatitude()));
        mLongText.setText("Lon.:" + String.valueOf(location.getLongitude()));


        if(((location.getLatitude()) <  -6.9510512 && (location.getLatitude()) >= -6.9545351) &&
                ((location.getLongitude()) >= 107.6658616 && (location.getLongitude()) < 107.6688085)){
            lokasi.setText("Anda Berada di Rumah");
        }
        else{
            lokasi.setText("Anda Berada diluar rumah");
        }

    }


}
