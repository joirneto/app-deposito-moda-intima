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
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmacaoPedidoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVendaDetalhe;
    private List<ProdutoVendido> produtosAux = new ArrayList<>();
    private AdapterCarrinho adapterVendaDetalhe;
    private DatabaseReference produtosAuxlistaRef;
    private DatabaseReference adminRef;
    private DatabaseReference usuarioRef;
    private AlertDialog.Builder dialog;


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

    private TextView vendedorVendaDetalhe, clienteVendaDetalhe, valorFinal, quantidadeFinal, statusVenda, enderecoVendaDetalhe;
    private Button botaoFinalizar;
    private Venda vendaAux, venda;

    private Produto produtoAux;
    private ProdutoVendido produtoVendidoAux;
    private AlertDialog.Builder dialogPedido;

    private ValueEventListener valueEventListenerVendaDetalhe;

    private BottomNavigationViewEx bottomNavigationViewExConfirmacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao_pedido);

        initAtributesVendaDetalheTotal();

        //Configuração Buttom Navigation
        configuracaoBottomNavigation();


        dialogCancelCompra = new AlertDialog.Builder(this);
        dialogCancelProd = new AlertDialog.Builder(this);

        vendaAux = (Venda) getIntent().getSerializableExtra("vendaSelecionada");

        clienteVendaDetalhe.setText("Cliente: "+vendaAux.getCliente());
        valorFinal.setText("Valor Total: "+vendaAux.getTotalValor());
        quantidadeFinal.setText("Qtd Total: "+ vendaAux.getTotalItens());
        enderecoVendaDetalhe.setText("Local de Entrega: "+vendaAux.getEnderecoEntrega());


        produtosAuxlistaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoClienteExterno").child(vendaAux.getIdVenda());

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
        recyclerViewVendaDetalhe = findViewById(R.id.idRecyclerConfimacao);
        clienteVendaDetalhe = findViewById(R.id.idClienteVendaDetalhe);
        valorFinal = findViewById(R.id.idValorFinalConfirmacao);
        quantidadeFinal = findViewById(R.id.idQuantidadeFinalConfirmacao);
        progressBarVendaDetalhe = findViewById(R.id.idprogressBarConfirmacao);
        botaoFinalizar = findViewById(R.id.idBotaoFinalizarVendaDetalhe);
        enderecoVendaDetalhe = findViewById(R.id.idVendaEnderecoConfirmacao);
        bottomNavigationViewExConfirmacao = findViewById(R.id.idBottomNavigationTabConfirmacao);

    }// fim initAtributesVendaDetalheTotal()

    public void finalizarEtapa(View view){
        dialog =new AlertDialog.Builder(this);
        dialog.setTitle("CONFIRMAR O PEDIDO");
        dialog.setMessage("Tem certeza que deseja CONFIRMAR este PEDIDO?");

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

                        vendaAux.setStatusVenda("PedidoOKAguardandoSeparacao");

                        atualizarListaProdutosEmVendas();
                        setLimparDados();
                        atualizarQtdProdVendidos(vendaAux);

                        confirmacaoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("vendasPorEtapas")
                                .child("PedidoOKAguardandoSeparacao")
                                .child(vendaAux.getIdVenda());
                        confirmacaoRef.setValue(vendaAux);

                        vendaClienteRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("vendasClientes")
                                .child(vendaAux.getContatoCliente())
                                .child(vendaAux.getIdVenda());
                        vendaClienteRef.setValue(vendaAux);

                        vendaClienteRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("vendas")
                                .child(vendaAux.getIdVenda());
                        vendaClienteRef.setValue(vendaAux);

                        iniciarVenda();
                        pedidoConcluido();




                    }
                });


        dialog.create();
        dialog.show();


    }

    private void atualizarQtdProdVendidos(Venda venda){
        HashMap<String, HashMap<String,String>> produtosAux = venda.getProdutoEquantidade();
        for (final Map.Entry<String, HashMap<String,String>> entrada : produtosAux.entrySet()) {
            //System.out.println(entrada.getKey());
            //System.out.println(entrada.getValue());

            atualizarEstoqueProdutosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("produtos")
                    .child(entrada.getKey());
            atualizarEstoqueProdutosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Produto produtoAtualizarEstoque = dataSnapshot.getValue(Produto.class);
                    HashMap<String,String> qtds = entrada.getValue();

                    int auxEstoqueP,
                            auxEstoqueM,
                            auxEstoqueG,
                            auxEstoqueGG,
                            auxEstoqueU,

                            auxIAT1, auxIAT2, auxIAT3;

                    auxEstoqueP = Integer.parseInt(qtds.get("P"));
                    auxEstoqueM = Integer.parseInt(qtds.get("M"));
                    auxEstoqueG = Integer.parseInt(qtds.get("G"));
                    auxEstoqueGG = Integer.parseInt(qtds.get("GG"));
                    auxEstoqueU = Integer.parseInt(qtds.get("U"));

                    auxIAT2 = auxEstoqueP + auxEstoqueM + auxEstoqueG + auxEstoqueGG + auxEstoqueU;

                    auxIAT1 = Integer.parseInt(produtoAtualizarEstoque.getQtdVendidos());

                    auxIAT3 = auxIAT1+auxIAT2;

                    produtoAtualizarEstoque.setQtdVendidos(String.valueOf(auxIAT3));
                    produtoAtualizarEstoque.salvar();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void atualizarListaProdutosEmVendas(){
        atualizarProdutosON = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoClienteExterno")
                .child(vendaAux.getIdVenda());

        atualizarProdutosON.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    produtoVendidos.add(ds.getValue(ProdutoVendido.class));
                }

                atualizarVendaAux1ON = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("vendasClientesGeral")
                        .child(vendaAux.getIdVenda())
                        .child("idVenda");
                atualizarVendaAux1ON.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String idVenda;
                        idVenda = dataSnapshot.getValue(String.class);
                        atualizarVendaAux2ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("vendasClientesGeral")
                                .child(idVenda);
                        atualizarVendaAux2ON.child("ProdutosVendidos").removeValue();
                        for(int i=0;i<produtoVendidos.size();++i){
                            atualizarVendaAux2ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                    .child("vendasClientesGeral")
                                    .child(idVenda)
                                    .child("ProdutosVendidos").child(produtoVendidos.get(i).getId());
                            atualizarVendaAux2ON.setValue(produtoVendidos.get(i));

                            atualizarVendaAux2ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                    .child("vendasPorEtapas")
                                    .child("PedidoOKAguardandoSeparacao")
                                    .child(idVenda)
                                    .child("ProdutosVendidos").child(produtoVendidos.get(i).getId());
                            atualizarVendaAux2ON.setValue(produtoVendidos.get(i));

                            atualizarVendaAux3ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                    .child("vendasClientes")
                                    .child(vendaAux.getContatoCliente())
                                    .child(idVenda)
                                    .child("ProdutosVendidos").child(produtoVendidos.get(i).getId());
                            atualizarVendaAux3ON.setValue(produtoVendidos.get(i));

                            atualizarVendaAux4ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                    .child("vendas")
                                    .child(idVenda)
                                    .child("ProdutosVendidos").child(produtoVendidos.get(i).getId());
                            atualizarVendaAux4ON.setValue(produtoVendidos.get(i));

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    public void setLimparDados(){
        limparDados =ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoClienteExterno");
        limparDados.child(vendaAux.getIdVenda()).removeValue();
    }

    private void gravarArquivoVenda(String idVenda){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("IDVenda.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(idVenda);
            outputStreamWriter.close();
        }catch (IOException e){

        }
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

    //Método para Configurar Bottom Navigation
    private void configuracaoBottomNavigation(){
        bottomNavigationViewExConfirmacao.enableAnimation(false);
        bottomNavigationViewExConfirmacao.enableItemShiftingMode(true);
        bottomNavigationViewExConfirmacao.enableShiftingMode(false);
        bottomNavigationViewExConfirmacao.setTextVisibility(true);
        habilitarNavegacaoCliente(bottomNavigationViewExConfirmacao);

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


    private void pedidoConcluido(){
        dialogPedido =new AlertDialog.Builder(this);
        dialogPedido.setTitle("PARABÉNS");
        dialogPedido.setMessage("Seu Pedido foi concluído com Sucesso!\n\n" +
                "Entre em contato com o WhatsAPP\n" +
                "(85) 99722-2897, para solicitar os " +
                "dados bancários para depósito/transferência.\n\n" +
                "Após o pagamento, enviar o comprovante " +
                "para o mesmo WhatsAPP e aguarde a " +
                "confirmação no STATUS de seu pedido.\n\n" +
                "Você pode acompanhar seu pedido\nclicando em Minhas Compras.\n\n" +
                "Agradecemos a preferência.\nDeus abençoe!");

        //Para impedir que a dialog feche se clicar fora dele
        dialogPedido.setCancelable(false);

        //Para inserir incone
        dialogPedido.setIcon(R.drawable.ic_parabens);

        dialogPedido.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();


                    }
                });


        dialogPedido.create();
        dialogPedido.show();
    }

}
