package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.adapter.AdapterCarrinho;
import com.example.depositomodaintima.adapter.AdapterListaVendas;
import com.example.depositomodaintima.fragment.BabydoolFragment;
import com.example.depositomodaintima.fragment.CalcinhaAdultoFragment;
import com.example.depositomodaintima.fragment.CalcinhaInfantilFragment;
import com.example.depositomodaintima.fragment.CamisolaFragment;
import com.example.depositomodaintima.fragment.ConjuntosFragment;
import com.example.depositomodaintima.fragment.CuecaAdultoFragment;
import com.example.depositomodaintima.fragment.CuecaInfantilFragment;
import com.example.depositomodaintima.fragment.EnvioRecebimentoFragment;
import com.example.depositomodaintima.fragment.MeiaMasculinaFragment;
import com.example.depositomodaintima.fragment.PagamentoEnvioFragment;
import com.example.depositomodaintima.fragment.PedidoSeparacaoFragment;
import com.example.depositomodaintima.fragment.SeparacaoPagamentoFragment;
import com.example.depositomodaintima.fragment.ShortFragment;
import com.example.depositomodaintima.fragment.SutiaFragment;
import com.example.depositomodaintima.fragment.TopAdultoFragment;
import com.example.depositomodaintima.fragment.TopInfantilFragment;
import com.example.depositomodaintima.fragment.VendasFinalizadasFragment;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.ProdutoVendido;
import com.example.depositomodaintima.model.Venda;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListarVendasActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_vendas);

        getSupportActionBar().setTitle("VENDAS REALIZADAS");

        initAtributesListarVendas();

        //Configurando abas
        FragmentPagerItemAdapter adapter;
        adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("PEDIDO OK.\nAGUARDANDO SEPARAÇÃO.", PedidoSeparacaoFragment.class)
                        .add("SEPARAÇÃO OK.\nAGUARDANDO PAGAMENTO.", SeparacaoPagamentoFragment.class)
                        .add("PAGAMENTO OK.\n AGUARDANDO ENVIO. ", PagamentoEnvioFragment.class)
                        .add("ENVIO OK. \nAGUARDANDO RECEBIMENTO CLIENTE.", EnvioRecebimentoFragment.class)
                        .add("VENDAS FINALIZADAS", VendasFinalizadasFragment.class)
                        .create());

        ViewPager viewPager = findViewById(R.id.viewPagerListaVendas);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab= findViewById(R.id.viewPagerTabListaVendas);
        viewPagerTab.setViewPager(viewPager);

    }

    public void initAtributesListarVendas(){

    }



}
