package com.example.depositomodaintima.model;

import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {
    private String id;
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private String vendendo;
    private String ativado;
    private String admin;
    private String cliente;

    public Usuario() {
        this.ativado = "NAO";
        this.admin = "NAO";
    }

    public void salvar(){
        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("usuarios");
        usuarioRef.child(getId()).
                setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getVendendo() {
        return vendendo;
    }

    public void setVendendo(String vendendo) {
        this.vendendo = vendendo;
    }

    public String getAtivado() {
        return ativado;
    }

    public void setAtivado(String ativado) {
        this.ativado = ativado;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
}
