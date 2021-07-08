package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Produto;
import com.example.depositomodaintima.model.ProdutoVendido;
import com.example.depositomodaintima.model.Venda;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.depositomodaintima.R.*;

public class ProdutoDetalhesActivity extends AppCompatActivity {
    private TextView tituloProdutoDetalhe, precoProdutoDetalhe,
            descricaoProdutoDetalhe,
            valorTotalItem, valorTotalCarrinho,
            totalItensCarrinhoDetalhe, totalCarrinhoDetalhe, textoGanho, textoQtdVendido, textoTotalVendido,
            tamanhoPProdDet, tamanhoMProdDet, tamanhoGProdDet, tamanhoGGProdDet, tamanhoUProdDet,
            tamanhoPProdDetTexto, tamanhoMProdDetTexto, tamanhoGProdDetTexto, tamanhoGGProdDetTexto, tamanhoUProdDetTexto;

    private TextView minusP, minusM, minusG, minusGG, minusU,
                    addP, addM, addG, addGG, addU;

    private EditText qtdAtualP, qtdAtualM, qtdAtualG, qtdAtualGG, qtdAtualU;

    private CarouselView carouselView;
    private Produto produtoSelecionado;
    private DatabaseReference vendaOn, adminRef;
    private LinearLayout carrinho;
    private LinearLayout addCompras;
    private Button botaoAddProdCarrinho;
    private ProdutoVendido produtos;
    private List<String> listaProdutos;
    private HashMap<String,HashMap<String,String>> produtoEquantidade;

    private Venda venda;

    private DatabaseReference produtoOn;
    private DatabaseReference produtoGravar;
    private DatabaseReference produtoRef;
    private DatabaseReference estoqueRef;
    private HashMap<String,String> qtdAux = new HashMap<>();

    private LinearLayout detalhesGanho;
    private LinearLayout estoqueProdutoDetalhe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_produto_detalhes);

         getSupportActionBar().setTitle("Produto em Detalhe");

        //INICIANDO OS ATRIBUTOS
        initAtributesProdutoDetalhes();

        //CONFIGURANDO TELA ADMIN/USER
        vendaOn = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(ConfiguracaoFirebase.getIdUsuario())
                .child("vendendo");
        vendaOn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String status;
                status = dataSnapshot.getValue().toString();
                if (status.equals("NAO")) {
                    carrinho.setVisibility(View.GONE);
                    addCompras.setVisibility(View.GONE);
                    estoqueProdutoDetalhe.setVisibility(View.VISIBLE);

                    adminRef = ConfiguracaoFirebase.getFirebaseDatabase()
                            .child("usuarios")
                            .child(ConfiguracaoFirebase.getIdUsuario());

                    adminRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String aux;
                            aux = dataSnapshot.getValue(String.class);

                            if(aux.equals("SIM")){
                                detalhesGanho.setVisibility(View.VISIBLE);
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
        });//FIM CONFIGURANDO TELA ADMIN/USER

        //CONFIGURANDO DADOS DO PRODUTOS
        produtoSelecionado = (Produto) getIntent().getSerializableExtra("produtoSelecionado");
        if(produtoSelecionado!=null){
            tituloProdutoDetalhe.setText(produtoSelecionado.getTitulo());
            precoProdutoDetalhe.setText(produtoSelecionado.getPreco());
            descricaoProdutoDetalhe.setText(produtoSelecionado.getDescricao());
            textoGanho.setText("Ganho por unidade: "+produtoSelecionado.getGanho());
            textoQtdVendido.setText("Quantidade Vendido: "+produtoSelecionado.getQtdVendidos());
            valorTotalItem.setText("R$0,00");

            //Atualizar GANHO por produto
            String precoAtualizado1;
            String precoAtualizado2;
            float precoAux1, precoAux2;
            float precoOK;
            precoAtualizado1 = produtoSelecionado.getGanho().replace("R$", "");
            precoAtualizado2 = precoAtualizado1.replace(",", ".");
            precoAux1 = Float.parseFloat(precoAtualizado2);
            precoAux2 = Float.parseFloat(produtoSelecionado.getQtdVendidos());
            precoOK = precoAux1* precoAux2;
            DecimalFormat df = new DecimalFormat("0.00");
            textoTotalVendido.setText("Ganho TOTAL: "+String.valueOf(df.format(precoOK)));
            //FIM Atualizar GANHO por produto

            //CONFIGURANDO OS TAMANHOS DE ACORDO COM O ESTOQUE
            estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("produtos")
                    .child(produtoSelecionado.getIdProduto());
            estoqueRef.child("tamanhosEquantidades").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    HashMap<String,String> qtdAux;
                    qtdAux = (HashMap<String, String>) dataSnapshot.getValue();

                    tamanhoPProdDet.setText(qtdAux.get("P"));
                    if(tamanhoPProdDet.getText().toString().equals("0")){
                        addP.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_add_cinza,0,0,0);
                        addP.setEnabled(false);
                        qtdAtualP.setEnabled(false);
                        tamanhoPProdDet.setVisibility(View.GONE);
                        tamanhoPProdDetTexto.setVisibility(View.GONE);
                    }

                    tamanhoMProdDet.setText(qtdAux.get("M"));
                    if(tamanhoMProdDet.getText().toString().equals("0")){
                        addM.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_add_cinza,0,0,0);
                        addM.setEnabled(false);
                        qtdAtualM.setEnabled(false);
                        tamanhoMProdDet.setVisibility(View.GONE);
                        tamanhoMProdDetTexto.setVisibility(View.GONE);
                    }

                    tamanhoGProdDet.setText(qtdAux.get("G"));
                    if(tamanhoGProdDet.getText().toString().equals("0")){
                        addG.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_add_cinza,0,0,0);
                        addG.setEnabled(false);
                        qtdAtualG.setEnabled(false);
                        tamanhoGProdDet.setVisibility(View.GONE);
                        tamanhoGProdDetTexto.setVisibility(View.GONE);
                    }

                    tamanhoGGProdDet.setText(qtdAux.get("GG"));
                    if(tamanhoGGProdDet.getText().toString().equals("0")){
                        addGG.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_add_cinza,0,0,0);
                        addGG.setEnabled(false);
                        qtdAtualGG.setEnabled(false);
                        tamanhoGGProdDet.setVisibility(View.GONE);
                        tamanhoGGProdDetTexto.setVisibility(View.GONE);
                    }

                    tamanhoUProdDet.setText(qtdAux.get("U"));
                    if(tamanhoUProdDet.getText().toString().equals("0")){
                        addU.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_add_cinza,0,0,0);
                        addU.setEnabled(false);
                        qtdAtualU.setEnabled(false);
                        tamanhoUProdDet.setVisibility(View.GONE);
                        tamanhoUProdDetTexto.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            }); //FIM CONFIGURANDO OS TAMANHOS DE ACORDO COM O ESTOQUE

            //CONFIGURANDO IMAGEM
            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = produtoSelecionado.getListaFotos().get(position);
                    Picasso.get().load(urlString).fit().into(imageView);
                }
            };
            carouselView.setPageCount(produtoSelecionado.getListaFotos().size());
            carouselView.setImageListener(imageListener);
        }//FIM CONFIGURANDO DADOS DO PRODUTOS

        //Carregando Tamanhos para edição de produtos
        HashMap<String,String > hashMapEdit;
        hashMapEdit = (HashMap<String, String>) getIntent().getSerializableExtra("tamanhos");
        if (hashMapEdit!=null){
            qtdAtualP.setText(hashMapEdit.get("P"));
            qtdAtualM.setText(hashMapEdit.get("M"));
            qtdAtualG.setText(hashMapEdit.get("G"));
            qtdAtualGG.setText(hashMapEdit.get("GG"));
            qtdAtualU.setText(hashMapEdit.get("U"));

            atualizarEdicao();
        }
        //FIM Carregando Tamanhos para edição de produtos


        bloquearRemove();

        vendaOn = ConfiguracaoFirebase.getFirebaseDatabase().child("vendasPorUsuario").child(ConfiguracaoFirebase.getIdUsuario());

        vendaOn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                venda = dataSnapshot.getValue(Venda.class);
                if(venda!=null) {

                    totalItensCarrinhoDetalhe.setText(venda.getTotalItens());
                    totalCarrinhoDetalhe.setText(venda.getTotalValor());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //ATUALZIANDO VALORES DIGITADOS
        atualizarValorDigitadoP();
        atualizarValorDigitadoM();
        atualizarValorDigitadoG();
        atualizarValorDigitadoGG();
        atualizarValorDigitadoU();

        //CLICANDO NO ADDPRODUTO
        botaoAddProdCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificarMenorZero()&&verificarValorNegativo()) {
                    estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                            .child("produtos")
                            .child(produtoSelecionado.getIdProduto());
                    estoqueRef.child("tamanhosEquantidades").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HashMap<String,String> qtdAux;
                            qtdAux = (HashMap<String, String>) dataSnapshot.getValue();
                            final int aux1,
                                    aux2P,
                                    aux2M,
                                    aux2G,
                                    aux2GG,
                                    aux2U,
                                    auxEstoqueP,
                                    auxEstoqueM,
                                    auxEstoqueG,
                                    auxEstoqueGG,
                                    auxEstoqueU,
                                    totalAux;
                            auxEstoqueP = Integer.parseInt(qtdAux.get("P"));
                            auxEstoqueM = Integer.parseInt(qtdAux.get("M"));
                            auxEstoqueG = Integer.parseInt(qtdAux.get("G"));
                            auxEstoqueGG = Integer.parseInt(qtdAux.get("GG"));
                            auxEstoqueU = Integer.parseInt(qtdAux.get("U"));

                            aux1 = Integer.parseInt(venda.getTotalItens());

                            converterZeroProdDet();

                            aux2P = Integer.parseInt(qtdAtualP.getText().toString());
                            aux2M = Integer.parseInt(qtdAtualM.getText().toString());
                            aux2G = Integer.parseInt(qtdAtualG.getText().toString());
                            aux2GG = Integer.parseInt(qtdAtualGG.getText().toString());
                            aux2U = Integer.parseInt(qtdAtualU.getText().toString());

                            if (aux2P > auxEstoqueP||
                                aux2M > auxEstoqueM||
                                aux2G > auxEstoqueG||
                                aux2GG > auxEstoqueGG||
                                aux2U > auxEstoqueU) {

                                Toast.makeText(ProdutoDetalhesActivity.this,
                                        "Quantidade selecionada É MAIOR que a do ESTOQUE!",
                                        Toast.LENGTH_SHORT).show();
                                zeraCampos();

                            } else {
                                int aux2Todos;
                                aux2Todos = aux2P + aux2M + aux2G + aux2GG + aux2U;
                                totalAux = aux1 + aux2Todos;
                                venda.setTotalItens(String.valueOf(totalAux));
                                totalItensCarrinhoDetalhe.setText(String.valueOf(totalAux));


                                String precoAtualizado1, precoAtualizado2;
                                float precoAux1, precoAux2, precoOK;

                                precoAux1 = Float.parseFloat(venda.getTotalValor().replace(",", "."));
                                precoAtualizado1 = valorTotalItem.getText().toString().replace("R$", "");
                                precoAtualizado2 = precoAtualizado1.replace(",", ".");
                                precoAux2 = Float.parseFloat(precoAtualizado2);
                                DecimalFormat df = new DecimalFormat("0.00");
                                precoOK = precoAux1 + precoAux2;
                                venda.setTotalValor(df.format(precoOK));
                                totalCarrinhoDetalhe.setText(df.format(precoOK));


                                produtoOn = ConfiguracaoFirebase.getFirebaseDatabase()
                                        .child("vendaProdutoUsuarioOn")
                                        .child(ConfiguracaoFirebase.getIdUsuario())
                                        .child(produtoSelecionado.getIdProduto());

                                produtoOn.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        produtos = dataSnapshot.getValue(ProdutoVendido.class);
                                        if (produtos != null) {

                                            String auxP,
                                                auxM,
                                                auxG,
                                                auxGG,
                                                auxU;

                                            int auxI1P,
                                                auxI1M,
                                                auxI1G,
                                                auxI1GG,
                                                auxI1U,

                                                auxI2P,
                                                auxI2M,
                                                auxI2G,
                                                auxI2GG,
                                                auxI2U;

                                            auxI1P = Integer.parseInt(produtos.getTamanhosEquantidades().get("P"));
                                            auxI1M = Integer.parseInt(produtos.getTamanhosEquantidades().get("M"));
                                            auxI1G = Integer.parseInt(produtos.getTamanhosEquantidades().get("G"));
                                            auxI1GG = Integer.parseInt(produtos.getTamanhosEquantidades().get("GG"));
                                            auxI1U = Integer.parseInt(produtos.getTamanhosEquantidades().get("U"));

                                            auxI2P = Integer.parseInt(qtdAtualP.getText().toString());
                                            auxI2M = Integer.parseInt(qtdAtualM.getText().toString());
                                            auxI2G = Integer.parseInt(qtdAtualG.getText().toString());
                                            auxI2GG = Integer.parseInt(qtdAtualGG.getText().toString());
                                            auxI2U = Integer.parseInt(qtdAtualU.getText().toString());


                                            auxP = String.valueOf(auxI1P + auxI2P);
                                            auxM = String.valueOf(auxI1M + auxI2M);
                                            auxG = String.valueOf(auxI1G + auxI2G);
                                            auxGG = String.valueOf(auxI1GG + auxI2GG);
                                            auxU = String.valueOf(auxI1U + auxI2U);

                                            HashMap<String,String> hashMapAux = produtos.getTamanhosEquantidades();

                                            hashMapAux.put("P", auxP);
                                            hashMapAux.put("M", auxM);
                                            hashMapAux.put("G", auxG);
                                            hashMapAux.put("GG", auxGG);
                                            hashMapAux.put("U", auxU);

                                            produtos.setTamanhosEquantidades(hashMapAux);

                                            produtoEquantidade = venda.getProdutoEquantidade();
                                            produtoEquantidade.put(produtoSelecionado.getIdProduto(), hashMapAux);
                                            venda.setProdutoEquantidade(produtoEquantidade);


                                        } else {

                                            produtos = new ProdutoVendido();
                                            HashMap<String,String> hashMapAux = new HashMap<>();

                                            hashMapAux.put("P", qtdAtualP.getText().toString());
                                            hashMapAux.put("M", qtdAtualM.getText().toString());
                                            hashMapAux.put("G", qtdAtualG.getText().toString());
                                            hashMapAux.put("GG", qtdAtualGG.getText().toString());
                                            hashMapAux.put("U", qtdAtualU.getText().toString());
                                            produtos.setTamanhosEquantidades(hashMapAux);

                                            produtos.setTitulo(produtoSelecionado.getTitulo());
                                            produtos.setPreco(produtoSelecionado.getPreco());
                                            produtos.setId(produtoSelecionado.getIdProduto());
                                            produtos.setFoto(produtoSelecionado.getListaFotos().get(0));

                                            if (venda.getProdutos() != null) {
                                                listaProdutos = venda.getProdutos();
                                                listaProdutos.add(produtoSelecionado.getIdProduto());
                                                venda.setProdutos(listaProdutos);
                                            } else {
                                                listaProdutos = new ArrayList<>();
                                                listaProdutos.add(produtoSelecionado.getIdProduto());
                                                venda.setProdutos(listaProdutos);
                                            }

                                            if (venda.getProdutoEquantidade() != null) {
                                                produtoEquantidade = venda.getProdutoEquantidade();
                                                produtoEquantidade.put(produtoSelecionado.getIdProduto(), hashMapAux);
                                                venda.setProdutoEquantidade(produtoEquantidade);
                                            } else {
                                                produtoEquantidade = new HashMap<>();
                                                produtoEquantidade.put(produtoSelecionado.getIdProduto(), hashMapAux);
                                                venda.setProdutoEquantidade(produtoEquantidade);
                                            }


                                        }
                                        int auxI1P = Integer.parseInt(tamanhoPProdDet.getText().toString());
                                        int auxI1M = Integer.parseInt(tamanhoMProdDet.getText().toString());
                                        int auxI1G = Integer.parseInt(tamanhoGProdDet.getText().toString());
                                        int auxI1GG = Integer.parseInt(tamanhoGGProdDet.getText().toString());
                                        int auxI1U = Integer.parseInt(tamanhoUProdDet.getText().toString());

                                        int auxI2P = Integer.parseInt(qtdAtualP.getText().toString());
                                        int auxI2M = Integer.parseInt(qtdAtualM.getText().toString());
                                        int auxI2G = Integer.parseInt(qtdAtualG.getText().toString());
                                        int auxI2GG = Integer.parseInt(qtdAtualGG.getText().toString());
                                        int auxI2U = Integer.parseInt(qtdAtualU.getText().toString());


                                        int auxOKP = auxI1P - auxI2P;
                                        int auxOKM = auxI1M - auxI2M;
                                        int auxOKG = auxI1G - auxI2G;
                                        int auxOKGG = auxI1GG - auxI2GG;
                                        int auxOKU = auxI1U - auxI2U;

                                        HashMap<String,String> hashMap = new HashMap<>();

                                        hashMap.put("P", String.valueOf(auxOKP));
                                        hashMap.put("M", String.valueOf(auxOKM));
                                        hashMap.put("G", String.valueOf(auxOKG));
                                        hashMap.put("GG", String.valueOf(auxOKGG));
                                        hashMap.put("U", String.valueOf(auxOKU));

                                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("produtos")
                                                .child(produtoSelecionado.getIdProduto());
                                        produtoRef.child("tamanhosEquantidades").setValue(hashMap);

                                        produtoGravar = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendaProdutoUsuarioOn")
                                                .child(ConfiguracaoFirebase.getIdUsuario())
                                                .child(produtoSelecionado.getIdProduto());
                                        produtoGravar.setValue(produtos);

                                        vendaOn = ConfiguracaoFirebase.getFirebaseDatabase()
                                                .child("vendasPorUsuario")
                                                .child(ConfiguracaoFirebase.getIdUsuario());
                                        vendaOn.setValue(venda);
                                        zeraCampos();
                                        Toast.makeText(ProdutoDetalhesActivity.this, "Produto adicionado com Sucesso!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                                        finish();


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
                }else{
                    zeraCampos();
                }
            }
        });

    }

    private void initAtributesProdutoDetalhes(){
        tituloProdutoDetalhe = findViewById(id.idTituloProdutoDetalhe);
        precoProdutoDetalhe = findViewById(id.idPrecoProdutoDetalhe);
        descricaoProdutoDetalhe = findViewById(id.idDescricaoProdutoDetalhe);
        carouselView = findViewById(id.idFotoProdutoDetalhe);
        carrinho = findViewById(id.idCarrinhoDetalhe);
        addCompras = findViewById(id.idAddCompras);
        valorTotalItem = findViewById(id.idValorTotalItem);
        valorTotalCarrinho = findViewById(id.idTotalCarrinhoDetalhe);
        botaoAddProdCarrinho  =findViewById(id.idBotaoAdicionarProdCarrinho);
        totalItensCarrinhoDetalhe = findViewById(id.idTotalItensCarrinhoDetalhe);
        totalCarrinhoDetalhe = findViewById(id.idTotalCarrinhoDetalhe);
        detalhesGanho = findViewById(id.idDetalhesProduto);
        textoGanho = findViewById(id.idGanhoProdutoDetalhe);
        textoQtdVendido = findViewById(id.idQtdVendidaProdutoDetalhe);
        textoTotalVendido = findViewById(id.idTotalVendidoProdutoDetalhe);

        estoqueProdutoDetalhe = findViewById(id.idEstoqueProdutosDetalhes);

        tamanhoPProdDet = findViewById(id.idquantidadePProdDetalhe);
        tamanhoMProdDet = findViewById(id.idquantidadeMProdDetalhe);
        tamanhoGProdDet = findViewById(id.idquantidadeGProdDetalhe);
        tamanhoGGProdDet = findViewById(id.idquantidadeGGProdDetalhe);
        tamanhoUProdDet = findViewById(id.idquantidadeUProdDetalhe);

        minusP = findViewById(id.idMinusP);
        minusM = findViewById(id.idMinusM);
        minusG = findViewById(id.idMinusG);
        minusGG = findViewById(id.idMinusGG);
        minusU = findViewById(id.idMinusU);

        addP = findViewById(id.idAddP);
        addM = findViewById(id.idAddM);
        addG = findViewById(id.idAddG);
        addGG = findViewById(id.idAddGG);
        addU = findViewById(id.idAddU);

        qtdAtualP = findViewById(id.idquantidadeAtualP);
        qtdAtualM = findViewById(id.idquantidadeAtualM);
        qtdAtualG = findViewById(id.idquantidadeAtualG);
        qtdAtualGG = findViewById(id.idquantidadeAtualGG);
        qtdAtualU = findViewById(id.idquantidadeAtualU);

        tamanhoPProdDetTexto = findViewById(id.idquantidadePProdDetalheTexto);
        tamanhoMProdDetTexto = findViewById(id.idquantidadeMProdDetalheTexto);
        tamanhoGProdDetTexto = findViewById(id.idquantidadeGProdDetalheTexto);
        tamanhoGGProdDetTexto = findViewById(id.idquantidadeGGProdDetalheTexto);
        tamanhoUProdDetTexto = findViewById(id.idquantidadeUProdDetalheTexto);



    }

    public void removeProdutoP(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iG, iGG, iU;
        a = Integer.parseInt(qtdAtualP.getText().toString());
        a--;
        qtdAtualP.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iM + iG + iGG + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void adicionaProdutoP(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iG, iGG, iU;
        a = Integer.parseInt(qtdAtualP.getText().toString());
        a++;
        qtdAtualP.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iM + iG + iGG + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void bloquearRemove(){

        //TAMANHO P
        if(qtdAtualP.getText().toString().equals("0")){
            minusP.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_cinza_24dp,0,0,0);
            minusP.setEnabled(false);
        }
        else{
            minusP.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_pink_24dp,0,0,0);
            minusP.setEnabled(true);
        }

        //TAMANHO M
        if(qtdAtualM.getText().toString().equals("0")){
            minusM.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_cinza_24dp,0,0,0);
            minusM.setEnabled(false);
        }
        else{
            minusM.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_pink_24dp,0,0,0);
            minusM.setEnabled(true);
        }

        //TAMANHO G
        if(qtdAtualG.getText().toString().equals("0")){
            minusG.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_cinza_24dp,0,0,0);
            minusG.setEnabled(false);
        }
        else{
            minusG.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_pink_24dp,0,0,0);
            minusG.setEnabled(true);
        }

        //TAMANHO GG
        if(qtdAtualGG.getText().toString().equals("0")){
            minusGG.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_cinza_24dp,0,0,0);
            minusGG.setEnabled(false);
        }
        else{
            minusGG.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_pink_24dp,0,0,0);
            minusGG.setEnabled(true);
        }

        //TAMANHO U
        if(qtdAtualU.getText().toString().equals("0")){
            minusU.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_cinza_24dp,0,0,0);
            minusU.setEnabled(false);
        }
        else{
            minusU.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_remove_pink_24dp,0,0,0);
            minusU.setEnabled(true);
        }


    }
    public void abrirCarrinhoDetalhe(View view){
        startActivity(new Intent(getApplicationContext(),CarrinhoActivity.class));
        finish();

    }

    public void zeraCampos(){
        qtdAtualP.setText("0");
        qtdAtualM.setText("0");
        qtdAtualG.setText("0");
        qtdAtualGG.setText("0");
        qtdAtualU.setText("0");
        valorTotalItem.setText("R$0,00");
        bloquearRemove();
    }

    public boolean verificarMenorZero(){
        if(
                qtdAtualP.getText().toString().isEmpty()||
                qtdAtualM.getText().toString().isEmpty()||
                qtdAtualG.getText().toString().isEmpty()||
                qtdAtualGG.getText().toString().isEmpty()||
                qtdAtualU.getText().toString().isEmpty()||

                qtdAtualP.getText().toString().equals("0")&&
                qtdAtualM.getText().toString().equals("0")&&
                qtdAtualG.getText().toString().equals("0")&&
                qtdAtualGG.getText().toString().equals("0")&&
                qtdAtualU.getText().toString().equals("0")
        ){
            Toast.makeText(this,
                    "Uma das quantidades informadas é nula\nOU\nTodas as quandidades está zero!\nFavor digitar quantidade correta.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Método para verificar se valor é branco
    public boolean verificarValorBranco(){
        if(
                qtdAtualP.getText().toString().isEmpty()||
                        qtdAtualM.getText().toString().isEmpty()||
                        qtdAtualG.getText().toString().isEmpty()||
                        qtdAtualGG.getText().toString().isEmpty()||
                        qtdAtualU.getText().toString().isEmpty()
        ){
            Toast.makeText(this,
                    "Uma das quantidades informadas é nula!\nFavor digitar quantidade correta.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }//fim verificarValorBranco()

    //Método para verificar se valor é negativo
    public boolean verificarValorNegativo(){
        if(
                qtdAtualP.getText().toString().isEmpty()||
                        qtdAtualM.getText().toString().isEmpty()||
                        qtdAtualG.getText().toString().isEmpty()||
                        qtdAtualGG.getText().toString().isEmpty()||
                        qtdAtualU.getText().toString().isEmpty()
        ){
            Toast.makeText(this,
                    "Uma das quantidades informadas é nula!\nFavor digitar quantidade correta.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else{
            int numP = Integer.parseInt(qtdAtualP.getText().toString());
            int numM = Integer.parseInt(qtdAtualM.getText().toString());
            int numG = Integer.parseInt(qtdAtualG.getText().toString());
            int numGG = Integer.parseInt(qtdAtualGG.getText().toString());
            int numU = Integer.parseInt(qtdAtualU.getText().toString());

            if(numP<0||numM<0||numG<0||numGG<0||numU<0){
                Toast.makeText(this,
                        "Uma das quantidades informadas é negativa!\nFavor digitar quantidade correta.",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

    }//fim verificarValorNegativo()


    //atualizarValorDigitadoP()
    public void atualizarValorDigitadoP(){
        qtdAtualP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String precoAtualizado1;
                String precoAtualizado2;
                float precoAux;
                float precoOK;

                int i, iP, iM, iG, iGG, iU;
                if(verificarValorBranco()) {
                    iP = Integer.parseInt(qtdAtualP.getText().toString());
                    bloquearRemove();

                    precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$", "");
                    precoAtualizado2 = precoAtualizado1.replace(",", ".");
                    precoAux = Float.parseFloat(precoAtualizado2);

                    iM= Integer.parseInt(qtdAtualM.getText().toString());
                    iG= Integer.parseInt(qtdAtualG.getText().toString());
                    iGG= Integer.parseInt(qtdAtualGG.getText().toString());
                    iU= Integer.parseInt(qtdAtualU.getText().toString());

                    i = iP + iM + iG + iGG + iU;

                    precoOK = ((float) i) * precoAux;

                    DecimalFormat df = new DecimalFormat("0.00");

                    valorTotalItem.setText(String.valueOf(df.format(precoOK)));
                }
                else{

                    Toast.makeText(ProdutoDetalhesActivity.this,
                            "Quantidade informada é nula\nOU\n" +
                                    "Favor digitar quantidade correta.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }//fim atualizarValorDigitadoP()

    //atualizarValorDigitadoM()
    public void atualizarValorDigitadoM(){
        qtdAtualM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String precoAtualizado1;
                String precoAtualizado2;
                float precoAux;
                float precoOK;

                int i, iP, iM, iG, iGG, iU;
                if(verificarValorBranco()) {
                    iM = Integer.parseInt(qtdAtualM.getText().toString());
                    bloquearRemove();

                    precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$", "");
                    precoAtualizado2 = precoAtualizado1.replace(",", ".");
                    precoAux = Float.parseFloat(precoAtualizado2);

                    iP= Integer.parseInt(qtdAtualP.getText().toString());
                    iG= Integer.parseInt(qtdAtualG.getText().toString());
                    iGG= Integer.parseInt(qtdAtualGG.getText().toString());
                    iU= Integer.parseInt(qtdAtualU.getText().toString());

                    i = iP + iM + iG + iGG + iU;

                    precoOK = ((float) i) * precoAux;

                    DecimalFormat df = new DecimalFormat("0.00");

                    valorTotalItem.setText(String.valueOf(df.format(precoOK)));
                }
                else{

                    Toast.makeText(ProdutoDetalhesActivity.this,
                            "Quantidade informada é nula\nOU\n" +
                                    "Favor digitar quantidade correta.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }//fim atualizarValorDigitadoM()

    //atualizarValorDigitadoP()
    public void atualizarValorDigitadoG(){
        qtdAtualG.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String precoAtualizado1;
                String precoAtualizado2;
                float precoAux;
                float precoOK;

                int i, iP, iM, iG, iGG, iU;
                if(verificarValorBranco()) {
                    iG = Integer.parseInt(qtdAtualG.getText().toString());
                    bloquearRemove();

                    precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$", "");
                    precoAtualizado2 = precoAtualizado1.replace(",", ".");
                    precoAux = Float.parseFloat(precoAtualizado2);

                    iP= Integer.parseInt(qtdAtualP.getText().toString());
                    iM= Integer.parseInt(qtdAtualM.getText().toString());
                    iGG= Integer.parseInt(qtdAtualGG.getText().toString());
                    iU= Integer.parseInt(qtdAtualU.getText().toString());

                    i = iP + iM + iG + iGG + iU;

                    precoOK = ((float) i) * precoAux;

                    DecimalFormat df = new DecimalFormat("0.00");

                    valorTotalItem.setText(String.valueOf(df.format(precoOK)));
                }
                else{

                    Toast.makeText(ProdutoDetalhesActivity.this,
                            "Quantidade informada é nula\nOU\n" +
                                    "Favor digitar quantidade correta.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }//fim atualizarValorDigitadoG()

    //atualizarValorDigitadoGG()
    public void atualizarValorDigitadoGG(){
        qtdAtualGG.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String precoAtualizado1;
                String precoAtualizado2;
                float precoAux;
                float precoOK;

                int i, iP, iM, iG, iGG, iU;
                if(verificarValorBranco()) {
                    iGG = Integer.parseInt(qtdAtualGG.getText().toString());
                    bloquearRemove();

                    precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$", "");
                    precoAtualizado2 = precoAtualizado1.replace(",", ".");
                    precoAux = Float.parseFloat(precoAtualizado2);

                    iP= Integer.parseInt(qtdAtualP.getText().toString());
                    iM= Integer.parseInt(qtdAtualM.getText().toString());
                    iG= Integer.parseInt(qtdAtualG.getText().toString());
                    iU= Integer.parseInt(qtdAtualU.getText().toString());

                    i = iP + iM + iG + iGG + iU;

                    precoOK = ((float) i) * precoAux;

                    DecimalFormat df = new DecimalFormat("0.00");

                    valorTotalItem.setText(String.valueOf(df.format(precoOK)));
                }
                else{

                    Toast.makeText(ProdutoDetalhesActivity.this,
                            "Quantidade informada é nula\nOU\n" +
                                    "Favor digitar quantidade correta.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }//fim atualizarValorDigitadoGG()

    //atualizarValorDigitadoU()
    public void atualizarValorDigitadoU(){
        qtdAtualU.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String precoAtualizado1;
                String precoAtualizado2;
                float precoAux;
                float precoOK;

                int i, iP, iM, iG, iGG, iU;
                if(verificarValorBranco()) {
                    iU = Integer.parseInt(qtdAtualU.getText().toString());
                    bloquearRemove();

                    precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$", "");
                    precoAtualizado2 = precoAtualizado1.replace(",", ".");
                    precoAux = Float.parseFloat(precoAtualizado2);

                    iM= Integer.parseInt(qtdAtualM.getText().toString());
                    iG= Integer.parseInt(qtdAtualG.getText().toString());
                    iGG= Integer.parseInt(qtdAtualGG.getText().toString());
                    iP= Integer.parseInt(qtdAtualP.getText().toString());

                    i = iP + iM + iG + iGG + iU;

                    precoOK = ((float) i) * precoAux;

                    DecimalFormat df = new DecimalFormat("0.00");

                    valorTotalItem.setText(String.valueOf(df.format(precoOK)));
                }
                else{

                    Toast.makeText(ProdutoDetalhesActivity.this,
                            "Quantidade informada é nula\nOU\n" +
                                    "Favor digitar quantidade correta.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
    }//fim atualizarValorDigitadoU()

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }

    public void removeProdutoM(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iP, iG, iGG, iU;
        a = Integer.parseInt(qtdAtualM.getText().toString());
        a--;
        qtdAtualM.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iP= Integer.parseInt(qtdAtualP.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iP + iG + iGG + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void adicionaProdutoM(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iP, iG, iGG, iU;
        a = Integer.parseInt(qtdAtualM.getText().toString());
        a++;
        qtdAtualM.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iP= Integer.parseInt(qtdAtualP.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iP + iG + iGG + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void removeProdutoG(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iP, iGG, iU;
        a = Integer.parseInt(qtdAtualG.getText().toString());
        a--;
        qtdAtualG.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iP= Integer.parseInt(qtdAtualP.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iM + iP + iGG + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void adicionaProdutoG(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iP, iGG, iU;
        a = Integer.parseInt(qtdAtualG.getText().toString());
        a++;
        qtdAtualG.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iP= Integer.parseInt(qtdAtualP.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iM + iP + iGG + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void removeProdutoGG(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iG, iP, iU;
        a = Integer.parseInt(qtdAtualGG.getText().toString());
        a--;
        qtdAtualGG.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iP= Integer.parseInt(qtdAtualP.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iM + iG + iP + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void adicionaProdutoGG(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iG, iP, iU;
        a = Integer.parseInt(qtdAtualGG.getText().toString());
        a++;
        qtdAtualGG.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iP= Integer.parseInt(qtdAtualP.getText().toString());
        iU= Integer.parseInt(qtdAtualU.getText().toString());

        i = a + iM + iG + iP + iU;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void removeProdutoU(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iG, iGG, iP;
        a = Integer.parseInt(qtdAtualU.getText().toString());
        a--;
        qtdAtualU.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iP= Integer.parseInt(qtdAtualP.getText().toString());

        i = a + iM + iG + iGG + iP;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void adicionaProdutoU(View view){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int a, i, iM, iG, iGG, iP;
        a = Integer.parseInt(qtdAtualU.getText().toString());
        a++;
        qtdAtualU.setText(String.valueOf(a));
        bloquearRemove();

        precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$","");
        precoAtualizado2 = precoAtualizado1.replace(",",".");
        precoAux = Float.parseFloat(precoAtualizado2);



        iM= Integer.parseInt(qtdAtualM.getText().toString());
        iG= Integer.parseInt(qtdAtualG.getText().toString());
        iGG= Integer.parseInt(qtdAtualGG.getText().toString());
        iP= Integer.parseInt(qtdAtualP.getText().toString());

        i = a + iM + iG + iGG + iP;

        precoOK = ((float) i)*precoAux;


        DecimalFormat df = new DecimalFormat("0.00");

        valorTotalItem.setText(String.valueOf(df.format(precoOK)));

    }

    public void converterZeroProdDet(){

        if(qtdAtualP.getText().toString().equals("00")||
                qtdAtualP.getText().toString().equals("000")||
                qtdAtualP.getText().toString().equals("0000")||
                qtdAtualP.getText().toString().equals("00000")){
            qtdAtualP.setText("0");

        }

        if(qtdAtualM.getText().toString().equals("00")||
                qtdAtualM.getText().toString().equals("000")||
                qtdAtualM.getText().toString().equals("0000")||
                qtdAtualM.getText().toString().equals("00000")){
            qtdAtualM.setText("0");

        }

        if(qtdAtualG.getText().toString().equals("00")||
                qtdAtualG.getText().toString().equals("000")||
                qtdAtualG.getText().toString().equals("0000")||
                qtdAtualG.getText().toString().equals("00000")){
            qtdAtualG.setText("0");

        }

        if(qtdAtualGG.getText().toString().equals("00")||
                qtdAtualGG.getText().toString().equals("000")||
                qtdAtualGG.getText().toString().equals("0000")||
                qtdAtualGG.getText().toString().equals("00000")){
            qtdAtualGG.setText("0");

        }

        if(qtdAtualU.getText().toString().equals("00")||
                qtdAtualU.getText().toString().equals("000")||
                qtdAtualU.getText().toString().equals("0000")||
                qtdAtualU.getText().toString().equals("00000")){
            qtdAtualU.setText("0");

        }
    }

    public void atualizarEdicao(){
        String precoAtualizado1;
        String precoAtualizado2;
        float precoAux;
        float precoOK;

        int i, iP, iM, iG, iGG, iU;
        if(verificarValorBranco()) {
            iU = Integer.parseInt(qtdAtualU.getText().toString());
            bloquearRemove();

            precoAtualizado1 = precoProdutoDetalhe.getText().toString().replace("R$", "");
            precoAtualizado2 = precoAtualizado1.replace(",", ".");
            precoAux = Float.parseFloat(precoAtualizado2);

            iM= Integer.parseInt(qtdAtualM.getText().toString());
            iG= Integer.parseInt(qtdAtualG.getText().toString());
            iGG= Integer.parseInt(qtdAtualGG.getText().toString());
            iP= Integer.parseInt(qtdAtualP.getText().toString());

            i = iP + iM + iG + iGG + iU;

            precoOK = ((float) i) * precoAux;

            DecimalFormat df = new DecimalFormat("0.00");

            valorTotalItem.setText(String.valueOf(df.format(precoOK)));
        }
        else{

            Toast.makeText(ProdutoDetalhesActivity.this,
                    "Quantidade informada é nula\nOU\n" +
                            "Favor digitar quantidade correta.",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
