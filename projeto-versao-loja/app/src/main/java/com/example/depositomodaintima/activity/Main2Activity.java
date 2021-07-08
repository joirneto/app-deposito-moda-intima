package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.fragment.BabydoolFragment;
import com.example.depositomodaintima.fragment.CalcinhaAdultoFragment;
import com.example.depositomodaintima.fragment.CalcinhaInfantilFragment;
import com.example.depositomodaintima.fragment.CamisolaFragment;
import com.example.depositomodaintima.fragment.ConjuntosFragment;
import com.example.depositomodaintima.fragment.CuecaAdultoFragment;
import com.example.depositomodaintima.fragment.CuecaInfantilFragment;
import com.example.depositomodaintima.fragment.MeiaMasculinaFragment;
import com.example.depositomodaintima.fragment.ShortFragment;
import com.example.depositomodaintima.fragment.SutiaFragment;
import com.example.depositomodaintima.fragment.TopAdultoFragment;
import com.example.depositomodaintima.fragment.TopInfantilFragment;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Venda;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class Main2Activity extends AppCompatActivity {
    private Toolbar toolbarTabs;
    private TextView carrinhoValorTotal, carrinhoItensTotal;
    private FirebaseAuth autenticacao;
    private LinearLayout carrinho;
    private DatabaseReference vendaOn, adminRef;
    private Venda venda;
    private BottomNavigationViewEx bottomNavigationViewExAdmin, bottomNavigationViewExUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //Inicializando componentes
        initAtributesMain2();


        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        verificarUsuarioLogado();


        //Configurando a Toolbar
        toolbarTabs.setTitle("DepositoModaIntima");
        toolbarTabs.setNavigationIcon(R.drawable.logo3);
        setSupportActionBar(toolbarTabs);

        //Configuração Buttom Navigation
        configuracaoBottomNavigation();

        //Configurando abas
        FragmentPagerItemAdapter   adapter;
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("CALCINHA ADULTO", CalcinhaAdultoFragment.class)
                        .add("CALCINHA INFANTIL", CalcinhaInfantilFragment.class)
                        .add("SUTIÃ", SutiaFragment.class)
                        .add("CUECA ADULTO", CuecaAdultoFragment.class)
                        .add("CUECA INFANTIL", CuecaInfantilFragment.class)
                        .add("BABYDOOL", BabydoolFragment.class)
                        .add("CAMISOLA", CamisolaFragment.class)
                        .add("CONJUNTOS", ConjuntosFragment.class)
                        .add("MEIA MASCULINA", MeiaMasculinaFragment.class)
                        .add("SHORT", ShortFragment.class)
                        .add("TOP ADULTO", TopAdultoFragment.class)
                        .add("TOP INFANTIL", TopInfantilFragment.class)
                        .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab= findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        vendaOn = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario())
                .child("vendendo");

        vendaOn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String status;
                status = dataSnapshot.getValue().toString();
                if(status.equals("SIM")){

                    carrinho.setVisibility(View.VISIBLE);
                    bottomNavigationViewExAdmin.setVisibility(View.GONE);
                    bottomNavigationViewExUser.setVisibility(View.GONE);

                }else{

                    carrinho.setVisibility(View.GONE);

                    adminRef = ConfiguracaoFirebase.getFirebaseDatabase()
                            .child("usuarios")
                            .child(ConfiguracaoFirebase.getIdUsuario());

                    adminRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String aux;
                            aux = dataSnapshot.getValue(String.class);

                            if(aux.equals("SIM")){
                                bottomNavigationViewExAdmin.setVisibility(View.VISIBLE);
                            }
                            else{

                                bottomNavigationViewExUser.setVisibility(View.VISIBLE);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        vendaOn = ConfiguracaoFirebase.getFirebaseDatabase().child("vendasPorUsuario").child(ConfiguracaoFirebase.getIdUsuario());
        vendaOn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                venda = dataSnapshot.getValue(Venda.class);
                if(venda!=null) {
                    carrinhoItensTotal.setText(venda.getTotalItens());
                    carrinhoValorTotal.setText(venda.getTotalValor());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void initAtributesMain2(){
      carrinhoItensTotal = findViewById(R.id.idTotalItensCarrinhoMain);
      carrinhoValorTotal = findViewById(R.id.idTotalCarrinhoMain);
      toolbarTabs = findViewById(R.id.idToolbarTabs);
      carrinho = findViewById(R.id.idCarrinho);
      bottomNavigationViewExAdmin = findViewById(R.id.idBottomNavigationTabsAdmin);
      bottomNavigationViewExUser = findViewById(R.id.idBottomNavigationTabsUser);

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
            case R.id.menu_pequisar:
                startActivity(new Intent(getApplicationContext(),PesquisarActivity.class));
                break;
           /* case R.id.menu_listar_vendas:
                startActivity(new Intent(getApplicationContext(),ListarVendasActivity.class));
                break;*/

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
        adminRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        adminRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux;
                aux = dataSnapshot.getValue(String.class);

                if(aux.equals("SIM")){
                    //Configuração
                    bottomNavigationViewExAdmin.enableAnimation(false);
                    bottomNavigationViewExAdmin.enableItemShiftingMode(true);
                    bottomNavigationViewExAdmin.enableShiftingMode(false);
                    bottomNavigationViewExAdmin.setTextVisibility(true);
                    habilitarNavegacaoAdmin(bottomNavigationViewExAdmin);
                }
                else{
                    //Configuração
                    bottomNavigationViewExUser.enableAnimation(false);
                    bottomNavigationViewExUser.enableItemShiftingMode(true);
                    bottomNavigationViewExUser.enableShiftingMode(false);
                    bottomNavigationViewExUser.setTextVisibility(true);
                    habilitarNavegacaoUser(bottomNavigationViewExUser);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }//fim configuracaoBottomNavigation

    //Método para Configurar Eventos de Click Bottom Navigation para User
    private void habilitarNavegacaoUser(BottomNavigationViewEx viewEx){
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
                    case R.id.ic_listar_clientes:
                        startActivity(new Intent(Main2Activity.this,ListarClientesActivity.class));
                        return  true;

                    case R.id.ic_nova_venda:
                        startActivity(new Intent(Main2Activity.this,NovaVendaActivity.class));
                        return  true;
                    case R.id.ic_listar_vendas_user:
                        startActivity(new Intent(Main2Activity.this,ListarVendasActivity.class));
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacaoUser

    //Método para Configurar Eventos de Click Bottom Navigation para Admin
    private void habilitarNavegacaoAdmin(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_listar_clientes:
                        startActivity(new Intent(Main2Activity.this,ListarClientesActivity.class));
                        return  true;
                    case R.id.ic_add_novo_produto:
                        startActivity(new Intent(Main2Activity.this,NovoProdutoActivity.class));
                        return  true;
                    case R.id.ic_nova_venda:
                        startActivity(new Intent(Main2Activity.this,NovaVendaActivity.class));
                        return  true;
                    case R.id.ic_listar_vendas_admin:
                        startActivity(new Intent(Main2Activity.this,ListarVendasActivity.class));
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacaoAdmin

    public void imageLoaderInit(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    public void abrirCarrinhoMain(View view){
        startActivity(new Intent(getApplicationContext(),CarrinhoActivity.class));
    }

    //Método Verificar Usuário Logado
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        }
    }//Fim verificarUsuarioLogado

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }

}
