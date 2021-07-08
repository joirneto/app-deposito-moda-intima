package com.example.depositomodaintima.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.depositomodaintima.activity.EditarProdutoActivity;
import com.example.depositomodaintima.activity.Main2Activity;
import com.example.depositomodaintima.activity.ProdutoDetalhesActivity;
import com.example.depositomodaintima.adapter.AdapterEstoque;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Produto;
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
public class ShortFragment extends Fragment {

    private RecyclerView recyclerViewShort;
    private AdapterEstoque adapterEstoqueShort;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference estoqueShortRef;
    private ValueEventListener valueEventListenerShort;
    private ProgressBar progressBarShort;
    private DatabaseReference produtoRef;
    private DatabaseReference vendendoRef;
    private AlertDialog.Builder dialog;

    public ShortFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_short, container, false);

        progressBarShort = view.findViewById(R.id.idprogressBarShort);

        //Configurações iniciais
        recyclerViewShort = view.findViewById(R.id.idRecyclerShort);
        estoqueShortRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtosCategoria").child("Short");
        //Configurando o Adaper
        adapterEstoqueShort = new AdapterEstoque(produtos,getActivity());

        //Configurar Recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewShort.setLayoutManager(layoutManager);
        recyclerViewShort.setHasFixedSize(true);
        recyclerViewShort.setAdapter(adapterEstoqueShort);


        recyclerViewShort.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewShort,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produtoSelcionado = produtos.get(position);
                        Intent intent = new Intent(getActivity(), ProdutoDetalhesActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelcionado);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(final View view, final int position) {
                        final Produto produtoSelcionado = produtos.get(position);
                        vendendoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("usuarios")
                                .child(ConfiguracaoFirebase.getIdUsuario())
                                .child("vendendo");
                        vendendoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String teste;

                                teste = dataSnapshot.getValue(String.class);

                                if(teste.equals("SIM")){

                                }
                                else{
                                    dialog =new AlertDialog.Builder(getActivity());
                                    dialog.setTitle("EDITAR PRODUTO");
                                    dialog.setMessage("Tem certeza que deseja EDITAR o protudo do estoque?");

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
                                                    Produto produtoSelecionado = produtos.get(position);
                                                    Intent intent = new Intent(getActivity(), EditarProdutoActivity.class);
                                                    intent.putExtra("produtoSelecionado", produtoSelecionado);
                                                    startActivity(intent);
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
        estoqueShortRef.removeEventListener(valueEventListenerShort);
    }

    private void recuperarProdutos(){

        valueEventListenerShort = estoqueShortRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    produtos.add(ds.getValue(Produto.class));
                }

                Collections.reverse(produtos);
                adapterEstoqueShort.notifyDataSetChanged();
                progressBarShort.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
