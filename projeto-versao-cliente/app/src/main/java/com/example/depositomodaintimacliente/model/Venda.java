package com.example.depositomodaintimacliente.model;

import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Venda implements Serializable {
    private String idVenda;
    private String data;
    private String cliente;
    private String clienteId;
    private String vendedorId;
    private String vendedorNome;
    private String totalValor;
    private String totalItens;
    private List<String> produtos;
    private HashMap<String,HashMap<String,String>> produtoEquantidade;
    private String statusVenda;
    private String enderecoEntrega;
    private String contatoCliente;
    private String cidadeCliente;
    private String estadoCliente;

    public Venda() {
        this.enderecoEntrega = "";
        DatabaseReference vendaRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("vendasClientesGeral");
        setIdVenda(vendaRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference vendaRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("vendasClientesGeral");
        vendaRef.child(getIdVenda()).
                setValue(this);

    }


    public String getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(String idVenda) {
        this.idVenda = idVenda;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(String vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getVendedorNome() {
        return vendedorNome;
    }

    public void setVendedorNome(String vendedorNome) {
        this.vendedorNome = vendedorNome;
    }

    public String getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(String totalValor) {
        this.totalValor = totalValor;
    }

    public String getTotalItens() {
        return totalItens;
    }

    public void setTotalItens(String totalItens) {
        this.totalItens = totalItens;
    }

    public List<String> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<String> produtos) {
        this.produtos = produtos;
    }

    public HashMap<String, HashMap<String, String>> getProdutoEquantidade() {
        return produtoEquantidade;
    }

    public void setProdutoEquantidade(HashMap<String, HashMap<String, String>> produtoEquantidade) {
        this.produtoEquantidade = produtoEquantidade;
    }

    public String getStatusVenda() {
        return statusVenda;
    }

    public void setStatusVenda(String statusVenda) {
        this.statusVenda = statusVenda;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getContatoCliente() {
        return contatoCliente;
    }

    public void setContatoCliente(String contatoCliente) {
        this.contatoCliente = contatoCliente;
    }

    public String getCidadeCliente() {
        return cidadeCliente;
    }

    public void setCidadeCliente(String cidadeCliente) {
        this.cidadeCliente = cidadeCliente;
    }

    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }
}
