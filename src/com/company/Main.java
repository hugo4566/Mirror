package com.company;

import java.io.*;

/**
 * Created by Hugo on 13/01/2015.
 */
public class Main {

    static InfoEntrada infoEntrada;
    static SimpleFTP simpleFTP = new SimpleFTP();

    public static void main(String[] args) throws IOException {
        cicloA();
        cicloB();
    }

    private static void cicloA() throws IOException {
        File f = new File("entradas.txt");
        InputStream is = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        infoEntrada = new InfoEntrada(br).invoke();
        System.out.println(infoEntrada.toString());
    }

    private static void cicloB() throws IOException {
        simpleFTP.connect(infoEntrada.getHost(),infoEntrada.getUsuario(),infoEntrada.getSenha());
        System.out.println(simpleFTP.nlst());
        listFilesForFolder(new File(infoEntrada.getDirLocal()));
    }

    public static void listFilesForFolder(File folder) {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println(fileEntry.toString());
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.toString());
            }
        }
    }
}
