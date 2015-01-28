package com.company;

import java.util.Calendar;

/**
 * Created by hugo on 1/27/15.
 */
public class FileFTP {

    private String tipo;
    private String nome;
    private String tamanho;
    private String nomeModificador;
    private String modificado;
    private Calendar dataModificacao;

    public FileFTP(String response) {
        String[] partes = response.split("\\s+");
        this.tipo = (partes[1]);
        this.nome = (partes[8]);
        this.tamanho = (partes[4]);
        this.nomeModificador = (partes[2]);
        this.modificado = (partes[5]+" "+partes[6]+" "+partes[7]);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public String getNomeModificador() {
        return nomeModificador;
    }

    public void setNomeModificador(String nomeModificador) {
        this.nomeModificador = nomeModificador;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    public Calendar getDataModificacao() {
        return dataModificacao;
    }

    public void setDataModificacao(Calendar dataModificacao) {
        this.dataModificacao = dataModificacao;
    }

    @Override
    public String toString() {
        return "FileFTP{" +
                "tipo=" + tipo +
                ", nome='" + nome + '\'' +
                ", tamanho=" + tamanho +
                ", modificado='" + modificado + '\'' +
                ", data de modificacao='" + dataModificacao + '\'' +
                '}';
    }

}