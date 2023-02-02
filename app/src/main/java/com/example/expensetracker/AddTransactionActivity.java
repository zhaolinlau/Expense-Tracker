package com.example.expensetracker;

import static android.hardware.Sensor.TYPE_LIGHT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.expensetracker.databinding.ActivityAddTransactionBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class AddTransactionActivity extends AppCompatActivity {
    ActivityAddTransactionBinding binding;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String type="";

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private View root;
    private float maxValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        root = findViewById(R.id.addtransaction);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(TYPE_LIGHT);

        if (lightSensor == null){
            Toast.makeText(this, "The device has no light sensor :(", Toast.LENGTH_SHORT).show();
            finish();
        }

        maxValue = lightSensor.getMaximumRange();

        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float value = sensorEvent.values[0];
                getSupportActionBar().setTitle("Luminosity : " + value + "lx");
                int newValue = (int) (255f * value / maxValue);
                root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        binding.expenseRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Expense";
                binding.expenseRadioBtn.setChecked(true);
                binding.incomeRadioBtn.setChecked(false);
            }
        });

        binding.incomeRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type="Income";
                binding.expenseRadioBtn.setChecked(false);
                binding.incomeRadioBtn.setChecked(true);
            }
        });

        binding.btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=binding.userAmountAdd.getText().toString().trim();
                String note=binding.userNoteAdd.getText().toString().trim();
                if (amount.length()<=0) {
                    return;
                }
                if (type.length()<=0) {
                    Toast toast = Toast.makeText(AddTransactionActivity.this,"Select transaction type",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();

                }

                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a", Locale.getDefault());
                TimeZone tz = TimeZone.getTimeZone("Asia/Kuala_Lumpur");
                sdf.setTimeZone(tz);
                String currentDateAndTime = sdf.format(new Date());


                String id= UUID.randomUUID().toString();
                Map<String,Object> transaction=new HashMap<>();
                transaction.put("id",id);
                transaction.put("amount",amount);
                transaction.put("note",note);
                transaction.put("type",type);
                transaction.put("date",currentDateAndTime);

                firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes").document(id)
                        .set(transaction)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast toast = Toast.makeText(AddTransactionActivity.this,"New transaction has been added",Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                binding.userNoteAdd.setText("");
                                binding.userAmountAdd.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast = Toast.makeText(AddTransactionActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
            }
        });
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
    }

}