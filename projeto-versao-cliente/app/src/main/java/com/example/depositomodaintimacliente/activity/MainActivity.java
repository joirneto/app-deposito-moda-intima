package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.adapter.AdapterEstoque;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.example.depositomodaintimacliente.helper.RecyclerItemClickListener;
import com.example.depositomodaintimacliente.model.Produto;
import com.example.depositomodaintimacliente.model.Venda;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbarTabs;
    private TextView carrinhoValorTotal, carrinhoItensTotal;
    private FirebaseAuth autenticacao;
    private LinearLayout carrinho;
    private DatabaseReference clienteComprando;
    private Venda venda;
    private BottomNavigationViewEx bottomNavigationViewExCliente;

    private RecyclerView recyclerViewMain;
    private AdapterEstoque adapterEstoqueMain;
    private List<Produto> produtos = new ArrayList<>();
    private List<Produto> produtosAux = new ArrayList<>();
    private List<Venda> vendas = new ArrayList<>();
    private DatabaseReference estoqueMain;
    private ValueEventListener valueEventListenerMain;
    private ProgressBar progressBarMain;
    private DatabaseReference vendaRef;
    private DatabaseReference vendaPRef;
    private AlertDialog.Builder dialog;
    private String idVenda;
    private List<String> aux = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Verificando se existe venda ativa
        if(recupTexto().isEmpty()){
            iniciarVenda();
        }

        //RECUPERANDO DADOS DE VENDA
        vendaPRef = ConfiguracaoFirebase.getFirebaseDatabase().child("vendasClientesGeral")
                .child(recupTexto());
        vendaPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                venda = dataSnapshot.getValue(Venda.class);

                //Configurações Tela
                carrinhoItensTotal.setText(venda.getTotalItens());
                carrinhoValorTotal.setText(venda.getTotalValor());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Inicializando componentes
        initAtributesMain();

        //InitVerificação
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");

        //Configurando a Toolbar
        toolbarTabs.setTitle("DepositoModaIntima");
        toolbarTabs.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        toolbarTabs.setNavigationIcon(R.drawable.logo3);
        setSupportActionBar(toolbarTabs);

        //Configuração Buttom Navigation
        configuracaoBottomNavigation();

//-------------------------Configurando o RecyclerView -------------------------------

        progressBarMain = findViewById(R.id.idprogressBarMain);

        //Configurações iniciais
        recyclerViewMain = findViewById(R.id.idRecyclerMain);

        //Configurando o Adaper
        adapterEstoqueMain = new AdapterEstoque(produtos,this);

        //Configurar Recycler
       // RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerViewMain.setLayoutManager(layoutManager);
        recyclerViewMain.setHasFixedSize(true);
        recyclerViewMain.setAdapter(adapterEstoqueMain);

        recyclerViewMain.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewMain,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View view, final int position) {
                        Produto produtoSelcionado = produtos.get(position);
                        Intent intent = new Intent(MainActivity.this, ProdutoDetalhesActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelcionado);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) { }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                }
        ));

//-------------------------FIM Configurando o RecyclerView -------------------------------

    }//FIM onCreate

    //Método para inicializar os atributos
    public void initAtributesMain(){
        carrinhoItensTotal = findViewById(R.id.idTotalItensCarrinhoMain);
        carrinhoValorTotal = findViewById(R.id.idValorTotalCarrinhoMain);
        toolbarTabs = findViewById(R.id.idToolbarTabs);
        carrinho = findViewById(R.id.idCarrinho);
        bottomNavigationViewExCliente = findViewById(R.id.idBottomNavigationTabClientes);
    }//FIM initAtributesMain()

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_pequisar:
                startActivity(new Intent(getApplicationContext(),PesquisarProdutosActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Método para Configurar Bottom Navigation
    private void configuracaoBottomNavigation(){
        bottomNavigationViewExCliente.enableAnimation(false);
        bottomNavigationViewExCliente.enableItemShiftingMode(true);
        bottomNavigationViewExCliente.enableShiftingMode(false);
        bottomNavigationViewExCliente.setTextVisibility(true);
        habilitarNavegacaoCliente(bottomNavigationViewExCliente);

    }//fim configuracaoBottomNavigation

    //Método para Configurar Eventos de Click Bottom Navigation para Clientes
    private void habilitarNavegacaoCliente(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                    //    startActivity(new Intent(Main2Activity.this,NovaVendaActivity.class));
                        return  true;
                    case R.id.ic_minhas_compras:
                        if(recupTextoDadosClienteMain().isEmpty()){
                            Toast.makeText(MainActivity.this,
                                    "Sem Histórico de Compras",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startActivity(new Intent(MainActivity.this,MinhasComprasActivity.class));

                        }

                        return  true;
                    case R.id.ic_menu:
                        startActivity(new Intent(MainActivity.this,MenuActivity.class));
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacaoAdmin

    public void abrirCarrinhoMain(View view){
        startActivity(new Intent(MainActivity.this,CarrinhoActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarProdutos();

    }

    @Override
    public void onStop() {
        super.onStop();
        estoqueMain.removeEventListener(valueEventListenerMain);
    }

    private void recuperarProdutos(){
        initAux();
        produtos.clear();
        produtosAux.clear();
           valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            produtosAux.add(ds.getValue(Produto.class));
                        }

                        Log.i("MMNN: ", String.valueOf(produtosAux.size()));
                        //verificar possivel erro
                        for(int i = 0; i<12;i++){
                            for(int j=0;j<produtosAux.size();j++){
                                if(produtosAux.get(j).getCategoria().equals(aux.get(i))){
                                    produtos.add(produtosAux.get(j));
                                }
                            }
                        }
                        adapterEstoqueMain.notifyDataSetChanged();
                        progressBarMain.setVisibility(View.GONE);
                      //  recuperarProdutos1();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }



  /* private void recuperarProdutos(){
        produtos.clear();
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Calcinha Adulto");

           valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            produtos.add(ds.getValue(Produto.class));
                        }
                        adapterEstoqueMain.notifyDataSetChanged();
                        recuperarProdutos1();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }*/

  /*  private void recuperarProdutos1(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Calcinha Infantil");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos2(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Sutiã");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos3();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos3(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Cueca Adulto");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos4();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos4(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Cueca Infantil");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos5();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos5(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Babydool");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos6();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos6(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Camisola");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos7();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos7(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Conjuntos");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }
                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos8();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos8(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Meia Masculina");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos9();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos9(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Short");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos10();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos10(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Top Adulto");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                adapterEstoqueMain.notifyDataSetChanged();
                recuperarProdutos11();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void recuperarProdutos11(){
        estoqueMain = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Top Infantil");

        valueEventListenerMain = estoqueMain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                progressBarMain.setVisibility(View.GONE);
                // Collections.reverse(produtos);
                adapterEstoqueMain.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }*/

    public void initAux(){
        aux.add("Calcinha Adulto");
        aux.add("Calcinha Infantil");
        aux.add("Sutiã");
        aux.add("Cueca Adulto");
        aux.add("Cueca Infantil");
        aux.add("Babydool");
        aux.add("Camisola");
        aux.add("Conjuntos");
        aux.add("Meia Masculina");
        aux.add("Short");
        aux.add("Top Adulto");
        aux.add("Top Infantil");
    }

    private void gravarArquivoVenda(String idVenda){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("IDVenda.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(idVenda);
            outputStreamWriter.close();
        }catch (IOException e){
            Log.v("MainActivity",e.toString());
        }
    }

    private String recupTexto(){
        String resultado = "";

        try {
            InputStream arquivo = openFileInput("IDVenda.txt");
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
            Log.v("MainActivity",e.toString());

        }

        return resultado;
    }

    public void iniciarVenda(){
        venda = new Venda();
        venda.setCliente("anonimo");
        venda.setVendedorId("VENDA EXTERNA");
        venda.setTotalValor("0");
        venda.setTotalItens("0");
        venda.setClienteId("anonimo");
        venda.setVendedorNome("VENDA EXTERNA");


        Date data = new Date();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        venda.setData(formatador.format(data));

        venda.salvar();
        gravarArquivoVenda(venda.getIdVenda());
    }
    private String recupTextoDadosClienteMain(){
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
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}


