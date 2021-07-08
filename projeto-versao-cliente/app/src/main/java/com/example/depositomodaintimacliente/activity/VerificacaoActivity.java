package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class VerificacaoActivity extends AppCompatActivity {
    private DatabaseReference ativadoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacao);

        ativadoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("versao0001")
                .child("ativado");
        ativadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux = dataSnapshot.getValue(String.class);
                if(aux.equals("SIM")){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }
                else{

                    startActivity(new Intent(getApplicationContext(),TelaMensagemUsuarioActivity.class));
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
