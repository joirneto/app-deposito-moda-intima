package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.adapter.AdapterClientes;
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
import java.util.Collections;
import java.util.List;

public class ListarClientesActivity extends AppCompatActivity {
    private SearchView pesquisaClientes;
    private RecyclerView recyclerPesquisaClientes;
    private List<Cliente> listaClientes;
    private DatabaseReference clientesRef;
    private AdapterClientes adapterPesquisaClientes;
    private AlertDialog.Builder dialog;

    private ValueEventListener valueEventListenerClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_clientes);

        pesquisaClientes = findViewById(R.id.idPesquisarClientes);
        recyclerPesquisaClientes = findViewById(R.id.idRecyclerClientes);
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

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {

                        dialog =new AlertDialog.Builder(ListarClientesActivity.this);
                        dialog.setTitle("EDITAR DADOS DO CLIENTE");
                        dialog.setMessage("Tem certeza que deseja EDITAR os dados do cliente?");

                        //Para impedir que a dialog feche se clicar fora dele
                        dialog.setCancelable(false);

                        //Para inserir incone
                        dialog.setIcon(android.R.drawable.ic_delete);

                        dialog.setNegativeButton("Não",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                        dialog.setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Cliente clienteSelecionado = listaClientes.get(position);
                                        Intent intent = new Intent(getApplicationContext(), NovoClienteActivity.class);
                                        intent.putExtra("clienteSelecionado", clienteSelecionado);
                                        startActivity(intent);

                                    }
                                });


                        dialog.create();
                        dialog.show();


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

    private void recuperarClientes(){

        valueEventListenerClientes = clientesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaClientes.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    listaClientes.add(ds.getValue(Cliente.class));
                }

                Collections.reverse(listaClientes);
                adapterPesquisaClientes.notifyDataSetChanged();

        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarClientes();
    }

    @Override
    public void onStop() {
        super.onStop();
        clientesRef.removeEventListener(valueEventListenerClientes);
    }
}
