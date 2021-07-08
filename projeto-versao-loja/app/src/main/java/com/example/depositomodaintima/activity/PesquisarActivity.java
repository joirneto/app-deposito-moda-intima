package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.adapter.AdapterEstoque;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.RecyclerItemClickListener;
import com.example.depositomodaintima.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PesquisarActivity extends AppCompatActivity {
        private SearchView pesquisa;
        private RecyclerView recyclerPesquisa;
        private List<Produto>  listaProdutos;
        private DatabaseReference produtosRef;
        private AdapterEstoque adapterEstoquePesquisa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisar);

        pesquisa = findViewById(R.id.idPesquisar);
        recyclerPesquisa = findViewById(R.id.idRecyclerPesquisa);
        listaProdutos = new ArrayList<>();
        produtosRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");

        adapterEstoquePesquisa = new AdapterEstoque(listaProdutos,this);

        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(this));
        recyclerPesquisa.setAdapter(adapterEstoquePesquisa);

        //Configurar o SearchView
        pesquisa.setIconified(false);

        pesquisa.setFocusable(true);
        pesquisa.setQueryHint("Buscar Produtos");
        pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarProdutos(textoDigitado);
                return true;
            }
        });

        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Produto produtoSelcionado = listaProdutos.get(position);
                        Intent intent = new Intent(getApplicationContext(), ProdutoDetalhesActivity.class);
                        intent.putExtra("produtoSelecionado", produtoSelcionado);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
    }

    private void pesquisarProdutos(String textoDigitado){
        listaProdutos.clear();
        if(textoDigitado.length()>=2){
            Query query = produtosRef.orderByChild("titulo").startAt(textoDigitado).endAt(textoDigitado + "\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaProdutos.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){

                        listaProdutos.add(ds.getValue(Produto.class));

                    }

                    adapterEstoquePesquisa.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }
}
