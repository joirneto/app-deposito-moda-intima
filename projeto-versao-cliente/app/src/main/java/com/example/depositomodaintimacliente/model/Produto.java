package com.example.depositomodaintimacliente.model;

import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Produto implements Serializable {
    private String idProduto;
    private String titulo;
    private String preco;
    private String categoria;
    private String descricao;
    private String quantidade;
    private String ganho;
    private String qtdVendidos;
    private List<String> listaFotos;
    private HashMap<String,String> tamanhosEquantidades;

    public Produto() {
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("produtos");
        setIdProduto(produtoRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("produtos");
        produtoRef.child(getIdProduto()).
                setValue(this);

        salvarCategoria();
        salvarHistorico();
    }

    public void salvarCategoria(){
        DatabaseReference produtoRefCat = ConfiguracaoFirebase.getFirebaseDatabase().
                child("produtosCategoria").child(getCategoria());
        produtoRefCat.child(getIdProduto()).
                setValue(this);
    }


    public void salvarHistorico(){
        DatabaseReference produtoRefCat = ConfiguracaoFirebase.getFirebaseDatabase().
                child("produtosHistorico").child(getCategoria());
        produtoRefCat.child(getIdProduto()).
                setValue(this);
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto= idProduto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo.toUpperCase();
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.toUpperCase();
    }


    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public List<String> getListaFotos() {
        return listaFotos;
    }

    public void setListaFotos(List<String> listaFotos) {
        this.listaFotos = listaFotos;
    }

    public String getGanho() {
        return ganho;
    }

    public void setGanho(String ganho) {
        this.ganho = ganho;
    }

    public String getQtdVendidos() {
        return qtdVendidos;
    }

    public void setQtdVendidos(String qtdVendidos) {
        this.qtdVendidos = qtdVendidos;
    }

    public HashMap<String, String> getTamanhosEquantidades() {
        return tamanhosEquantidades;
    }

    public void setTamanhosEquantidades(HashMap<String, String> tamanhosEquantidades) {
        this.tamanhosEquantidades = tamanhosEquantidades;
    }
}
