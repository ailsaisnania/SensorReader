package com.example.sensorreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener { //untuk implement sensor
    private SensorManager mSensorManager; //inisialisasi sensor manager agar bisa digunakan
    private Sensor mSensorAccelerometer; //inisialisasi untuk sensor accelerometer
    //sensor accelerometer berfungsi untuk mengukur percepatan dan membaca kemiringan hp
    //dapat digunakan untuk menghitung jumlah langkah, mengatur orientasi layar (landscape/portrait), dsb
    private Sensor mSensorMagnetoMeter; //bekerja atas dasar pendeteksian gaya magnet bumi.
    //contohnya untuk menentukan arah mata angin.


    //inisialisasi untuk text view
    private TextView mTextSensorAzimuth;
    private TextView mTextSensorPitch;
    private TextView mTextSensorRoll;


    private float[] mAccelerometerData = new float[4]; //ini menentukan jumlah dotnya
    private float[] mMagnetometerData = new float[4];

    private static final float VALUE_DRIFT = 0.05f; //buat acuannya
    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotRight;
    private ImageView mSpotLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //orientation di activity main
        mTextSensorPitch = findViewById(R.id.value_pitch); //menentukan value untuk variabel ini yang akan ditampilkan di layout
        mTextSensorAzimuth = findViewById(R.id.value_azimuth);
        mTextSensorRoll = findViewById(R.id.value_roll);

        mSensorManager =(SensorManager) getSystemService(Context.SENSOR_SERVICE); //mengambil service sensor
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //membuat variabel mSensorAccelerometer
        //punya sensor accelerometer
        mSensorMagnetoMeter = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSpotTop = findViewById(R.id.spot_top); //identifikasi letak spot di layar
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotRight = findViewById(R.id.spot_right);
        mSpotLeft = findViewById(R.id.spot_left);
    }

    protected void onStart(){
        super.onStart();

        if (mSensorAccelerometer != null){ //jika ada deteksi dari variabel ini, ubah value nya ke sensor accelerometer
            mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager
                    .SENSOR_DELAY_NORMAL);
        }

        if (mSensorMagnetoMeter != null){ //jika ada deteksi dari variabel ini, ubah value nya ke sensor magnetometer
            mSensorManager.registerListener(this, mSensorMagnetoMeter, SensorManager
                    .SENSOR_DELAY_NORMAL);}
    }

    protected void onStop(){
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType(); //inisialisasi sensor
        switch (sensorType){
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone(); //mengembalikan nilai true atau keluaran yang dihasilkan
                // sama dengan nilai yang sebelum dilakukan clone
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;

            default:
                return;

        }

        float[] rotationMatrix = new float[9]; //inisialisasi rotasi
        //deteksi perubahan rotasi
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerData, mMagnetometerData);
        float orientationValues[] = new float[3];
        if (rotationOK){ //jika ada rotasi pada hp,
            SensorManager.getOrientation(rotationMatrix, orientationValues); //simpan rotation matrix dan orientation value
        }

        float azimuth = orientationValues[0]; //tampilkan valuenya
        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        mTextSensorRoll.setText(getResources().getString(R.string.value_format, roll)); //set text untuk labelmya
        mTextSensorPitch.setText(getResources().getString(R.string.value_format, pitch));
        mTextSensorAzimuth.setText(getResources().getString(R.string.value_format, azimuth));

        if (Math.abs(pitch) < VALUE_DRIFT){ //jika value derajat rotasi (sumbu x) kurang dari value drift yg ditetntukan
            pitch = 0; //value pitch = 0
        }

        if (Math.abs(roll) < VALUE_DRIFT){  //jika value derajat rotasi (sumbu y) kurang dari value drift yg ditetntukan
            roll = 0;
        }

        mSpotTop.setAlpha(0f); //mengatur posisi spot saat perangkat digerakan
        mSpotRight.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotBottom.setAlpha(0f);
        //mengatur posisi spot saat perangkat digerakan
        if (pitch > 0){
            mSpotBottom.setAlpha(pitch);

        }
        else {
            mSpotTop.setAlpha(Math.abs(pitch));
        }

        if (roll > 0){
            mSpotLeft.setAlpha(roll);

        }
        else {
            mSpotRight.setAlpha(Math.abs(roll));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {


    }
}