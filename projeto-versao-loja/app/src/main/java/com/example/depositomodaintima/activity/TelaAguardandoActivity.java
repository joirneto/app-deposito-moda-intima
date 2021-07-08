package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class TelaAguardandoActivity extends AppCompatActivity {
    private DatabaseReference ativacaoRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_aguardando);
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        if(auth.getCurrentUser()!=null){
            ativacaoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(ConfiguracaoFirebase.getIdUsuario());
            ativacaoRef.child("ativado").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String atv;
                    atv = String.valueOf(dataSnapshot.getValue());

                    if (atv.equals("SIM")){
                        startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                        finish();
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(), TelaAtivacaoUserActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else{
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }
}
