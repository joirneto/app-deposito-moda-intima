package com.example.depositomodaintima.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Venda;
import com.google.firebase.database.DatabaseReference;

public class RegistrarEnderecoActivity extends AppCompatActivity {
    private Button registrarEndereco;
    private TextView observacoes;
    private Venda vendaAux;
    private String endereco;
    private DatabaseReference enderecoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_endereco);
        initAtributesEndereco();


        vendaAux = (Venda) getIntent().getSerializableExtra("vendaSelecionada");
       if(!vendaAux.getEnderecoEntrega().equals("")) {
           observacoes.setText(vendaAux.getEnderecoEntrega());
       }

    }

    public void initAtributesEndereco() {
        registrarEndereco = findViewById(R.id.idBotaoGravarEndereco);
        observacoes = findViewById(R.id.idEnderecoVendaRealizadaOBS);

    }

    public void gravarEnd(View view) {
        endereco = observacoes.getText().toString();

        enderecoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child(vendaAux.getStatusVenda())
                .child(vendaAux.getIdVenda())
                .child("enderecoEntrega");
        enderecoRef.setValue(endereco);
        Toast.makeText(this,
                "ENDEREÇO REGISTRADO",
                Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), ListarVendasActivity.class));
        finish();

    }

}
