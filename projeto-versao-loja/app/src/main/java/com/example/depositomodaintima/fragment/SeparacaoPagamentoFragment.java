package com.example.depositomodaintima.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.activity.VendaDetalheActivity;
import com.example.depositomodaintima.adapter.AdapterListaVendas;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Venda;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeparacaoPagamentoFragment extends Fragment {
    private RecyclerView recyclerViewListaVendasSeparacaoPagamento;
    private List<Venda> vendas = new ArrayList<>();
    private AdapterListaVendas adapterListaVendasSeparacaoPagamento;
    private ProgressBar progressBarSeparacaoPagamento;
    private DatabaseReference vendaSeparacaoPagamentoRef;
    private DatabaseReference vendaSeparacaoPagamentoVrfRef;
    private ValueEventListener valueEventListenerListarVendasSeparacaoPagamento;

    public SeparacaoPagamentoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_separacao_pagamento, container, false);

        recyclerViewListaVendasSeparacaoPagamento = view.findViewById(R.id.idRecyclerSeparacaoPagamento);
        progressBarSeparacaoPagamento = view.findViewById(R.id.idprogressBarSeparacaoPagamento);

        vendaSeparacaoPagamentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child("SeparacaoOKAguardandoPagamento");

        adapterListaVendasSeparacaoPagamento = new AdapterListaVendas(vendas,getActivity());

        //Configurar Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaVendasSeparacaoPagamento.setLayoutManager(layoutManager);
        recyclerViewListaVendasSeparacaoPagamento.setHasFixedSize(true);
        recyclerViewListaVendasSeparacaoPagamento.setAdapter(adapterListaVendasSeparacaoPagamento);

        recyclerViewListaVendasSeparacaoPagamento.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewListaVendasSeparacaoPagamento,
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
        vendaSeparacaoPagamentoRef.removeEventListener(valueEventListenerListarVendasSeparacaoPagamento);
    }

    private void recuperarProdutos(){

        valueEventListenerListarVendasSeparacaoPagamento = vendaSeparacaoPagamentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendas.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    vendas.add(ds.getValue(Venda.class));
                }

                Collections.reverse(vendas);
                adapterListaVendasSeparacaoPagamento.notifyDataSetChanged();
                progressBarSeparacaoPagamento.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
