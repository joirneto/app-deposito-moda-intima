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

public class TelaMensagemUsuarioActivity extends AppCompatActivity {

    private TextView mensagem;
    private DatabaseReference mensagemRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_mensagem_usuario);

        mensagem= findViewById(R.id.idMensagemVerificacao);

        mensagemRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("versao0001")
                .child("mensagem");
        mensagemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux = dataSnapshot.getValue(String.class);
                mensagem.setText(aux);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
