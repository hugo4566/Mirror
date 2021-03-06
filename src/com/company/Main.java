package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Hugo on 13/01/2015.
 */
/*
OK a-  MirrorFTP lê as linhas do arquivo "entradas.txt" obtendo os parâmetros de execução.
OK b- O Mirror recupera informações tanto do conteúdo do diretório local quanto do conteúdo do diretório remoto
 (diretórios existentes, arquivos existentes, e informações de quando foi a última modificação de cada arquivo).
c- A partir das informações recuperadas, ele inicia o processo de espelhamento:
 (i) se existe um arquivo em um diretório que não existe no outro, o Mirror o copia para este diretório.
 (ii) se a data de modificação um arquivo em um diretório é diferente da data de modificação da sua cópia
 no outro diretório, o Mirror sobrescreve o arquivo mais antigo de um diretório com o mais novo de um outro diretório.
 (iii) se um determinado arquivo ou pasta é excluída de um determinado diretório (local ou remoto),
 o Mirror exclui o seu equivalente no outro diretório.
d- O processo de espelhamento deve ser disparado pelo Mirror FTP automaticamente (sem a intervenção ou o conhecimento
do usuário) entre determinados intervalos de tempo (por exemplo, entre intervalos de tempo de 60 segundos)
como obtido através do arquivo "entradas.txt".
*/
public class Main {

    static InfoEntrada infoEntrada;
    static SimpleFTP simpleFTP = new SimpleFTP();
    static Collection<File> listFilesClient = new ArrayList<File>();
    static Collection<FileFTP> listFilesServer = new ArrayList<FileFTP>();
    static Collection<FileFTP> OldlistFilesServer = new ArrayList<FileFTP>();
    private static boolean DEBUG = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        cicloA();
        while (true) {
            cicloB();
            cicloC();
            Thread.sleep(infoEntrada.getIntervalo()*1000);
        }
    }

    private static void cicloA() throws IOException {
        File f = new File("entradas.txt");
        InputStream is = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        infoEntrada = new InfoEntrada(br).invoke();
        System.out.println(infoEntrada.toString());
        System.out.println(simpleFTP.connect(infoEntrada.getHost(), infoEntrada.getUsuario(), infoEntrada.getSenha(), infoEntrada.getPorta()));
    }

    private static void cicloB() throws IOException {
        System.out.println("-----------------------------------------");
        listFilesClient.addAll(listFilesFromFolder(new File(infoEntrada.getDirLocal())));
        System.out.println("-----------------------------------------");
        listFilesServer.addAll(listFilesFromServer(infoEntrada.getDirRemoto()));
    }

    private static void cicloC() throws IOException {

        for (Iterator<FileFTP> iterator = listFilesServer.iterator(); iterator.hasNext();) {
            FileFTP fileftp = iterator.next();
            boolean found = false;
            for (Iterator<File> iterator2 = listFilesClient.iterator(); iterator2.hasNext();) {
                File file = iterator2.next();
                switch (fileftp.compareTo(file)) {
                    case 0:
                        found = true;
                        break;
                    case 1:
                        puxarDoLocal(file);
                        found = true;
                        break;
                    case -1:
                        puxarDoServer(fileftp);
                        found = true;
                        break;
                    default:
                        break;
                }
            }
            if (!found){
            	boolean oldfind = false;
            	for (FileFTP oldftp : OldlistFilesServer){
            		if (fileftp.compareTo(oldftp) != 999){
            			deletarDoServer(fileftp);
            			oldfind = true;
            			break;
            		}
            	} 
            	if (!oldfind){
            		puxarDoServer(fileftp);
            	}
            }
        }
        for (Iterator<File> iterator = listFilesClient.iterator(); iterator.hasNext();) {
            File file = iterator.next();
            boolean found = false;
            for (Iterator<FileFTP> iterator2 = listFilesServer.iterator(); iterator2.hasNext();) {
                FileFTP fileftp = iterator2.next();
                switch (fileftp.compareTo(file)) {
                    case 0:
                        found = true;
                        break;
                    case 1:
                        puxarDoServer(fileftp);
                        found = true;
                        break;
                    case -1:
                        puxarDoLocal(file);
                        found = true;
                        break;
                    default:
                        break;
                }
            }
            if (!found){
            	boolean oldfind = false;
            	for (FileFTP oldftp : OldlistFilesServer){
            		if (oldftp.compareTo(file) != 999){
            			deletarDoLocal(file);
            			oldfind = true;
            			break;
            		}
            	} 
            	if (!oldfind){
            		puxarDoLocal(file);
            	}
            	
            }
        }

        OldlistFilesServer.clear();
        OldlistFilesServer.addAll(listFilesServer);
    }

    private static void deletarDoServer(FileFTP fileFTP) throws IOException {
        String path = fileFTP.getPath();
        if(!path.endsWith("/"))
            path = path.concat("/");
        if(fileFTP.getTipo().equals("1")) {
            simpleFTP.dele(path + fileFTP.getNome());
        }else{
            simpleFTP.rmd(path + fileFTP.getNome());
        }
    }

    private static void deletarDoLocal(File file) throws IOException {
        file.delete();
    }

    private static void puxarDoServer(FileFTP fileFTP) throws IOException {
        String path = fileFTP.getPath();
        if(!path.endsWith("/")){
            path = path.concat("/");
        }

        if(fileFTP.getTipo().equals("1")) {
            simpleFTP.retr(infoEntrada.getDirLocal()+path+fileFTP.getNome(),path+fileFTP.getNome());
        }else{
            File file = new File(infoEntrada.getDirLocal()+path+fileFTP.getNome());
            if(!file.exists())
                file.mkdirs();
        }
    }

    private static void puxarDoLocal(File file) throws IOException {
        if (file.isDirectory()) {
            String path = getRelativeLocalPath(file);
            if(!path.endsWith("/")){
                path = path.concat("/");
            }
            simpleFTP.mkd(path);
        }else if(file.isFile()){
            String path = getRelativeLocalPath(file);
            path = path.substring(0,(path.length()-file.getName().length()));
            simpleFTP.cwd(path);
            simpleFTP.stor(file);
            simpleFTP.mfmt(Utils.lastModifiedParaCalendar(file.lastModified()), path + file.getName());
        }
    }

    public static Collection<File> listFilesFromFolder(File folder) {
        Collection<File> listFilesFolder = new ArrayList<File>();
        for (File fileEntry : folder.listFiles()) {
            listFilesFolder.add(fileEntry);
            if(DEBUG) System.out.println(fileEntry.toString());
            if (fileEntry.isDirectory()) {
                listFilesFolder.addAll(listFilesFromFolder(fileEntry));
            }
        }
        return listFilesFolder;
    }

    public static Collection<FileFTP> listFilesFromServer(String path) throws IOException {
        String responseList = simpleFTP.list(path);
        Collection<FileFTP> list = destrinchaLista(responseList);
        Collection<FileFTP> listFilesServer = new ArrayList<FileFTP>();
        for (FileFTP fileEntry : list) {
            if (!fileEntry.getTipo().equals("1")) {
                if(path.endsWith("/"))
                    listFilesServer.addAll(listFilesFromServer(path + fileEntry.getNome()));
                else
                    listFilesServer.addAll(listFilesFromServer(path + "/" + fileEntry.getNome()));
            }else{
                fileEntry.setDataModificacao(Utils.mdtmParaCalendar(simpleFTP.mdtm(path + "/" + fileEntry.getNome())));
            }
            fileEntry.setPath(path);
            listFilesServer.add(fileEntry);
            if(DEBUG) System.out.println(fileEntry.toString());
        }
        return listFilesServer;
    }

    private static Collection<FileFTP> destrinchaLista(String responseList) throws IOException {
        Collection<FileFTP> listaFileFTPs = new ArrayList<FileFTP>();
        if(responseList.length() >0){
            String[] listaArquivos = responseList.split("\\r\\n");
            for (int i = 0; i < listaArquivos.length; i++) {
                FileFTP fileFTP = new FileFTP(listaArquivos[i]);
                listaFileFTPs.add(fileFTP);
            }
        }
        return listaFileFTPs;
    }

    private static String getRelativeLocalPath(File fileEntry) {
        return fileEntry.getAbsolutePath().substring(infoEntrada.getDirLocal().length());
    }
}