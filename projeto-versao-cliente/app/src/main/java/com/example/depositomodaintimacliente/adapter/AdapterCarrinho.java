package com.example.depositomodaintimacliente.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.model.ProdutoVendido;
import com.example.depositomodaintimacliente.model.Venda;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.MyViewHolder> {
    private List<ProdutoVendido> produtos;
    private Context context;
    private DatabaseReference produtoRef;
    private DatabaseReference vendaRef;
    private Venda vendaSel;

    public AdapterCarrinho(List<ProdutoVendido> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_carrinho, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

         ProdutoVendido produto = produtos.get(position);
         Log.i("TESTE003: ", String.valueOf(produto.getId()));

        holder.nomeProduto.setText(produto.getTitulo());
        holder.precoProduto.setText("VALOR por UNIDADE: "+produto.getPreco());
        HashMap<String,String> hashMap = produto.getTamanhosEquantidades();
        String aux = "";


        if(!hashMap.get("P").equals("0")){
            aux = aux + "P: "+hashMap.get("P")+" ";
            holder.quantidadeEtamanhoProduto.setText(aux);
        }

        if(!hashMap.get("M").equals("0")){
            aux = aux + "M: "+hashMap.get("M")+" ";
            holder.quantidadeEtamanhoProduto.setText(aux);
        }

        if(!hashMap.get("G").equals("0")){
            aux = aux + "G: "+hashMap.get("G")+" ";
            holder.quantidadeEtamanhoProduto.setText(aux);
        }

        if(!hashMap.get("GG").equals("0")){
            aux = aux + "GG: "+hashMap.get("GG")+" ";
            holder.quantidadeEtamanhoProduto.setText(aux);
        }

        if(!hashMap.get("U").equals("0")){
            aux = aux + "U: "+hashMap.get("U")+" ";
            holder.quantidadeEtamanhoProduto.setText(aux);
        }

        String urlFoto = produto.getFoto();
        Picasso.get().load(urlFoto).into(holder.fotoProduto);

        String auxS1, auxS2;
        Float auxF1, auxF2;
        auxS1 = produto.getPreco().replace("R$","");
        auxS2 = auxS1.replace(",",".");
        auxF1 = Float.parseFloat(auxS2);

        int auxEstoqueP,
            auxEstoqueM,
            auxEstoqueG,
            auxEstoqueGG,
            auxEstoqueU,
            auxEstoque;

        auxEstoqueP = Integer.parseInt(hashMap.get("P"));
        auxEstoqueM = Integer.parseInt(hashMap.get("M"));
        auxEstoqueG = Integer.parseInt(hashMap.get("G"));
        auxEstoqueGG = Integer.parseInt(hashMap.get("GG"));
        auxEstoqueU = Integer.parseInt(hashMap.get("U"));
        auxEstoque = auxEstoqueP+auxEstoqueM+auxEstoqueG +auxEstoqueGG + auxEstoqueU;


        auxF2 = (float)(auxEstoque);

        holder.totalItens.setText("Quantidade Total de itens: "+String.valueOf(auxEstoque));

        DecimalFormat df = new DecimalFormat("0.00");

        holder.totalProduto.setText("Valor Total deste item: R$ "+ df.format(auxF1*auxF2));

    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nomeProduto;
        TextView quantidadeEtamanhoProduto;
        TextView precoProduto;
        TextView totalItens;
        TextView totalProduto;
        ImageView fotoProduto;

        public MyViewHolder(View itemView){
            super(itemView);

            nomeProduto = itemView.findViewById(R.id.idTituloAC);
            quantidadeEtamanhoProduto = itemView.findViewById(R.id.idQuantidadeEtamanhoAC);
            precoProduto = itemView.findViewById(R.id.idPrecoAC);
            totalItens = itemView.findViewById(R.id.idTotalItensAC);
            totalProduto = itemView.findViewById(R.id.idTotalAC);
            fotoProduto = itemView.findViewById(R.id.idImagemAC);

        }

    }


}
