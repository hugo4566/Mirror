package com.company;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Hugo on 13/01/2015.
 */
public class InfoEntrada {

    private BufferedReader br;
    private String host;
    private int porta;
    private int intervalo;
    private String usuario;
    private String senha;
    private String dirLocal;
    private String dirRemoto;

    public InfoEntrada(BufferedReader br) {
        this.br = br;
    }

    public String getHost() {
        return host;
    }

    public int getPorta() {
        return porta;
    }

    public int getIntervalo() {
        return intervalo;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

    public String getDirLocal() {
        return dirLocal;
    }

    public String getDirRemoto() {
        return dirRemoto;
    }

    public InfoEntrada invoke() throws IOException {
        host = br.readLine();
        porta = new Integer(br.readLine());
        intervalo = new Integer(br.readLine());
        usuario = br.readLine();
        senha = br.readLine();
        dirLocal = br.readLine();
        dirRemoto = br.readLine();
        return this;
    }

    @Override
    public String toString() {
        return "InfoEntrada{" +
                "host='" + host + '\'' +
                ", porta=" + porta +
                ", intervalo=" + intervalo +
                ", usuario='" + usuario + '\'' +
                ", senha='" + senha + '\'' +
                ", dirLocal='" + dirLocal + '\'' +
                ", dirRemoto='" + dirRemoto + '\'' +
                '}';
    }
}
