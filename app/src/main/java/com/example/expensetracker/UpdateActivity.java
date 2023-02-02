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

import com.example.expensetracker.databinding.ActivityUpdateBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateActivity extends AppCompatActivity {
    ActivityUpdateBinding binding;
    String newType;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private View root;
    private float maxValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        root = findViewById(R.id.update);
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

        String id=getIntent().getStringExtra("id");
        String amount=getIntent().getStringExtra("amount");
        String note=getIntent().getStringExtra("note");
        String type=getIntent().getStringExtra("type");

        binding.userAmountAdd.setText(amount);
        binding.userNoteAdd.setText(note);

        switch (type) {
            case "Income":
                newType="Income";
                binding.incomeRadioBtn.setChecked(true);
                break;
            case "Expense":
                newType="Expense";
                binding.expenseRadioBtn.setChecked(true);
                break;
        }
        binding.incomeRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newType="Income";
                binding.incomeRadioBtn.setChecked(true);
                binding.expenseRadioBtn.setChecked(false);
            }
        });
        binding.expenseRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newType="Expense";
                binding.incomeRadioBtn.setChecked(false);
                binding.expenseRadioBtn.setChecked(true);
            }
        });
        binding.btnUpdateTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String amount=binding.userAmountAdd.getText().toString();
                String note=binding.userNoteAdd.getText().toString();

                firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid())
                        .collection("Notes").document(id)
                        .update("amount",amount,"note",note,"type",newType)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                                Toast toast = Toast.makeText(UpdateActivity.this, "Transaction has been updated", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.btnDeleteTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid())
                        .collection("Notes")
                        .document(id).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                onBackPressed();
                                Toast toast = Toast.makeText(UpdateActivity.this, "Transaction has been deleted", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast = Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
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