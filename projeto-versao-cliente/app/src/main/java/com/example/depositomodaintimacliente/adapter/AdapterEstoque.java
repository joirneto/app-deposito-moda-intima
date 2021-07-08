package com.example.depositomodaintimacliente.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.example.depositomodaintimacliente.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class AdapterEstoque extends RecyclerView.Adapter<AdapterEstoque.MyViewHolder> {
    private List<Produto> produtos;
    private Context context;
    private DatabaseReference estoqueRef;
    private String qtd = null;


    public AdapterEstoque(List<Produto> produtos, Context context) {
       // Collections.reverse(produtos);
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Produto produto = produtos.get(position);
        holder.titulo.setText(produto.getTitulo());
        holder.valor.setText(produto.getPreco());

        estoqueRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("produtos")
                .child(produto.getIdProduto());
        estoqueRef.child("tamanhosEquantidades").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,String> qtdAux;
                qtdAux = (HashMap<String, String>) dataSnapshot.getValue();
                        int auxEstoqueP,
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
                totalAux = auxEstoqueP + auxEstoqueM + auxEstoqueG + auxEstoqueGG + auxEstoqueU;

            //    holder.quantidade.setText(String.valueOf(totalAux)+ " itens em Estoque");
                if (String.valueOf(totalAux).equals("0")){
                    holder.fundoProduto.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

                }
                else{
                    holder.fundoProduto.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        //REcupera a primeira Foto
        List<String> urlFoto = produto.getListaFotos();
        String urlCapa = urlFoto.get(0);


      /*  ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urlCapa, holder.foto, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.progressBarAdapterFoto.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.progressBarAdapterFoto.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.progressBarAdapterFoto.setVisibility(View.GONE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.progressBarAdapterFoto.setVisibility(View.GONE);

            }
        });
*/
        Picasso.get().load(urlCapa).resize(400,400).centerInside().into(holder.foto);



    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        //TextView descricao;
        TextView valor;
        TextView quantidade;
        ImageView foto;

        LinearLayout fundoProduto;





        public MyViewHolder(View itemview){
            super(itemview);

            titulo = itemview.findViewById(R.id.idTituloEstoque);
            //descricao = itemview.findViewById(R.id.idDescricaoEStoque);
            valor = itemview.findViewById(R.id.idValorEstoque);
          //  quantidade = itemview.findViewById(R.id.idQuantidadeEstoque);
            foto = itemview.findViewById(R.id.idImagemEstoque);
            fundoProduto = itemView.findViewById(R.id.idfundoProduto);


        }

    }



}
