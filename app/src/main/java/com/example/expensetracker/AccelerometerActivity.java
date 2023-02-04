package com.example.expensetracker;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.expensetracker.databinding.ActivityAccelerometerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AccelerometerActivity extends AppCompatActivity {
    ActivityAccelerometerBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SensorManager sm = null;
    TextView accelerometerView = null;
    List list;

    private SensorEventListener AccelerometerEventListener;
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccelerometerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        root = findViewById(R.id.accelerometer);

        AccelerometerEventListener = new SensorEventListener(){
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                accelerometerView.setText("x: "+values[0]+"\ny: "+values[1]+"\nz: "+values[2]);
            }
        };

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);

        accelerometerView = (TextView)findViewById(R.id.accelerometerView);

        list = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(list.size()>0){
            sm.registerListener(AccelerometerEventListener, (Sensor) list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast.makeText(getBaseContext(), "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        if(list.size()>0){
            sm.unregisterListener(AccelerometerEventListener);
        }
        super.onStop();
    }
}