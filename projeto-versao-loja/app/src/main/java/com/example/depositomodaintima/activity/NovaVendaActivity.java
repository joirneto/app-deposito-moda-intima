package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Cliente;
import com.example.depositomodaintima.model.Produto;
import com.example.depositomodaintima.model.Venda;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class NovaVendaActivity extends AppCompatActivity {
    private TextView nomeCliente;
    private Button botaoIniciarVenda;

    private DatabaseReference clienteRef;
    private DatabaseReference vendendoRef;
    private DatabaseReference vendedorRef;
    private DatabaseReference clienteIdRef;

    private String idUsuario;

    private Venda venda;

    private Cliente cliente;

    private String zapDigitado, clienteId;

    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_venda);

        getSupportActionBar().setTitle("Cadastrando NOVA VENDA");

        //Tela de alerta enquanto procura o cliente
        dialog = new SpotsDialog.Builder().
                setContext(this).
                setMessage("Bucando Cliente").
                setCancelable(false).build();

        //Inicializar venda
        venda = new Venda();

         //Iniciando os Atributos
        initAtributesCliente();

        cliente = (Cliente) getIntent().getSerializableExtra("clienteSelecionado");
        if(cliente!=null){
            nomeCliente.setText(cliente.getNome());
        }

        //Inicializando a referencia DataBase
        clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");


        botaoIniciarVenda.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String textoNomeCliente = nomeCliente.getText().toString();

                if(!textoNomeCliente.isEmpty()) {
                    iniciarVenda(venda);
                    vendendoSim();
                    startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                    finish();

                }
                else{
                    Toast.makeText(NovaVendaActivity.this,
                            "Identifique o Cliente",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    //Método para abrir cadastrar novo cliente
    public void abrirNovoCliente(View view){
        startActivity(new Intent(NovaVendaActivity.this,NovoClienteActivity.class));
    }//fim abrirNovoCliente

    //Método para iniciar os Atributos
    public void initAtributesCliente(){
        botaoIniciarVenda = findViewById(R.id.idBotaoIniciarVenda);
        nomeCliente = findViewById(R.id.idNomeClienteNovaVenda);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void iniciarVenda(final Venda venda){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate dataAtual;
            dataAtual = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
            String dataFormatada = formatter.format(dataAtual);
            venda.setData(dataFormatada);
        }
        venda.setCliente(nomeCliente.getText().toString());
        venda.setVendedorId(ConfiguracaoFirebase.getIdUsuario());
        venda.setTotalValor("0");
        venda.setTotalItens("0");

        venda.setClienteId(clienteId);

        vendedorRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(ConfiguracaoFirebase.getIdUsuario())
                .child("nome");

        vendedorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                venda.setVendedorNome(dataSnapshot.getValue(String.class));
                venda.salvar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void vendendoSim(){
        vendendoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario());
        vendendoRef.child("vendendo").setValue("SIM");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }

    public void pesquisarCliente(View view){
        startActivity(new Intent(getApplicationContext(),PesquisarClienteActivity.class));

    }


}
