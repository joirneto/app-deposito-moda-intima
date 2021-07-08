package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.activity.MainActivity;
import com.example.depositomodaintimacliente.adapter.AdapterCarrinho;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.example.depositomodaintimacliente.helper.RecyclerItemClickListener;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    private DatabaseReference confirmarPedido3Ref;


    private DatabaseReference editarProdRef;


    private DatabaseReference atualizarEstoqueProdutosRef;

    private DatabaseReference produtoRef;

    private DatabaseReference estoqueRef;

    private AlertDialog.Builder dialogCancelCompra;
    private AlertDialog.Builder dialogCancelProd;

    private TextView valorFinal, quantidadeFinal;
    private Button botaoCancelar, botaoConfirmar;
    private Venda venda, vendaCancel;

    private ValueEventListener valueEventListenerCarrinho;

    private BottomNavigationViewEx bottomNavigationViewExCarrinho;



    //------------------------ON CREATE-------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        initAtributesCarrinhoTotal();
        dialogCancelCompra =new AlertDialog.Builder(this);
        dialogCancelProd =new AlertDialog.Builder(this);


        //RECUPERANDO DADOS DE VENDA
        vendaRef = ConfiguracaoFirebase.getFirebaseDatabase().child("vendasClientesGeral")
                .child(recupTexto());
        vendaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Venda.class)!=null){
                    venda = dataSnapshot.getValue(Venda.class);

                    //Configurações Tela
                    valorFinal.setText("Valor Total: "+venda.getTotalValor());
                    quantidadeFinal.setText("Qtd Total: "+ venda.getTotalItens());


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        produtoslistaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoClienteExterno")
                .child(recupTexto());
        recyclerViewCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCarrinho.setHasFixedSize(true);

        adapterCarrinho = new AdapterCarrinho(produtos, this);
        recyclerViewCarrinho.setAdapter(adapterCarrinho);

        //Configuração Buttom Navigation
        configuracaoBottomNavigation();


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
                                                .child("vendaProdutoClienteExterno")
                                                .child(recupTexto());
                                        produtoRef.child(produtoSelecionado.getId()).removeValue();

                                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasClientesGeral")
                                                .child(recupTexto());
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
                                                        .child("vendasClientesGeral");

                                                vendaRef.child(recupTexto())
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
                                                .child("vendaProdutoClienteExterno")
                                                .child(recupTexto());
                                        produtoRef.child(produtoSelecionado.getId()).removeValue();

                                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasClientesGeral")
                                                .child(recupTexto());
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
                                                        .child("vendasClientesGeral");

                                                vendaRef.child(recupTexto())
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




    }//------------------------FIM ON CREATE-------------------------------

    //Método para inicializar os atributos
    public void initAtributesCarrinhoTotal(){
        recyclerViewCarrinho = findViewById(R.id.idRecyclerCarrinho);
        valorFinal = findViewById(R.id.idValorFinal);
        quantidadeFinal = findViewById(R.id.idQuantidadeFinal);
        botaoCancelar = findViewById(R.id.idBotaoCancelarVenda);
        botaoConfirmar = findViewById(R.id.idBotaoFinalizarVenda);
        progressBarCarrinho = findViewById(R.id.idprogressBarCarrinho);
        bottomNavigationViewExCarrinho = findViewById(R.id.idBottomNavigationTabCarrinho);


    }// fim initAtributesCarrinhoTotal()

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
    public void setLimparDados(){



        limparDados =ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendaProdutoClienteExterno");
        limparDados.child(recupTexto()).removeValue();

    }

    public void confirmarPedido(){

        Intent intent = new Intent(getApplicationContext(),RegistrarDadosClienteEntregaActivity.class);
        intent.putExtra("vendaSelecionada", venda);
        startActivity(intent);
        finish();



    }

    public void cancelarPedido(){

        setLimparDados();
        restaurarEstoque();

        iniciarVenda();

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);

        startActivity(intent);
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

    public void gravarArquivoVendaNova(String idVenda){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("IDVenda.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(idVenda);
            outputStreamWriter.close();
        }catch (IOException e){
            Log.v("CarrinhoActivity",e.toString());
        }
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
        bottomNavigationViewExCarrinho.enableAnimation(false);
        bottomNavigationViewExCarrinho.enableItemShiftingMode(true);
        bottomNavigationViewExCarrinho.enableShiftingMode(false);
        bottomNavigationViewExCarrinho.setTextVisibility(true);
        habilitarNavegacaoCliente(bottomNavigationViewExCarrinho);

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
                        if(recupTextoDadosClienteCarrinho().isEmpty()){
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


    private String recupTextoDadosClienteCarrinho(){
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

}//------------------------FIM-------------------------------
