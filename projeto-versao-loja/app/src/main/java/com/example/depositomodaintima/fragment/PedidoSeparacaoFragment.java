package com.example.depositomodaintima.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.activity.EditarProdutoActivity;
import com.example.depositomodaintima.activity.ListarVendasActivity;
import com.example.depositomodaintima.activity.Main2Activity;
import com.example.depositomodaintima.activity.ProdutoDetalhesActivity;
import com.example.depositomodaintima.activity.VendaDetalheActivity;
import com.example.depositomodaintima.adapter.AdapterListaVendas;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Produto;
import com.example.depositomodaintima.model.Venda;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidoSeparacaoFragment extends Fragment {
    private RecyclerView recyclerViewListaVendasPedidoSeparacao;
    private List<Venda> vendas = new ArrayList<>();
    private AdapterListaVendas adapterListaVendasPedidoSeparacao;
    private ProgressBar progressBarPedidoSeparacao;
    private DatabaseReference vendaPedidoSeparacaoRef;
    private DatabaseReference vendaPedidoSeparacaoVrfRef;
    private ValueEventListener valueEventListenerListarVendasPedidoSeparacao;


    private AlertDialog.Builder dialog;
    private DatabaseReference cancelarRef;
    private DatabaseReference cancelarPassoUmRef;
    private DatabaseReference cancelarPassoDoisRef;
    private DatabaseReference cancelarPassoTresRef;

    public PedidoSeparacaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedido_separacao, container, false);

        recyclerViewListaVendasPedidoSeparacao = view.findViewById(R.id.idRecyclerPedidoSeparacao);
        progressBarPedidoSeparacao = view.findViewById(R.id.idprogressBarPedidoSeparacao);

        vendaPedidoSeparacaoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child("PedidoOKAguardandoSeparacao");

        adapterListaVendasPedidoSeparacao = new AdapterListaVendas(vendas,getActivity());

        //Configurar Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaVendasPedidoSeparacao.setLayoutManager(layoutManager);
        recyclerViewListaVendasPedidoSeparacao.setHasFixedSize(true);
        recyclerViewListaVendasPedidoSeparacao.setAdapter(adapterListaVendasPedidoSeparacao);

        recyclerViewListaVendasPedidoSeparacao.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewListaVendasPedidoSeparacao,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Venda venda = vendas.get(position);
                        Intent intent = new Intent(getActivity(), VendaDetalheActivity.class);
                        intent.putExtra("vendaSelecionada", venda);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(final View view, final int position) {
                        final Venda venda = vendas.get(position);
                        cancelarRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("usuarios")
                                .child(ConfiguracaoFirebase.getIdUsuario());
                        cancelarRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String teste;

                                teste = dataSnapshot.getValue(String.class);

                                if(teste.equals("NAO")){

                                }
                                else{
                                    dialog =new AlertDialog.Builder(getActivity());
                                    dialog.setTitle("CANCELAR VENDA");
                                    dialog.setMessage("Tem certeza que deseja CANCELAR a venda?");

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
                                                    cancelarVenda(venda);

                                                }
                                            });


                                    dialog.create();
                                    dialog.show();


                                }




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarProdutos();
    }

    @Override
    public void onStop() {
        super.onStop();
        vendaPedidoSeparacaoRef.removeEventListener(valueEventListenerListarVendasPedidoSeparacao);
    }

    private void recuperarProdutos(){

        valueEventListenerListarVendasPedidoSeparacao = vendaPedidoSeparacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendas.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Venda vendaRecup;
                    vendaRecup = ds.getValue(Venda.class);
                    if(!String.valueOf(vendaRecup.getVendedorNome()).equals("null") ){
                        vendas.add(vendaRecup);
                    }

                }

                Collections.reverse(vendas);
                adapterListaVendasPedidoSeparacao.notifyDataSetChanged();
                progressBarPedidoSeparacao.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void cancelarVenda(final Venda venda){

        cancelarPassoUmRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendas");

        cancelarPassoUmRef.child(venda.getIdVenda()).removeValue();

        cancelarPassoUmRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child("PedidoOKAguardandoSeparacao");

        cancelarPassoUmRef.child(venda.getIdVenda()).removeValue();
////
        final HashMap<String, HashMap<String,String>> aux1;
        aux1 = venda.getProdutoEquantidade();

        final List<String> aux2;
        aux2 = venda.getProdutos();

        if(aux2!=null){
            for(int i = 0;i<aux2.size();i++){

                cancelarPassoDoisRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("produtos")
                        .child(aux2.get(i));
                final int finalI = i;
                cancelarPassoDoisRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Produto produtoC = dataSnapshot.getValue(Produto.class);
                        HashMap<String,String> qtdAux;
                        qtdAux = produtoC.getTamanhosEquantidades();
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

                        Log.i("OOOT1: ",String.valueOf(qtdAuxFim));
                        Log.i("OOOT2: ",String.valueOf(produtoC.getTamanhosEquantidades()));

                        produtoC.setTamanhosEquantidades(qtdAuxFim);
                        Log.i("OOOT3: ",String.valueOf(produtoC.getTamanhosEquantidades()));

                        produtoC.salvar();

                        atualizarQtdProdVendidosCancelar(venda);

                        Intent intent = new Intent(getActivity(), ListarVendasActivity.class);
                        startActivity(intent);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        /////


     //   restaurarEstoqueCancel(venda);

    }

    public void restaurarEstoqueCancel(Venda venda){
        final HashMap<String, HashMap<String,String>> aux1;
        aux1 = venda.getProdutoEquantidade();

        final List<String> aux2;
        aux2 = venda.getProdutos();

        if(aux2!=null){
            for(int i = 0;i<aux2.size();i++){

                cancelarPassoDoisRef = ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("produtos")
                        .child(aux2.get(i));
                final int finalI = i;
                cancelarPassoDoisRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Produto produtoC = dataSnapshot.getValue(Produto.class);
                        HashMap<String,String> qtdAux;
                        qtdAux = produtoC.getTamanhosEquantidades();
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

                        Log.i("OOOT1: ",String.valueOf(qtdAuxFim));
                        Log.i("OOOT2: ",String.valueOf(produtoC.getTamanhosEquantidades()));

                        produtoC.setTamanhosEquantidades(qtdAuxFim);
                        Log.i("OOOT3: ",String.valueOf(produtoC.getTamanhosEquantidades()));

                        produtoC.salvar();





                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void atualizarQtdProdVendidosCancelar(Venda venda){
        HashMap<String, HashMap<String,String>> produtosAux = venda.getProdutoEquantidade();
        for (final Map.Entry<String, HashMap<String,String>> entrada : produtosAux.entrySet()) {
            //System.out.println(entrada.getKey());
            //System.out.println(entrada.getValue());

            cancelarPassoTresRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("produtos")
                    .child(entrada.getKey());
            cancelarPassoTresRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                    auxIAT3 = auxIAT1-auxIAT2;

                    produtoAtualizarEstoque.setQtdVendidos(String.valueOf(auxIAT3));
                    produtoAtualizarEstoque.salvar();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

}
