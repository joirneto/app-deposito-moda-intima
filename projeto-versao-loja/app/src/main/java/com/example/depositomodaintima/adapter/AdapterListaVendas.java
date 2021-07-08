package com.example.depositomodaintima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.model.Venda;

import java.util.List;

public class AdapterListaVendas extends RecyclerView.Adapter<AdapterListaVendas.MyViewHolder> {
    private List<Venda> vendas;
    private Context context;

    public AdapterListaVendas(List<Venda> vendas, Context context) {
        this.vendas = vendas;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listar_vendas, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Venda venda = vendas.get(position);


        holder.vendedor.setText("VENDEDOR: "+venda.getVendedorNome());
        holder.cliente.setText("CLIENTE: "+ venda.getCliente());
        holder.data.setText("Data da VENDA: "+venda.getData());
        holder.itens.setText("TOTAL de ITENS: "+venda.getTotalItens());
        holder.valor.setText("VALOR TOTAL DA VENDA: R$ "+venda.getTotalValor());

        if (venda.getVendedorNome().equals("VENDA EXTERNA")){
            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.background_fundo_venda_externa));

        }
        else{
            holder.linearLayout.setBackground(context.getResources().getDrawable(R.drawable.background_text_final));
        }

    }

    @Override
    public int getItemCount() {
        return vendas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cliente,vendedor,valor,itens,data;
        LinearLayout linearLayout;


        public MyViewHolder(View itemView) {
            super(itemView);

            cliente = itemView.findViewById(R.id.idClienteLV);
            vendedor = itemView.findViewById(R.id.idVendedorLV);
            valor = itemView.findViewById(R.id.idTotalLV);
            itens = itemView.findViewById(R.id.idItensLV);
            data = itemView.findViewById(R.id.idDataLV);
            linearLayout = itemView.findViewById(R.id.idFundoVendas);


        }

    }
}