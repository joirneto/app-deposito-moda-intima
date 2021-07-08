package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Cliente;
import com.example.depositomodaintima.model.Venda;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NovoClienteActivity extends AppCompatActivity {
    private EditText nomeNovoCliente, zapNovoCliente, cidadeNovoCliente, estadoNovoCliente;
    private Button botaoCadastrarNovoCliente;

    private DatabaseReference clienteRef;

    private List<Cliente> clientes;


    private Cliente cliente, clienteVerzap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_cliente);

        clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");

        clientes = new ArrayList<>();

        //Configuração do Toolbar
        getSupportActionBar().setTitle("Novo Cliente");

        //Inicializando Atributos
        initAtributesNovoCliente();

        cliente = (Cliente) getIntent().getSerializableExtra("clienteSelecionado");
        if(cliente!=null) {
            nomeNovoCliente.setText(cliente.getNome());
            zapNovoCliente.setText(cliente.getTelefone());
            estadoNovoCliente.setText(cliente.getEstado());
            cidadeNovoCliente.setText(cliente.getCidade());

        }else{
            cliente = new Cliente();
        }



        botaoCadastrarNovoCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarNovoCliente();
            }
        });
    }

    //Método para inicializar os atributos
    private void initAtributesNovoCliente(){
        nomeNovoCliente = findViewById(R.id.idNomeNovoCliente);
        zapNovoCliente = findViewById(R.id.idZapNovoCliente);
        estadoNovoCliente = findViewById(R.id.idEstadoNovoCliente);
        cidadeNovoCliente = findViewById(R.id.idCidadeNovoCliente);
        botaoCadastrarNovoCliente = findViewById(R.id.idBotaoNovoCliente);

        //Máscara para ZAP (NN) N NNNN - NNNN
        SimpleMaskFormatter simpleMaskZAP = new SimpleMaskFormatter("(NN) N NNNN - NNNN");
        MaskTextWatcher maskZAP = new MaskTextWatcher(zapNovoCliente,simpleMaskZAP);
        zapNovoCliente.addTextChangedListener(maskZAP);

    }//fim initAtributesNovoCliente()

    public boolean validarNovoCliente(){
        cliente.setNome(nomeNovoCliente.getText().toString().toUpperCase());
        cliente.setTelefone(zapNovoCliente.getText().toString());
        cliente.setCidade(cidadeNovoCliente.getText().toString().toUpperCase());
        cliente.setEstado(estadoNovoCliente.getText().toString().toUpperCase());

        if(!cliente.getNome().isEmpty()){
            if(!cliente.getTelefone().isEmpty() && (cliente.getTelefone().length() == 18)){
                if(!cliente.getCidade().isEmpty()){
                    if(!cliente.getEstado().isEmpty()){

                        cliente.salvar();
                        Toast.makeText(NovoClienteActivity.this,
                                "Cliente CADASTRADO com Sucesso!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), NovaVendaActivity.class));
                        finish();

                    }
                    else{
                        Toast.makeText(this,
                                "Digite o ESTADO de residencia do cliente!",
                                Toast.LENGTH_SHORT).show();

                    }

                }
                else{
                    Toast.makeText(this,
                            "Selecione a CIDADE de residendia do cliente!",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this,
                        "Digite um número de WhatApp com válido!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,
                    "Preencha o nome do cliente!",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void pesquisarZap(String zapDigitado){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }
}
