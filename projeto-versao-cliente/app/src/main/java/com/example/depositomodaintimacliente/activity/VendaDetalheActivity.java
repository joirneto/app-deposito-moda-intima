package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.adapter.AdapterCarrinho;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.example.depositomodaintimacliente.helper.Permissoes;
import com.example.depositomodaintimacliente.model.Produto;
import com.example.depositomodaintimacliente.model.ProdutoVendido;
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
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VendaDetalheActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVendaDetalhe;
    private List<ProdutoVendido> produtosAux = new ArrayList<>();
    private AdapterCarrinho adapterVendaDetalhe;
    private DatabaseReference produtosAuxlistaRef;
    private DatabaseReference adminRef;
    private DatabaseReference usuarioRef;


    private List<ProdutoVendido> produtoVendidos = new ArrayList<>();


    private ProgressBar progressBarVendaDetalhe;

    private DatabaseReference limparDados;
    private DatabaseReference confirmarPedidoRef;
    private DatabaseReference cancelarPeditoRef;

    private DatabaseReference atualizarEstoqueProdutosRef;

    private DatabaseReference atualizarVendaAux1ON;
    private DatabaseReference atualizarVendaAux2ON;
    private DatabaseReference atualizarVendaAux3ON;
    private DatabaseReference atualizarVendaAux4ON;

    private DatabaseReference atualizarProdutosON;

    private DatabaseReference confirmacaoRef;
    private DatabaseReference vendaClienteRef;
    private DatabaseReference estoqueRef;
    private DatabaseReference impressaoRef;

    private AlertDialog.Builder dialogCancelCompra;
    private AlertDialog.Builder dialogCancelProd;

    private TextView statusVendaDetalhe, dataVenda, valorFinal, quantidadeFinal, statusVenda, enderecoVendaDetalhe;
    private Button botaoFinalizar;
    private Venda vendaAux, venda;

    private Produto produtoAux;
    private ProdutoVendido produtoVendidoAux;
    private AlertDialog.Builder dialog;

    private ValueEventListener valueEventListenerVendaDetalhe;

    private BottomNavigationViewEx bottomNavigationViewExVendaDetalhe;

    private String[] permissoes = new String[]{
            //  Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda_detalhe);

        initAtributesVendaDetalheTotal();

        //Configuração Buttom Navigation
        configuracaoBottomNavigation();

        //Validar Permissoes
        Permissoes.validarPermissoes(permissoes, this,1);


        dialogCancelCompra = new AlertDialog.Builder(this);
        dialogCancelProd = new AlertDialog.Builder(this);

        vendaAux = (Venda) getIntent().getSerializableExtra("vendaSelecionada");

        corrigirNomeEtapaVenda(vendaAux);


        dataVenda.setText("DATA: "+vendaAux.getData());
        valorFinal.setText("Valor Total: "+vendaAux.getTotalValor());
        quantidadeFinal.setText("Qtd Total: "+ vendaAux.getTotalItens());
        enderecoVendaDetalhe.setText("Local de Entrega: "+vendaAux.getEnderecoEntrega());
        statusVendaDetalhe.setText(vendaAux.getStatusVenda());


        produtosAuxlistaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas").child(vendaAux.getIdVenda())
                .child("ProdutosVendidos");

        recyclerViewVendaDetalhe.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVendaDetalhe.setHasFixedSize(true);

        adapterVendaDetalhe = new AdapterCarrinho(produtosAux, this);
        recyclerViewVendaDetalhe.setAdapter(adapterVendaDetalhe);
    }


    private void recuperaListaProdutosVendas(){
        produtosAux.clear();
        produtosAuxlistaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtosAux.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    produtosAux.add(ds.getValue(ProdutoVendido.class));
                }
                adapterVendaDetalhe.notifyDataSetChanged();
                progressBarVendaDetalhe.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }//fim recuperaListaProdutos()

    @Override
    protected void onStart() {
        super.onStart();
        recuperaListaProdutosVendas();
    }

    //Método para inicializar os atributos
    public void initAtributesVendaDetalheTotal(){
        recyclerViewVendaDetalhe = findViewById(R.id.idRecyclerVendaDetalhe);
        dataVenda = findViewById(R.id.idDataVendaDetalhe);
        valorFinal = findViewById(R.id.idValorFinalVendaDetalhe);
        quantidadeFinal = findViewById(R.id.idQuantidadeFinalVendaDetalhe);
        progressBarVendaDetalhe = findViewById(R.id.idprogressBarVendaDetalhe);
        statusVendaDetalhe = findViewById(R.id.idStatusVendaDetalhe);

        enderecoVendaDetalhe = findViewById(R.id.idVendaDetalheEndereco);
        bottomNavigationViewExVendaDetalhe = findViewById(R.id.idBottomNavigationTabVendaDetalhe);

    }// fim initAtributesVendaDetalheTotal()


    //Método para Configurar Bottom Navigation
    private void configuracaoBottomNavigation(){
        bottomNavigationViewExVendaDetalhe.enableAnimation(false);
        bottomNavigationViewExVendaDetalhe.enableItemShiftingMode(true);
        bottomNavigationViewExVendaDetalhe.enableShiftingMode(false);
        bottomNavigationViewExVendaDetalhe.setTextVisibility(true);
        habilitarNavegacaoCliente(bottomNavigationViewExVendaDetalhe);

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
                        if(recupTextoDadosClienteConfimacao().isEmpty()){
                            Toast.makeText(getApplicationContext(),
                                    "Sem Histórico de Compras",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(),MinhasComprasActivity.class));
                        }
                        return  true;
                    case R.id.ic_menu:
                           startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacaoAdmin

    private String recupTextoDadosClienteConfimacao(){
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

    public void corrigirNomeEtapaVenda(Venda venda){
        switch (venda.getStatusVenda()){
            case "PedidoOKAguardandoSeparacao":
                venda.setStatusVenda("Pedido OK. Aguardando Separacao.");
                break;
            case "SeparacaoOKAguardandoPagamento":
                venda.setStatusVenda("Separacao OK. Aguardando Pagamento.");
                break;
            case "PagamentoOKAguardandoEnvio":
                venda.setStatusVenda("Pagamento OK. Aguardando envio.");
                break;
            case "EnvioOKAguardandoRecebimentoCliente":
                venda.setStatusVenda("Envio OK. Aguardando Recebimento do Cliente");
                break;
            case "VendasFinalizadas":
                venda.setStatusVenda("Venda Finalizada!");
                break;
        }
    }
}
