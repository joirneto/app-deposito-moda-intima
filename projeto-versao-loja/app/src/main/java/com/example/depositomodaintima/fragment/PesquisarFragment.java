package com.example.depositomodaintima.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.depositomodaintima.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisarFragment extends Fragment {
    private SearchView pesquisa;
    private RecyclerView recyclerPesquisa;

    public PesquisarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisar, container, false);

        pesquisa = view.findViewById(R.id.idPesquisar);
        recyclerPesquisa = view.findViewById(R.id.idRecyclerPesquisa);

        //Configurar o SearchView
        pesquisa.setQueryHint("Buscar Produtos");
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }
}
