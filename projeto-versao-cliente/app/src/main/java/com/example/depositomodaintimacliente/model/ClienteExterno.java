package com.example.depositomodaintimacliente.model;

import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class ClienteExterno implements Serializable {
    private String IdCliente;
    private String nome;
    private String email;
    private String telefone;
    private String cidade;
    private String estado;
    private String comprando;

    public ClienteExterno() {
        this.comprando = "NAO";
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("clientesExterno");
        setIdCliente(clienteRef.push().getKey());
    }

    public void salvar(){
        DatabaseReference clienteRef = ConfiguracaoFirebase.getFirebaseDatabase().
                child("clientesExternos");
        clienteRef.child(getIdCliente()).
                setValue(this);
    }

    public String getIdCliente() {
        return IdCliente;
    }

    public void setIdCliente(String idCliente) {
        IdCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComprando() {
        return comprando;
    }

    public void setComprando(String comprando) {
        this.comprando = comprando;
    }
}
