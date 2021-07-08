package com.example.depositomodaintimacliente.model;

import java.util.HashMap;

public class ProdutoVendido {
    private String id;
    private String titulo;
    private String preco;
    private String foto;
    private HashMap<String,String> tamanhosEquantidades;

    public ProdutoVendido() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public HashMap<String, String> getTamanhosEquantidades() {
        return tamanhosEquantidades;
    }

    public void setTamanhosEquantidades(HashMap<String, String> tamanhosEquantidades) {
        this.tamanhosEquantidades = tamanhosEquantidades;
    }
}
