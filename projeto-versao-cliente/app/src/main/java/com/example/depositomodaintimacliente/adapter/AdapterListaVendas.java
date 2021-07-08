package com.example.depositomodaintimacliente.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.model.Venda;

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
        corrigirNomeEtapaVenda(venda);


        holder.status.setText("STATUS: "+venda.getStatusVenda());
        holder.cliente.setText("CLIENTE: "+ venda.getCliente());
        holder.data.setText("Data da VENDA: "+venda.getData());
        holder.itens.setText("TOTAL de ITENS: "+venda.getTotalItens());
        holder.valor.setText("VALOR TOTAL DA VENDA: R$ "+venda.getTotalValor());

    }

    @Override
    public int getItemCount() {
        return vendas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cliente,status,valor,itens,data;


        public MyViewHolder(View itemView) {
            super(itemView);

            cliente = itemView.findViewById(R.id.idClienteLV);
            status = itemView.findViewById(R.id.idStatusLV);
            valor = itemView.findViewById(R.id.idTotalLV);
            itens = itemView.findViewById(R.id.idItensLV);
            data = itemView.findViewById(R.id.idDataLV);


        }

    }

    public void corrigirNomeEtapaVenda(Venda venda){
        switch (venda.getStatusVenda()){
            case "PedidoOKAguardandoSeparacao":
                venda.setStatusVenda("Pedido OK. Aguardando Separacao.");
                break;
            case "SeparacaoOKAguardandoPagamento":
                venda.setStatusVenda("Separacao OK. Aguardando Pagamento.");
                break;
            case "PagamentoOKAguardandoEnvio":
                venda.setStatusVenda("Pagamento OK. Aguardando envio.");
                break;
            case "EnvioOKAguardandoRecebimentoCliente":
                venda.setStatusVenda("Envio OK. Aguardando Recebimento do Cliente");
                break;
            case "VendasFinalizadas":
                venda.setStatusVenda("Venda Finalizada!");
                break;
        }
    }
}