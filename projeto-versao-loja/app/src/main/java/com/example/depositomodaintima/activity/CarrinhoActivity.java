package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Produto;
import com.example.depositomodaintima.model.ProdutoVendido;
import com.example.depositomodaintima.model.Venda;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarrinhoActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCarrinho;
    private List<ProdutoVendido> produtos = new ArrayList<>();
    private AdapterCarrinho adapterCarrinho;
    private DatabaseReference produtoslistaRef;
    private DatabaseReference vendaRef;
    private DatabaseReference usuarioRef;

    private ProgressBar progressBarCarrinho;

    private DatabaseReference limparDados;
    private DatabaseReference confirmarPedidoRef;
    private DatabaseReference atualizarVendaAux1ON;
    private DatabaseReference atualizarVendaAux2ON;
    private DatabaseReference atualizarVendaAux3ON;

    private DatabaseReference editarProdRef;


    private DatabaseReference atualizarEstoqueProdutosRef;

    private DatabaseReference produtoRef;
    private DatabaseReference atualizarProdutosON;
    private DatabaseReference estoqueRef;

    private AlertDialog.Builder dialogCancelCompra;
    private AlertDialog.Builder dialogCancelProd;

    private TextView vendedorCarrinho, clienteCarrinho, valorFinal, quantidadeFinal;
    private Button botaoCancelar, botaoConfirmar;
    private Venda venda, vendaCancel;

    private ValueEventListener valueEventListenerCarrinho;

    private List<ProdutoVendido> produtoVendidos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);


        initAtributesCarrinhoTotal();
        dialogCancelCompra =new AlertDialog.Builder(this);
        dialogCancelProd =new AlertDialog.Builder(this);

    vendaRef = ConfiguracaoFirebase.getFirebaseDatabase().child("vendasPorUsuario").child(ConfiguracaoFirebase.getIdUsuario()).child("cliente");
        vendaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nomeCliente;
                nomeCliente = dataSnapshot.getValue(String.class);
                clienteCarrinho.setText("Cliente: "+nomeCliente);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(ConfiguracaoFirebase.getIdUsuario()).child("nome");
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nomeVendedor;
                nomeVendedor = dataSnapshot.getValue(String.class);
                vendedorCarrinho.setText("VENDEDOR: "+nomeVendedor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        vendaRef = ConfiguracaoFirebase.getFirebaseDatabase().child("vendasPorUsuario").child(ConfiguracaoFirebase.getIdUsuario());
        vendaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                venda = dataSnapshot.getValue(Venda.class);
                valorFinal.setText("Valor Total: "+venda.getTotalValor());
                quantidadeFinal.setText("Qtd Total: "+ venda.getTotalItens());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        produtoslistaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoUsuarioOn")
                .child(ConfiguracaoFirebase.getIdUsuario());


        recyclerViewCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarrinho.setHasFixedSize(true);

        adapterCarrinho = new AdapterCarrinho(produtos, this);
        recyclerViewCarrinho.setAdapter(adapterCarrinho);




        botaoConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((venda.getTotalValor()).equals("0")||(venda.getTotalValor()).equals("0,00")){
                    Toast.makeText(CarrinhoActivity.this,
                            "Venda não pode ser finalizada. Nenhum item selecionado.",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    confirmarPedido();
                    Toast.makeText(CarrinhoActivity.this,
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

        recyclerViewCarrinho.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerViewCarrinho,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final ProdutoVendido produtoSelecionado = produtos.get(position);

                        dialogCancelCompra.setTitle("EDITAR PRODUTO");
                        dialogCancelCompra.setMessage("Tem certeza que deseja EDITAR este ITEM?");

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
                                                HashMap<String,String> listaAux2 = vendaCancel.getProdutoEquantidade().get(produtoSelecionado.getId());

                                                String aux1P,
                                                        aux1M,
                                                        aux1G,
                                                        aux1GG,
                                                        aux1U,
                                                        aux2, aux3, aux4;
                                                float aux5,
                                                        aux5P,
                                                        aux5M,
                                                        aux5G,
                                                        aux5GG,
                                                        aux5U,


                                                        aux6, aux7, aux8, aux10;
                                                int aux9;

                                                aux1P = listaAux2.get("P");
                                                aux1M = listaAux2.get("M");
                                                aux1G = listaAux2.get("G");
                                                aux1GG = listaAux2.get("GG");
                                                aux1U = listaAux2.get("U");

                                                aux2 = produtoSelecionado.getPreco()
                                                        .replace("R$","").replace(",",".");


                                                aux5P = Float.parseFloat(aux1P);
                                                aux5M = Float.parseFloat(aux1M);
                                                aux5G = Float.parseFloat(aux1G);
                                                aux5GG = Float.parseFloat(aux1GG);
                                                aux5U = Float.parseFloat(aux1U);
                                                aux5 = aux5P + aux5M + aux5G + aux5GG + aux5U;

                                                aux6 = Float.parseFloat(aux2);

                                                aux3 = vendaCancel.getTotalItens();
                                                aux4 = vendaCancel.getTotalValor().replace(",",".");

                                                aux7 = Float.parseFloat(aux3);
                                                aux8 = Float.parseFloat(aux4);

                                                aux9 = (int) (aux7 - aux5);
                                                aux10 = aux8 - (aux6*aux5);
                                                DecimalFormat df = new DecimalFormat("0.00");

                                                vendaCancel.setTotalItens(String.valueOf(aux9));
                                                vendaCancel.setTotalValor(df.format(aux10));

                                                listaAux1.remove(produtoSelecionado.getId());

                                                HashMap<String,HashMap<String,String>> hashMapExc = vendaCancel.getProdutoEquantidade();
                                                hashMapExc.remove(produtoSelecionado.getId());
                                                vendaCancel.setProdutoEquantidade(hashMapExc);


                                                vendaCancel.setProdutos(listaAux1);

                                                vendaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                        .child("vendasPorUsuario");

                                                vendaRef.child(ConfiguracaoFirebase.getIdUsuario())
                                                        .setValue(vendaCancel);

                                                restaurarEstoqueItem(produtoSelecionado);

                                                editarProdRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                        .child("produtos")
                                                        .child(produtoSelecionado.getId());

                                                editarProdRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Produto produtoEdit = dataSnapshot.getValue(Produto.class);
                                                        Intent intent = new Intent(getApplicationContext(),ProdutoDetalhesActivity.class);
                                                        intent.putExtra("produtoSelecionado", produtoEdit);
                                                        HashMap<String,String> tamanhosEdit = produtoSelecionado.getTamanhosEquantidades();
                                                        Log.i("FFF: ", String.valueOf(tamanhosEdit));
                                                        intent.putExtra("tamanhos",tamanhosEdit);
                                                        startActivity(intent);
                                                        finish();

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
                                });


                        dialogCancelCompra.create();
                        dialogCancelCompra.show();

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
                                                HashMap<String,String> listaAux2 = vendaCancel.getProdutoEquantidade().get(produtoSelecionado.getId());

                                                String aux1P,
                                                        aux1M,
                                                        aux1G,
                                                        aux1GG,
                                                        aux1U,
                                                        aux2, aux3, aux4;
                                                float aux5,
                                                        aux5P,
                                                        aux5M,
                                                        aux5G,
                                                        aux5GG,
                                                        aux5U,


                                                        aux6, aux7, aux8, aux10;
                                                int aux9;

                                                aux1P = listaAux2.get("P");
                                                aux1M = listaAux2.get("M");
                                                aux1G = listaAux2.get("G");
                                                aux1GG = listaAux2.get("GG");
                                                aux1U = listaAux2.get("U");

                                                aux2 = produtoSelecionado.getPreco()
                                                        .replace("R$","").replace(",",".");


                                                aux5P = Float.parseFloat(aux1P);
                                                aux5M = Float.parseFloat(aux1M);
                                                aux5G = Float.parseFloat(aux1G);
                                                aux5GG = Float.parseFloat(aux1GG);
                                                aux5U = Float.parseFloat(aux1U);
                                                aux5 = aux5P + aux5M + aux5G + aux5GG + aux5U;

                                                aux6 = Float.parseFloat(aux2);

                                                aux3 = vendaCancel.getTotalItens();
                                                aux4 = vendaCancel.getTotalValor().replace(",",".");

                                                aux7 = Float.parseFloat(aux3);
                                                aux8 = Float.parseFloat(aux4);

                                                aux9 = (int) (aux7 - aux5);
                                                aux10 = aux8 - (aux6*aux5);
                                                DecimalFormat df = new DecimalFormat("0.00");

                                                vendaCancel.setTotalItens(String.valueOf(aux9));
                                                vendaCancel.setTotalValor(df.format(aux10));

                                                listaAux1.remove(produtoSelecionado.getId());

                                                HashMap<String,HashMap<String,String>> hashMapExc = vendaCancel.getProdutoEquantidade();
                                                hashMapExc.remove(produtoSelecionado.getId());
                                                vendaCancel.setProdutoEquantidade(hashMapExc);


                                                vendaCancel.setProdutos(listaAux1);

                                                vendaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                        .child("vendasPorUsuario");

                                                vendaRef.child(ConfiguracaoFirebase.getIdUsuario())
                                                        .setValue(vendaCancel);




                                                restaurarEstoqueItem(produtoSelecionado);


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


    }

    private void recuperaListaProdutos(){
        produtos.clear();
        valueEventListenerCarrinho = produtoslistaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(ProdutoVendido.class));
                }
                adapterCarrinho.notifyDataSetChanged();
                progressBarCarrinho.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperaListaProdutos();
    }

    @Override
    protected void onStop() {
        super.onStop();

        produtoslistaRef.removeEventListener(valueEventListenerCarrinho);
    }

    //Método para inicializar os atributos
    public void initAtributesCarrinhoTotal(){
        recyclerViewCarrinho = findViewById(R.id.idRecyclerCarrinho);
        vendedorCarrinho = findViewById(R.id.idVendedorCarrinho);
        clienteCarrinho = findViewById(R.id.idClienteCarrinho);
        valorFinal = findViewById(R.id.idValorFinal);
        quantidadeFinal = findViewById(R.id.idQuantidadeFinal);
        botaoCancelar = findViewById(R.id.idBotaoCancelarVenda);
        botaoConfirmar = findViewById(R.id.idBotaoFinalizarVenda);
        progressBarCarrinho = findViewById(R.id.idprogressBarCarrinho);

    }// fim initAtributesCarrinhoTotal()

    public void setLimparDados(){


        limparDados =ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario());
        limparDados.child("vendendo").setValue("NAO");

      limparDados =ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoUsuarioOn");
       limparDados.child(ConfiguracaoFirebase.getIdUsuario()).removeValue();

    }

    public void confirmarPedido(){
        atualizarListaProdutosEmVendas();
        setLimparDados();

        venda.setStatusVenda("PedidoOKAguardandoSeparacao");

        confirmarPedidoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas");

        confirmarPedidoRef.child(venda.getIdVenda()).setValue(venda);

        atualizarVendaAux3ON = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasUsuarioHistorico")
                .child(ConfiguracaoFirebase.getIdUsuario())
                .child(venda.getIdVenda());
        atualizarVendaAux3ON.setValue(venda.getIdVenda());

        atualizarQtdProdVendidos(venda);

        atualizarVendaAux3ON = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child("PedidoOKAguardandoSeparacao")
                .child(venda.getIdVenda());
        atualizarVendaAux3ON.setValue(venda);

        Intent intent = new Intent(getApplicationContext(),RegistrarEnderecoActivity.class);
        intent.putExtra("vendaSelecionada", venda);
        startActivity(intent);
        finish();


    }

    public void cancelarPedido(){

        setLimparDados();
        confirmarPedidoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas");

        confirmarPedidoRef.child(venda.getIdVenda()).removeValue();

        confirmarPedidoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child("PedidoOKAguardandoSeparacao");

        confirmarPedidoRef.child(venda.getIdVenda()).removeValue();

        restaurarEstoque();

        startActivity(new Intent(getApplicationContext(),Main2Activity.class));
        finish();


    }

    public void restaurarEstoque(){
        final HashMap<String,HashMap<String,String>> aux1;
        aux1 = venda.getProdutoEquantidade();

        final List<String> aux2;
        aux2 = venda.getProdutos();

        if(aux2!=null){
            for(int i = 0;i<aux2.size();i++){

                estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("produtos")
                        .child(aux2.get(i))
                        .child("tamanhosEquantidades");
                final int finalI = i;
                estoqueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String,String> qtdAux;
                        qtdAux = (HashMap<String, String>) dataSnapshot.getValue();

                        Log.i("OOOT0: ",String.valueOf(qtdAux));

                        int auxEstoqueP,
                            auxEstoqueM,
                            auxEstoqueG,
                            auxEstoqueGG,
                            auxEstoqueU,
                            auxEstoque1P,
                            auxEstoque1M,
                            auxEstoque1G,
                            auxEstoque1GG,
                            auxEstoque1U;

                        auxEstoqueP = Integer.parseInt(qtdAux.get("P"));
                        auxEstoqueM = Integer.parseInt(qtdAux.get("M"));
                        auxEstoqueG = Integer.parseInt(qtdAux.get("G"));
                        auxEstoqueGG = Integer.parseInt(qtdAux.get("GG"));
                        auxEstoqueU = Integer.parseInt(qtdAux.get("U"));

                        HashMap<String,String> qtdAuxOK = aux1.get(aux2.get(finalI));

                        auxEstoque1P = Integer.parseInt(qtdAuxOK.get("P"));
                        auxEstoque1M = Integer.parseInt(qtdAuxOK.get("M"));
                        auxEstoque1G = Integer.parseInt(qtdAuxOK.get("G"));
                        auxEstoque1GG = Integer.parseInt(qtdAuxOK.get("GG"));
                        auxEstoque1U = Integer.parseInt(qtdAuxOK.get("U"));

                        HashMap<String,String> qtdAuxFim = new HashMap<>();

                        qtdAuxFim.put("P",String.valueOf(auxEstoqueP+auxEstoque1P));
                        qtdAuxFim.put("M",String.valueOf(auxEstoqueM+auxEstoque1M));
                        qtdAuxFim.put("G",String.valueOf(auxEstoqueG+auxEstoque1G));
                        qtdAuxFim.put("GG",String.valueOf(auxEstoqueGG+auxEstoque1GG));
                        qtdAuxFim.put("U",String.valueOf(auxEstoqueU+auxEstoque1U));




                        estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("produtos")
                                .child(aux2.get(finalI))
                                .child("tamanhosEquantidades");
                        estoqueRef.setValue(qtdAuxFim);

                        Log.i("OOOT0: ",String.valueOf(qtdAuxFim));


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
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

    public void atualizarListaProdutosEmVendas (){
        atualizarProdutosON = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoUsuarioOn")
                .child(ConfiguracaoFirebase.getIdUsuario());

        atualizarProdutosON.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    produtoVendidos.add(ds.getValue(ProdutoVendido.class));
                }

                atualizarVendaAux1ON = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("vendasPorUsuario")
                        .child(ConfiguracaoFirebase.getIdUsuario())
                        .child("idVenda");
                atualizarVendaAux1ON.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String idVenda;
                        idVenda = dataSnapshot.getValue(String.class);
                        atualizarVendaAux2ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("vendas")
                                .child(idVenda);
                        atualizarVendaAux2ON.child("ProdutosVendidos").removeValue();
                        for(int i=0;i<produtoVendidos.size();++i){
                            atualizarVendaAux2ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                    .child("vendas")
                                    .child(idVenda)
                                    .child("ProdutosVendidos").child(produtoVendidos.get(i).getId());
                            atualizarVendaAux2ON.setValue(produtoVendidos.get(i));

                            atualizarVendaAux2ON = ConfiguracaoFirebase.getFirebaseDatabase()
                                    .child("vendasPorEtapas")
                                    .child("PedidoOKAguardandoSeparacao")
                                    .child(idVenda)
                                    .child("ProdutosVendidos").child(produtoVendidos.get(i).getId());
                            atualizarVendaAux2ON.setValue(produtoVendidos.get(i));

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

    public void restaurarEstoqueItem(final ProdutoVendido produtoVendido){
        estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos")
                .child(produtoVendido.getId())
                .child("tamanhosEquantidades");

        estoqueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,String> qtdAuxOrin;
                qtdAuxOrin = (HashMap<String,String>) dataSnapshot.getValue();

                HashMap<String,String> qtdAux;
                qtdAux = produtoVendido.getTamanhosEquantidades();

                int auxEstoqueP,
                        auxEstoqueM,
                        auxEstoqueG,
                        auxEstoqueGG,
                        auxEstoqueU,
                        auxEstoque1P,
                        auxEstoque1M,
                        auxEstoque1G,
                        auxEstoque1GG,
                        auxEstoque1U;

                auxEstoqueP = Integer.parseInt(qtdAux.get("P"));
                auxEstoqueM = Integer.parseInt(qtdAux.get("M"));
                auxEstoqueG = Integer.parseInt(qtdAux.get("G"));
                auxEstoqueGG = Integer.parseInt(qtdAux.get("GG"));
                auxEstoqueU = Integer.parseInt(qtdAux.get("U"));



                auxEstoque1P = Integer.parseInt(qtdAuxOrin.get("P"));
                auxEstoque1M = Integer.parseInt(qtdAuxOrin.get("M"));
                auxEstoque1G = Integer.parseInt(qtdAuxOrin.get("G"));
                auxEstoque1GG = Integer.parseInt(qtdAuxOrin.get("GG"));
                auxEstoque1U = Integer.parseInt(qtdAuxOrin.get("U"));

                HashMap<String,String> qtdAuxFim = new HashMap<>();

                qtdAuxFim.put("P",String.valueOf(auxEstoqueP+auxEstoque1P));
                qtdAuxFim.put("M",String.valueOf(auxEstoqueM+auxEstoque1M));
                qtdAuxFim.put("G",String.valueOf(auxEstoqueG+auxEstoque1G));
                qtdAuxFim.put("GG",String.valueOf(auxEstoqueGG+auxEstoque1GG));
                qtdAuxFim.put("U",String.valueOf(auxEstoqueU+auxEstoque1U));

                DatabaseReference estoqueFimRef;

                estoqueFimRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("produtos")
                        .child(produtoVendido.getId())
                        .child("tamanhosEquantidades");
                estoqueFimRef.setValue(qtdAuxFim);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
