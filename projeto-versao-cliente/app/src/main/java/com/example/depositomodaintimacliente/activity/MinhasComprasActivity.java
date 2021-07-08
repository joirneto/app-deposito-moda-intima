package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.adapter.AdapterListaVendas;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.example.depositomodaintimacliente.helper.RecyclerItemClickListener;
import com.example.depositomodaintimacliente.model.Venda;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinhasComprasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewListaVendasMinhasCompras;
    private List<Venda> vendas = new ArrayList<>();
    private AdapterListaVendas adapterListaVendasMinhasCompras;
    private ProgressBar progressBarMinhasCompras;
    private DatabaseReference vendaMinhasComprasRef;
    private DatabaseReference vendaMinhasComprasVrfRef;
    private ValueEventListener valueEventListenerListarVendasMinhasCompras;
    private String contatoCliente;
    private BottomNavigationViewEx bottomNavigationViewExMinhasCompras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_compras);

        recyclerViewListaVendasMinhasCompras = findViewById(R.id.idRecyclerMinhasCompras);
        progressBarMinhasCompras = findViewById(R.id.idprogressBarMinhasCompras);
        bottomNavigationViewExMinhasCompras = findViewById(R.id.idBottomNavigationTabMinhasCompras);

        recupContatoCliente();

        //Configuração Buttom Navigation
        configuracaoBottomNavigation();



        getSupportActionBar().setTitle("Minhas Compras");




        vendaMinhasComprasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasClientes")
                .child(contatoCliente);

        adapterListaVendasMinhasCompras = new AdapterListaVendas(vendas,this);

        //Configurar Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewListaVendasMinhasCompras.setLayoutManager(layoutManager);
        recyclerViewListaVendasMinhasCompras.setHasFixedSize(true);
        recyclerViewListaVendasMinhasCompras.setAdapter(adapterListaVendasMinhasCompras);

        recyclerViewListaVendasMinhasCompras.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewListaVendasMinhasCompras,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Venda venda = vendas.get(position);
                        Intent intent = new Intent(getApplicationContext(), VendaDetalheActivity.class);
                        intent.putExtra("vendaSelecionada", venda);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(final View view, final int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }

    public void recupContatoCliente(){
        if (!recupTextoDadosCliente().isEmpty()){
            String dadosGerais = recupTextoDadosCliente();
            String[] dadosAux = dadosGerais.split("//");
            contatoCliente = dadosAux[1];
        }
    }

    private String recupTextoDadosCliente(){
        String resultado = "";

        try {
            InputStream arquivo = openFileInput("dadosCliente.txt");
            if(arquivo!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(arquivo);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String linha = "";
                while ((linha = bufferedReader.readLine())!=null){
                    resultado += linha;
                }

                arquivo.close();
            }

        }catch (IOException e){


        }

        return resultado;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    @Override
    public void onStop() {
        super.onStop();
        vendaMinhasComprasRef.removeEventListener(valueEventListenerListarVendasMinhasCompras);
    }

    private void recuperarProdutos(){

        valueEventListenerListarVendasMinhasCompras = vendaMinhasComprasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendas.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    vendas.add(ds.getValue(Venda.class));
                }

                Collections.reverse(vendas);
                adapterListaVendasMinhasCompras.notifyDataSetChanged();
                progressBarMinhasCompras.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Método para Configurar Bottom Navigation
    private void configuracaoBottomNavigation(){
        bottomNavigationViewExMinhasCompras.enableAnimation(false);
        bottomNavigationViewExMinhasCompras.enableItemShiftingMode(true);
        bottomNavigationViewExMinhasCompras.enableShiftingMode(false);
        bottomNavigationViewExMinhasCompras.setTextVisibility(true);
        habilitarNavegacaoCliente(bottomNavigationViewExMinhasCompras);

    }//fim configuracaoBottomNavigation

    //Método para Configurar Eventos de Click Bottom Navigation para Clientes
    private void habilitarNavegacaoCliente(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return  true;
                    case R.id.ic_minhas_compras:
                        startActivity(new Intent(getApplicationContext(),MinhasComprasActivity.class));
                        return  true;
                    case R.id.ic_menu:
                        startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacaoAdmin






}
