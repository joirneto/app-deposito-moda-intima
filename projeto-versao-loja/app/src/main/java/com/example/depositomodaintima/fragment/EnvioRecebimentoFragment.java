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
public class EnvioRecebimentoFragment extends Fragment {
    private RecyclerView recyclerViewListaVendasEnvioRecebimento;
    private List<Venda> vendas = new ArrayList<>();
    private AdapterListaVendas adapterListaVendasEnvioRecebimento;
    private ProgressBar progressBarEnvioRecebimento;
    private DatabaseReference vendaEnvioRecebimentoRef;
    private DatabaseReference vendaEnvioRecebimentoVrfRef;
    private ValueEventListener valueEventListenerListarVendasEnvioRecebimento;

    public EnvioRecebimentoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_envio_recebimento, container, false);

        recyclerViewListaVendasEnvioRecebimento = view.findViewById(R.id.idRecyclerEnvioRecebimento);
        progressBarEnvioRecebimento = view.findViewById(R.id.idprogressBarEnvioRecebimento);

        vendaEnvioRecebimentoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasPorEtapas")
                .child("EnvioOKAguardandoRecebimentoCliente");

        adapterListaVendasEnvioRecebimento = new AdapterListaVendas(vendas,getActivity());

        //Configurar Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaVendasEnvioRecebimento.setLayoutManager(layoutManager);
        recyclerViewListaVendasEnvioRecebimento.setHasFixedSize(true);
        recyclerViewListaVendasEnvioRecebimento.setAdapter(adapterListaVendasEnvioRecebimento);

        recyclerViewListaVendasEnvioRecebimento.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewListaVendasEnvioRecebimento,
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
        vendaEnvioRecebimentoRef.removeEventListener(valueEventListenerListarVendasEnvioRecebimento);
    }

    private void recuperarProdutos(){

        valueEventListenerListarVendasEnvioRecebimento = vendaEnvioRecebimentoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendas.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    vendas.add(ds.getValue(Venda.class));
                }

                Collections.reverse(vendas);
                adapterListaVendasEnvioRecebimento.notifyDataSetChanged();
                progressBarEnvioRecebimento.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
