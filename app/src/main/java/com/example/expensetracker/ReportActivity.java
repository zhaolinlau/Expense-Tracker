package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.expensetracker.databinding.ActivityReportBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {
    ActivityReportBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    int sumExpense=0;
    int sumIncome=0;
    String type="";
    ArrayList<TransactionModel> transactionModelArrayList;
    TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        transactionModelArrayList=new ArrayList<>();
    }

    protected void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData() {
        firebaseFirestore.collection("Expenses").document(firebaseAuth.getUid()).collection("Notes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        transactionModelArrayList.clear();
                        sumExpense = 0;
                        sumIncome = 0;
                        for (DocumentSnapshot ds:task.getResult()) {
                            TransactionModel model=new TransactionModel(
                                    ds.getString("id"),
                                    ds.getString("note"),
                                    ds.getString("amount"),
                                    ds.getString("type"),
                                    ds.getString("date"));
                            int amount=Integer.parseInt(ds.getString("amount"));
                            if (ds.getString("type").equals("Expense")) {
                                sumExpense=sumExpense+amount;
                            } else {
                                sumIncome=sumIncome+amount;
                            }
                            transactionModelArrayList.add(model);
                        }
                        binding.tvCountIncome.setText(String.valueOf(sumIncome));
                        binding.tvCountExpenses.setText(String.valueOf(sumExpense));

                        transactionAdapter=new TransactionAdapter(ReportActivity.this,transactionModelArrayList);
                    }
                });
    }
}