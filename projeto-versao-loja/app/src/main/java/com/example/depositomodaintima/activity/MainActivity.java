package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.adapter.AdapterEstoque;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Produto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference estoqueRef;

    private RecyclerView recyclerEstoque;
    private Toolbar toolbar;
    private List<Produto> produtos = new ArrayList<>();

    private AdapterEstoque adapterEstoque;
    private AlertDialog dialog;

    //ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");

        //Inicializando componentes
        initAtributesMain();

        //Configurando a Toolbar

        toolbar.setTitle("Depósito Moda Intima");
        toolbar.setNavigationIcon(R.drawable.logo3);
        setSupportActionBar(toolbar);



        //Configuração Buttom Navigation
        configuracaoBottomNavigation();

        /*//Configuração para iniciar no Fragment Home
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new HomeFragment()).commit();*/

        //Configurara Recycler
        recyclerEstoque.setLayoutManager(new LinearLayoutManager(this));
        recyclerEstoque.setHasFixedSize(true);
        adapterEstoque = new AdapterEstoque(produtos,this);
        recyclerEstoque.setAdapter(adapterEstoque);

        recuperarProdutos();

        recyclerEstoque.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerEstoque,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produtoSelcionado = produtos.get(position);
                        Intent intent = new Intent(getApplicationContext(),ProdutoDetalhesActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelcionado);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
            case R.id.menu_novo_produto:
                startActivity(new Intent(getApplicationContext(),NovoProdutoActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Método para Configurar Bottom Navigation
    private void configuracaoBottomNavigation(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //Configuração
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);

        habilitarNavegacao(bottomNavigationViewEx);
    }//fim configuracaoBottomNavigation

    //Método para Configurar Eventos de Click Bottom Navigation
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                /*FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                */

                switch (item.getItemId()){
                   /* case R.id.ic_home:
                        //fragmentTransaction.replace(R.id.viewPager, new HomeFragment()).commit();
                        return  true;
                    case R.id.ic_pesquisa:
                        //fragmentTransaction.replace(R.id.viewPager, new PesquisarFragment()).commit();
                        return  true;
                    case R.id.ic_add_cliente:
                        //fragmentTransaction.replace(R.id.viewPager, new CadastroNovoClienteFragment()).commit();
                        return  true;*/
                    case R.id.ic_nova_venda:

                        //fragmentTransaction.replace(R.id.viewPager, new NovaVendaFragment()).commit();
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacao

    //MÉTODO PARA INICIALIZAR OS ATRIBUTOS
    public void initAtributesMain(){
        recyclerEstoque = findViewById(R.id.RecyclerEstoque);
        toolbar = findViewById(R.id.toolbarPrincipal);

    }//fim initAtributesMain()

    private void recuperarProdutos(){

        //Tela de alerta enquanto o sistema salva o produto
        dialog = new SpotsDialog.Builder().
                setContext(this).
                setMessage("Carregando Produtos...").
                setCancelable(false).build();
        dialog.show();

        estoqueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                Collections.reverse(produtos);
                adapterEstoque.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
