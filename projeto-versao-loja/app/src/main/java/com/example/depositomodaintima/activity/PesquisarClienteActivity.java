package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.adapter.AdapterClientes;
import com.example.depositomodaintima.adapter.AdapterEstoque;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Cliente;
import com.example.depositomodaintima.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisarClienteActivity extends AppCompatActivity {
    private SearchView pesquisaClientes;
    private RecyclerView recyclerPesquisaClientes;
    private List<Cliente> listaClientes;
    private DatabaseReference clientesRef;
    private AdapterClientes adapterPesquisaClientes;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar_cliente);

        pesquisaClientes = findViewById(R.id.idPesquisarCliente);
        recyclerPesquisaClientes = findViewById(R.id.idRecyclerPesquisaCliente);
        listaClientes = new ArrayList<>();
        clientesRef = ConfiguracaoFirebase.getFirebaseDatabase().child("clientes");

        adapterPesquisaClientes = new AdapterClientes(listaClientes,this);

        recyclerPesquisaClientes.setHasFixedSize(true);
        recyclerPesquisaClientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerPesquisaClientes.setAdapter(adapterPesquisaClientes);

        //Configurar o SearchView
        pesquisaClientes.setIconified(false);

        pesquisaClientes.setFocusable(true);
        pesquisaClientes.setQueryHint("Buscar Clientes");
        pesquisaClientes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisaClientes(textoDigitado);
                return true;
            }
        });

        recyclerPesquisaClientes.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerPesquisaClientes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Cliente clienteSelecionado = listaClientes.get(position);
                        Intent intent = new Intent(getApplicationContext(), NovaVendaActivity.class);
                        intent.putExtra("clienteSelecionado", clienteSelecionado);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {


                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    private void pesquisaClientes(String textoDigitado){
        listaClientes.clear();
        if(textoDigitado.length()>=2){
            Query query = clientesRef.orderByChild("nome").startAt(textoDigitado).endAt(textoDigitado + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaClientes.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){

                        listaClientes.add(ds.getValue(Cliente.class));

                    }

                    adapterPesquisaClientes.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }
}
