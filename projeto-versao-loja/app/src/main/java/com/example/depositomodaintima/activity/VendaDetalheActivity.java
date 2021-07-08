package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.adapter.AdapterCarrinho;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.Permissoes;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Produto;
import com.example.depositomodaintima.model.ProdutoVendido;
import com.example.depositomodaintima.model.Venda;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
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

    private ProgressBar progressBarVendaDetalhe;

    private DatabaseReference limparDados;
    private DatabaseReference confirmarPedidoRef;
    private DatabaseReference cancelarPeditoRef;

    private DatabaseReference atualizarEstoqueProdutosRef;

    private DatabaseReference produtoRef;
    private DatabaseReference atualizarQtdRef;
    private DatabaseReference estoqueRef;
    private DatabaseReference impressaoRef;
    private DatabaseReference alterarStatusRef;

    private AlertDialog.Builder dialogCancelCompra;
    private AlertDialog.Builder dialogCancelProd;

    private TextView vendedorVendaDetalhe, clienteVendaDetalhe, valorFinal, quantidadeFinal, statusVenda, enderecoVendaDetalhe;
    private Button botaoFinalizar;
    private Venda vendaAux, vendaCancel;

    private Produto produtoAux;
    private ProdutoVendido produtoVendidoAux;
    private AlertDialog.Builder dialog;

    private ValueEventListener valueEventListenerVendaDetalhe;

    private String[] permissoes = new String[]{
          //  Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda_detalhe);

        initAtributesVendaDetalheTotal();



        //Validar Permissoes
        Permissoes.validarPermissoes(permissoes, this,1);



        adminRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario())
                .child("admin");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux;
                aux = dataSnapshot.getValue(String.class);
                if(aux.equals("NAO")){
                    //botaoFinalizar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        dialogCancelCompra = new AlertDialog.Builder(this);
        dialogCancelProd = new AlertDialog.Builder(this);

        vendaAux = (Venda) getIntent().getSerializableExtra("vendaSelecionada");

        clienteVendaDetalhe.setText("Cliente: "+vendaAux.getCliente());
        vendedorVendaDetalhe.setText("VENDEDOR: "+vendaAux.getVendedorNome());
        valorFinal.setText("Valor Total: "+vendaAux.getTotalValor());
        quantidadeFinal.setText("Qtd Total: "+ vendaAux.getTotalItens());
        enderecoVendaDetalhe.setText("Local de Entrega: "+vendaAux.getEnderecoEntrega());


        produtosAuxlistaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas").child(vendaAux.getIdVenda()).child("ProdutosVendidos");

        recyclerViewVendaDetalhe.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVendaDetalhe.setHasFixedSize(true);

        adapterVendaDetalhe = new AdapterCarrinho(produtosAux, this);
        recyclerViewVendaDetalhe.setAdapter(adapterVendaDetalhe);

        if(vendaAux.getStatusVenda().equals("VendasFinalizadas")){
            botaoFinalizar.setVisibility(View.GONE);
        }
        if(vendaAux.getVendedorNome().equals("VENDA EXTERNA")){
            enderecoVendaDetalhe.setEnabled(false);
        }

        corrigirNomeEtapaVenda();




    /*    botaoConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((venda.getTotalValor()).equals("0")){
                    Toast.makeText(VendaDetalheActivity.this,
                            "Venda não pode ser finalizada. Nenhum item selecionado.",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    confirmarPedido();
                    Toast.makeText(VendaDetalheActivity.this,
                            "VENDA REALIZADA COM SUCESSO!",
                            Toast.LENGTH_SHORT).show();

                }


            }
        });

        botaoCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialogCancelCompra.setTitle("CANCELAR VENDA");
                dialogCancelCompra.setMessage("Tem certeza que deseja CANCELAR esta VENDA?");

                //Para impedir que a dialog feche se clicar fora dele
                dialogCancelCompra.setCancelable(false);

                //Para inserir incone
                dialogCancelCompra.setIcon(android.R.drawable.ic_delete);

                dialogCancelCompra.setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                dialogCancelCompra.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelarPedido();
                            }
                        });


                dialogCancelCompra.create();
                dialogCancelCompra.show();
            }
        });

        recyclerViewVendaDetalhe.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewVendaDetalhe,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(final View view, final int position) {
                        final ProdutoVendido produtoSelecionado = produtos.get(position);

                        dialogCancelCompra.setTitle("CANCELAR PRODUTO");
                        dialogCancelCompra.setMessage("Tem certeza que deseja EXCLUIR este ITEM?");

                        //Para impedir que a dialog feche se clicar fora dele
                        dialogCancelCompra.setCancelable(false);

                        //Para inserir incone
                        dialogCancelCompra.setIcon(android.R.drawable.ic_delete);

                        dialogCancelCompra.setNegativeButton("Não",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

                        dialogCancelCompra.setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendaProdutoUsuarioOn")
                                                .child(ConfiguracaoFirebase.getIdUsuario());
                                        produtoRef.child(produtoSelecionado.getId()).removeValue();

                                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorUsuario")
                                                .child(ConfiguracaoFirebase.getIdUsuario());
                                        produtoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                vendaCancel = dataSnapshot.getValue(Venda.class);
                                                int numAux1;
                                                String numAux2;

                                                List<String> listaAux1 = vendaCancel.getProdutos();
                                                HashMap<String,String> listaAux2 = vendaCancel.getProdutoEquantidade();

                                                String aux1, aux2, aux3, aux4;
                                                float aux5, aux6, aux7, aux8, aux10;
                                                int aux9;


                                                aux1 = listaAux2.get(produtoSelecionado.getId());
                                                aux2 = produtoSelecionado.getPreco()
                                                        .replace("R$","").replace(",",".");



                                                aux5 = Float.parseFloat(aux1);
                                                aux6 = Float.parseFloat(aux2);

                                                aux3 = vendaCancel.getTotalItens();
                                                aux4 = vendaCancel.getTotalValor().replace(",",".");

                                                Log.i("AUX ", aux3);
                                                Log.i("AUX ", aux4);

                                                aux7 = Float.parseFloat(aux3);
                                                aux8 = Float.parseFloat(aux4);

                                                aux9 = (int) (aux7 - aux5);
                                                aux10 = aux8 - (aux6*aux5);
                                                DecimalFormat df = new DecimalFormat("0.00");

                                                vendaCancel.setTotalItens(String.valueOf(aux9));
                                                vendaCancel.setTotalValor(df.format(aux10));

                                                listaAux1.remove(produtoSelecionado.getId());
                                                listaAux2.remove(produtoSelecionado.getId());

                                                vendaCancel.setProdutos(listaAux1);
                                                vendaCancel.setProdutoEquantidade(listaAux2);

                                                vendaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                        .child("vendasPorUsuario");

                                                vendaRef.child(ConfiguracaoFirebase.getIdUsuario())
                                                        .setValue(vendaCancel);




                                                restaurarEstoque();


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });


                        dialogCancelCompra.create();
                        dialogCancelCompra.show();

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        */
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



  /*  public void setLimparDados(){


        limparDados =ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario());
        limparDados.child("vendendo").setValue("NAO");

        limparDados =ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoUsuarioOn");
        limparDados.child(ConfiguracaoFirebase.getIdUsuario()).removeValue();
        startActivity(new Intent(getApplicationContext(),Main2Activity.class));
        finish();

    }

    public void confirmarPedido(){

        setLimparDados();

        confirmarPedidoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas");

        confirmarPedidoRef.child(venda.getIdVenda()).setValue(venda);
        atualizarQtdProdVendidos(venda);


    }

    public void cancelarPedido(){

        setLimparDados();
        confirmarPedidoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas");

        confirmarPedidoRef.child(venda.getIdVenda()).removeValue();

        restaurarEstoque();


    }

    public void restaurarEstoque(){
        final HashMap<String,String> aux1;
        aux1 = venda.getProdutoEquantidade();

        final List<String> aux2;
        aux2 = venda.getProdutos();


        if(aux2!=null){
            for(int i = 0;i<aux2.size();i++){

                estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("produtos")
                        .child(aux2.get(i))
                        .child("quantidade");
                final int finalI = i;
                estoqueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String aux3;
                        int aux4, aux5;
                        aux3 = String.valueOf(dataSnapshot.getValue());
                        aux4 = Integer.parseInt(aux3);

                        aux5 = Integer.parseInt(aux1.get(aux2.get(finalI)));

                        estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("produtos")
                                .child(aux2.get(finalI))
                                .child("quantidade");
                        estoqueRef.setValue(String.valueOf(aux4+aux5));
                        Log.i("TESTEZ2 ", String.valueOf(aux4+aux5));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void atualizarQtdProdVendidos(Venda venda){
        HashMap<String, String> produtosAux = venda.getProdutoEquantidade();
        for (final Map.Entry<String, String> entrada : produtosAux.entrySet()) {
            //System.out.println(entrada.getKey());
            //System.out.println(entrada.getValue());

            atualizarEstoqueProdutosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("produtos")
                    .child(entrada.getKey());
            atualizarEstoqueProdutosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Produto produtoAtualizarEstoque = dataSnapshot.getValue(Produto.class);
                    int auxIAT1, auxIAT2, auxIAT3;

                    auxIAT1 = Integer.parseInt(produtoAtualizarEstoque.getQtdVendidos());
                    Log.i("ZZZQW1: ", String.valueOf(auxIAT1));
                    auxIAT2 = Integer.parseInt(entrada.getValue());
                    Log.i("ZZZQW2: ", String.valueOf(auxIAT2));
                    auxIAT3 = auxIAT1+auxIAT2;
                    Log.i("ZZZQW3: ", String.valueOf(auxIAT3));

                    produtoAtualizarEstoque.setQtdVendidos(String.valueOf(auxIAT3));
                    produtoAtualizarEstoque.salvar();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

*/
        //Método para inicializar os atributos
        public void initAtributesVendaDetalheTotal(){
            recyclerViewVendaDetalhe = findViewById(R.id.idRecyclerVendaDetalhe);
            vendedorVendaDetalhe = findViewById(R.id.idVendedorVendaDetalhe);
            clienteVendaDetalhe = findViewById(R.id.idClienteVendaDetalhe);
            valorFinal = findViewById(R.id.idValorFinalVendaDetalhe);
            quantidadeFinal = findViewById(R.id.idQuantidadeFinalVendaDetalhe);
            progressBarVendaDetalhe = findViewById(R.id.idprogressBarVendaDetalhe);
            botaoFinalizar = findViewById(R.id.idBotaoFinalizarVendaDetalhe);
            statusVenda = findViewById(R.id.idStatusVendaDetalhe);
            enderecoVendaDetalhe = findViewById(R.id.idVendaDetalheEndereco);

        }// fim initAtributesVendaDetalheTotal()

    public void finalizarEtapa(View view){
        dialog =new AlertDialog.Builder(this);
        dialog.setTitle("FINALIZAR ETAPA");
        dialog.setMessage("Tem certeza que deseja FINALIZAR esta etapa");

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
                        switch (vendaAux.getStatusVenda()){
                            case "PedidoOKAguardandoSeparacao":
                                final List<ProdutoVendido> produtoVendidosAux1 = new ArrayList<>();
                                final DatabaseReference alterarStatus1Ref1;

                                //Referencia para copiar os produtosVendidos
                                alterarStatus1Ref1 = ConfiguracaoFirebase.getFirebaseDatabase()
                                        .child("vendasPorEtapas")
                                        .child("PedidoOKAguardandoSeparacao")
                                        .child(vendaAux.getIdVenda())
                                        .child("ProdutosVendidos");
                                alterarStatus1Ref1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                            produtoVendidosAux1.add(ds.getValue(ProdutoVendido.class));
                                        }

                                        //Excluindo Venda
                                        DatabaseReference alterarStatus2Ref1;
                                        alterarStatus2Ref1 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("PedidoOKAguardandoSeparacao")
                                                .child(vendaAux.getIdVenda());
                                        alterarStatus2Ref1.removeValue();

                                        //Salvando venda em "SeparacaoOKAguardandoPagamento"
                                        vendaAux.setStatusVenda("SeparacaoOKAguardandoPagamento");

                                        alterarStatus2Ref1 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("SeparacaoOKAguardandoPagamento")
                                                .child(vendaAux.getIdVenda());

                                        alterarStatus2Ref1.setValue(vendaAux);

                                        //Salvando produtos na venda alterada
                                        for(int i=0;i<produtoVendidosAux1.size();i++){
                                            alterarStatus2Ref1 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasPorEtapas")
                                                    .child("SeparacaoOKAguardandoPagamento")
                                                    .child(vendaAux.getIdVenda())
                                                    .child("ProdutosVendidos")
                                                    .child(produtoVendidosAux1.get(i).getId());
                                            alterarStatus2Ref1.setValue(produtoVendidosAux1.get(i));


                                        }

                                        if(!vendaAux.getContatoCliente().isEmpty()){
                                            alterarStatusRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasClientes")
                                                    .child(vendaAux.getContatoCliente())
                                                    .child(vendaAux.getIdVenda())
                                                    .child("statusVenda");
                                            alterarStatusRef.setValue("SeparacaoOKAguardandoPagamento");


                                        }


                                        startReturn();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                break;
                            case "SeparacaoOKAguardandoPagamento":
                                final DatabaseReference alterarStatus1Ref2;
                                //Referencia para copiar os produtosVendidos
                                alterarStatus1Ref2 = ConfiguracaoFirebase.getFirebaseDatabase()
                                        .child("vendasPorEtapas")
                                        .child("SeparacaoOKAguardandoPagamento")
                                        .child(vendaAux.getIdVenda())
                                        .child("ProdutosVendidos");
                                alterarStatus1Ref2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<ProdutoVendido> produtoVendidosAux2 = new ArrayList<>();
                                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                            produtoVendidosAux2.add(ds.getValue(ProdutoVendido.class));
                                        }

                                        //Excluindo Venda
                                        DatabaseReference alterarStatus2Ref2;
                                        alterarStatus2Ref2 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("SeparacaoOKAguardandoPagamento")
                                                .child(vendaAux.getIdVenda());
                                        alterarStatus2Ref2.removeValue();

                                        //Salvando venda em "SeparacaoOKAguardandoPagamento"
                                        vendaAux.setStatusVenda("PagamentoOKAguardandoEnvio");

                                        alterarStatus2Ref2 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("PagamentoOKAguardandoEnvio")
                                                .child(vendaAux.getIdVenda());

                                        alterarStatus2Ref2.setValue(vendaAux);

                                        //Salvando produtos na venda alterada
                                        for(int i=0;i<produtoVendidosAux2.size();i++){
                                            alterarStatus2Ref2 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasPorEtapas")
                                                    .child("PagamentoOKAguardandoEnvio")
                                                    .child(vendaAux.getIdVenda())
                                                    .child("ProdutosVendidos")
                                                    .child(produtoVendidosAux2.get(i).getId());
                                            alterarStatus2Ref2.setValue(produtoVendidosAux2.get(i));


                                        }

                                        if(!vendaAux.getContatoCliente().isEmpty()){
                                            alterarStatusRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasClientes")
                                                    .child(vendaAux.getContatoCliente())
                                                    .child(vendaAux.getIdVenda())
                                                    .child("statusVenda");
                                            alterarStatusRef.setValue("PagamentoOKAguardandoEnvio");


                                        }
                                        startReturn();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                break;
                            case "PagamentoOKAguardandoEnvio":
                                final DatabaseReference alterarStatus1Ref3;
                                //Referencia para copiar os produtosVendidos
                                alterarStatus1Ref3 = ConfiguracaoFirebase.getFirebaseDatabase()
                                        .child("vendasPorEtapas")
                                        .child("PagamentoOKAguardandoEnvio")
                                        .child(vendaAux.getIdVenda())
                                        .child("ProdutosVendidos");
                                alterarStatus1Ref3.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<ProdutoVendido> produtoVendidosAux3 = new ArrayList<>();
                                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                            produtoVendidosAux3.add(ds.getValue(ProdutoVendido.class));
                                        }

                                        //Excluindo Venda
                                        DatabaseReference alterarStatus2Ref3;
                                        alterarStatus2Ref3 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("PagamentoOKAguardandoEnvio")
                                                .child(vendaAux.getIdVenda());
                                        alterarStatus2Ref3.removeValue();

                                        //Salvando venda em "SeparacaoOKAguardandoPagamento"
                                        vendaAux.setStatusVenda("EnvioOKAguardandoRecebimentoCliente");

                                        alterarStatus2Ref3 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("EnvioOKAguardandoRecebimentoCliente")
                                                .child(vendaAux.getIdVenda());

                                        alterarStatus2Ref3.setValue(vendaAux);

                                        //Salvando produtos na venda alterada
                                        for(int i=0;i<produtoVendidosAux3.size();i++){
                                            alterarStatus2Ref3 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasPorEtapas")
                                                    .child("EnvioOKAguardandoRecebimentoCliente")
                                                    .child(vendaAux.getIdVenda())
                                                    .child("ProdutosVendidos")
                                                    .child(produtoVendidosAux3.get(i).getId());
                                            alterarStatus2Ref3.setValue(produtoVendidosAux3.get(i));

                                        }
                                        if(!vendaAux.getContatoCliente().isEmpty()){
                                            alterarStatusRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasClientes")
                                                    .child(vendaAux.getContatoCliente())
                                                    .child(vendaAux.getIdVenda())
                                                    .child("statusVenda");
                                            alterarStatusRef.setValue("EnvioOKAguardandoRecebimentoCliente");


                                        }
                                        startReturn();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                break;
                            case "EnvioOKAguardandoRecebimentoCliente":
                                final DatabaseReference alterarStatus1Ref4;
                                //Referencia para copiar os produtosVendidos
                                alterarStatus1Ref4 = ConfiguracaoFirebase.getFirebaseDatabase()
                                        .child("vendasPorEtapas")
                                        .child("EnvioOKAguardandoRecebimentoCliente")
                                        .child(vendaAux.getIdVenda())
                                        .child("ProdutosVendidos");
                                alterarStatus1Ref4.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<ProdutoVendido> produtoVendidosAux4 = new ArrayList<>();
                                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                                            produtoVendidosAux4.add(ds.getValue(ProdutoVendido.class));
                                        }

                                        //Excluindo Venda
                                        DatabaseReference alterarStatus2Ref4;
                                        alterarStatus2Ref4 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("EnvioOKAguardandoRecebimentoCliente")
                                                .child(vendaAux.getIdVenda());
                                        alterarStatus2Ref4.removeValue();

                                        //Salvando venda em "SeparacaoOKAguardandoPagamento"
                                        vendaAux.setStatusVenda("VendasFinalizadas");

                                        alterarStatus2Ref4 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorEtapas")
                                                .child("VendasFinalizadas")
                                                .child(vendaAux.getIdVenda());

                                        alterarStatus2Ref4.setValue(vendaAux);

                                        //Salvando produtos na venda alterada
                                        for(int i=0;i<produtoVendidosAux4.size();i++){
                                            alterarStatus2Ref4 = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasPorEtapas")
                                                    .child("VendasFinalizadas")
                                                    .child(vendaAux.getIdVenda())
                                                    .child("ProdutosVendidos")
                                                    .child(produtoVendidosAux4.get(i).getId());
                                            alterarStatus2Ref4.setValue(produtoVendidosAux4.get(i));

                                        }

                                        if(!vendaAux.getContatoCliente().isEmpty()){
                                            alterarStatusRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                    .child("vendasClientes")
                                                    .child(vendaAux.getContatoCliente())
                                                    .child(vendaAux.getIdVenda())
                                                    .child("statusVenda");
                                            alterarStatusRef.setValue("VendasFinalizadas");


                                        }
                                        startReturn();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                break;
                        }
                    }
                });


        dialog.create();
        dialog.show();


    }

    public void startReturn(){
        startActivity(new Intent(getApplicationContext(), ListarVendasActivity.class));
        finish();

    }

    public void editarEndereco(View view){

        Intent intent = new Intent(getApplicationContext(), RegistrarEnderecoActivity.class);
        intent.putExtra("vendaSelecionada", vendaAux);
        startActivity(intent);
        finish();

        /*DatabaseReference checarAdminRef;
        checarAdminRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario())
                .child("admin");
        checarAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux;
                aux = dataSnapshot.getValue(String.class);

                if(aux.equals("SIM")){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

*/
    }

    public void corrigirNomeEtapaVenda(){
            switch (vendaAux.getStatusVenda()){
                case "PedidoOKAguardandoSeparacao":
                    statusVenda.setText("Pedido OK. Aguardando Separacao.");
                    botaoFinalizar.setText("PEDIDO SEPARADO!");
                    break;
                case "SeparacaoOKAguardandoPagamento":
                    statusVenda.setText("Separacao OK. Aguardando Pagamento.");
                    botaoFinalizar.setText("PAGAMENTO REALIZADO!");
                    break;
                case "PagamentoOKAguardandoEnvio":
                    statusVenda.setText("Pagamento OK. Aguardando envio.");
                    botaoFinalizar.setText("PEDIDO ENVIADO!");
                    break;
                case "EnvioOKAguardandoRecebimentoCliente":
                    statusVenda.setText("Envio OK. Aguardando Recebimento do Cliente");
                    botaoFinalizar.setText("PEDIDO RECEBIDO!");
                    break;
                case "VendasFinalizadas":
                    statusVenda.setText("Venda Finalizada!");
                    break;
            }
    }

    public void imprimirVenda(View view){
            createPdf();
    }


    private void createPdf(){
        // create a new document
        final PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(400, 190+(vendaAux.getProdutoEquantidade().size()*125), 1).create();

        // start a page
        final PdfDocument.Page page = document.startPage(pageInfo);

        final Canvas canvas = page.getCanvas();
        final Paint paint = new Paint();
        paint.setColor(this.getResources().getColor(R.color.colorBlack));
        paint.setFakeBoldText(true);

        canvas.drawText("DEPOSITO DA MODA ÍNTIMA",117,15,paint);
        paint.setFakeBoldText(false);
        canvas.drawText("DATA DA VENDA: "+ vendaAux.getData(),20,40,paint);
        canvas.drawText("CLIENTE: "+ vendaAux.getCliente(),20,60,paint);
        canvas.drawText("TOTAL DE ITENS: "+ vendaAux.getTotalItens(),20,80,paint);
        canvas.drawText("VALOR TOTAL DA VENDA: "+vendaAux.getTotalValor(),20,100,paint);

        final int[] i;
        char auxSepara[];
        String auxSepara1 = "", auxSepara2 = "";
        auxSepara = vendaAux.getEnderecoEntrega().toCharArray();
        if(auxSepara.length>42){

            for(int k=0;k<42;k++){
                auxSepara1 = auxSepara1+ auxSepara[k];
            }
            for(int j=42;j<auxSepara.length;j++){
                auxSepara2 = auxSepara2+ auxSepara[j];
            }
            auxSepara1 = auxSepara1 + "-";

            canvas.drawText("LOCAL DE ENTREGA: "+auxSepara1,20,120,paint);
            canvas.drawText("                                        "+auxSepara2,20,135,paint);
            paint.setFakeBoldText(true);
            canvas.drawText("PRODUTOS",154,155,paint);
            paint.setFakeBoldText(false);
             i = new int[]{175};
        }else{

            canvas.drawText("LOCAL DE ENTREGA: "+vendaAux.getEnderecoEntrega(),20,120,paint);
            paint.setFakeBoldText(true);
            canvas.drawText("PRODUTOS",154,140,paint);
            paint.setFakeBoldText(false);
            i = new int[]{160};

        }


        final int[] j = {1};
        final HashMap<String, HashMap<String,String>>hashMap = vendaAux.getProdutoEquantidade();
        for (final Map.Entry<String, HashMap<String,String>> entrada : hashMap.entrySet()) {
            impressaoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("produtos")
                    .child(entrada.getKey());
            impressaoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Produto produtoImpressao = dataSnapshot.getValue(Produto.class);
                    HashMap<String,String> qtds = entrada.getValue();

                    String auxEstoqueP,
                            auxEstoqueM,
                            auxEstoqueG,
                            auxEstoqueGG,
                            auxEstoqueU;

                    auxEstoqueP = qtds.get("P");
                    auxEstoqueM = qtds.get("M");
                    auxEstoqueG = qtds.get("G");
                    auxEstoqueGG = qtds.get("GG");
                    auxEstoqueU = qtds.get("U");

                    int intP, intM, intG, intGG, intU;

                    intP = Integer.parseInt(auxEstoqueP);
                    intM = Integer.parseInt(auxEstoqueM);
                    intG = Integer.parseInt(auxEstoqueG);
                    intGG = Integer.parseInt(auxEstoqueGG);
                    intU = Integer.parseInt(auxEstoqueU);

                    paint.setFakeBoldText(true);
                    canvas.drawText("Produto "+j[0],20, i[0],paint);
                    i[0] = i[0] +15;
                    paint.setFakeBoldText(false);

                    char auxSep[];
                    String auxSep1 = "", auxSep2 = "";
                    auxSep = produtoImpressao.getTitulo().toCharArray();
                    if(auxSep.length>47){

                        for(int k=0;k<42;k++){
                            auxSep1 = auxSep1+ auxSep[k];
                        }
                        for(int j=42;j<auxSep.length;j++){
                            auxSep2 = auxSep2+ auxSep[j];
                        }

                        canvas.drawText("Produto: "+auxSep1,20, i[0],paint);
                        i[0] = i[0] +15;
                        canvas.drawText("                 "+auxSep2,20,i[0],paint);
                        i[0] = i[0] +15;

                    }else{

                        canvas.drawText("Produto: "+produtoImpressao.getTitulo(),20, i[0],paint);
                        i[0] = i[0] +15;

                    }





                    canvas.drawText("Preço do Produto: "+produtoImpressao.getPreco(),20, i[0],paint);
                    i[0] = i[0] +15;
                    canvas.drawText("Quantidade Total: "+(String.valueOf(intP+intM+intG+intGG+intU)),20, i[0],paint);
                    i[0] = i[0] +15;

                    String precoAtualizado1, precoAtualizado2;
                    float precoAux, precoOK;
                    precoAtualizado1 = produtoImpressao.getPreco().replace("R$", "");
                    precoAtualizado2 = precoAtualizado1.replace(",", ".");
                    precoAux = Float.parseFloat(precoAtualizado2);


                    precoOK = ((float) (intP+intM+intG+intGG+intU)) * precoAux;

                    DecimalFormat df = new DecimalFormat("0.00");





                    canvas.drawText("Valor Total deste Item: R$ "+df.format(precoOK),20, i[0],paint);
                    i[0] = i[0] +15;


                    String aux = "";

                    if(!auxEstoqueP.equals("0")){
                        aux = aux + "P: "+auxEstoqueP+" ";
                    }

                    if(!auxEstoqueM.equals("0")){
                        aux = aux + "M: "+auxEstoqueM+" ";
                    }

                    if(!auxEstoqueG.equals("0")){
                        aux = aux + "G: "+auxEstoqueG+" ";
                    }

                    if(!auxEstoqueGG.equals("0")){
                        aux = aux + "GG: "+auxEstoqueGG+" ";
                    }

                    if(!auxEstoqueU.equals("0")){
                        aux = aux + "ÚNICO: "+auxEstoqueU+" ";
                    }

                    canvas.drawText("Tamanhos: "+aux,20, i[0],paint);
                    i[0] = i[0] +15;



                    if(j[0] ==vendaAux.getProdutoEquantidade().size()){
                        i[0] = i[0] +35;
                        canvas.drawText("---------------------------------- FIM ----------------------------------",80,
                                170+(vendaAux.getProdutoEquantidade().size()*125),paint);
                        // finish the page
                        document.finishPage(page);

                        // write the document content
                        String targetPdf = "/sdcard/"+"DMI-Venda: "+vendaAux.getCliente()+".pdf";
                        File filePath = new File(targetPdf);
                        try {
                            document.writeTo(new FileOutputStream(filePath));
                            Toast.makeText(getApplicationContext(), "IMPRESSÃO EFETUADA COM SUCESSO!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something wrong: " + e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }

                        // close the document
                        document.close();

                    }
                    j[0]++;
                    i[0] = i[0] +20;

                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
           // canvas.drawText(vendaAux.getStatusVenda(),10,vendaAux.getProdutoEquantidade().size()+100,paint);
        }





    }

    //Método para criar Alerta das Permissoes
    private void alertaVAlidacaoPermissao(){
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }//fim alertaVAlidacaoPermissao()

    // Método para validar as Permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoREsultado: grantResults){
            if(permissaoREsultado== PackageManager.PERMISSION_DENIED){
                alertaVAlidacaoPermissao();

            }
        }
    }//fim onRequestPermissionsResult

}
