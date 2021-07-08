package com.example.depositomodaintima.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.model.Cliente;


import java.util.List;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.MyViewHolder> {

    private List<Cliente> clientes;
    private Context context;

    public AdapterClientes(List<Cliente> clientes, Context context) {
        this.clientes = clientes;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_listar_clientes, parent,false);

        return new MyViewHolder(item);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterClientes.MyViewHolder holder, int position) {
        Cliente cliente = clientes.get(position);
        holder.nome.setText("CLIENTE: "+cliente.getNome());
        holder.telefone.setText("CONTATO: "+cliente.getTelefone());
        holder.cidade.setText("CIDADE: "+cliente.getCidade()+" || ESTADO: "+cliente.getEstado());


    }

    @Override
    public int getItemCount() {
        return clientes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nome;
        TextView cidade;
        TextView telefone;

        public MyViewHolder(View itemview){
            super(itemview);

            nome = itemview.findViewById(R.id.nomePesquisarCliente);
            cidade = itemview.findViewById(R.id.idCidadePesquisarCliente);
            telefone = itemview.findViewById(R.id.idtelefonePesquisarCliente);
        }
    }
}

