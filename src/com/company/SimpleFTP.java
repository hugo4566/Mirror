package com.company;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Hugo on 13/11/2014.
 */
public class SimpleFTP {

    /**
     * Create an instance of FTP.SimpleFTP.
     */
    public SimpleFTP() {

    }

    /**
     * Connects to an FTP server and logs in with the supplied username and
     * password.
     */
    public synchronized String connect(String host, String login, String senha) throws IOException {
        socket = new Socket(host, 21);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream()));

        response();
        user(login);
        if(pass(senha).startsWith("530")) return "Login/Senha Errado";

        return "Logado!";
    }

    /*************************
     **** Common commands ****
     *************************/

    /**
     * Send this command to begin the login process. username should be a valid username on the system, or "anonymous" to initiate an anonymous login.
     * @param user login
     * @return response
     * @throws IOException
     */
    private synchronized String user(String user) throws IOException {
        sendLine("USER " + user);
        return response();
    }

    /**
     * After sending the USER command, send this command to complete the login process. (Note, however, that an ACCT command may have to be used on some systems.)
     * @param pass senha
     * @return response
     */
    private synchronized String pass(String pass) throws IOException {
        sendLine("PASS " + pass);
        return response();
    }

    private synchronized String abor() {
        return null;
    }

    /**
     * Makes the given directory be the current directory on the remote host.
     * @param dir diretorio
     * @return response
     */
    public synchronized String cwd(String dir) throws IOException {
        sendLine("CWD " + dir);
        return response();
    }

    /**
     * Deletes the given file on the remote host.
     * @param filename
     * @return
     * @throws IOException
     */
    public synchronized String dele(String filename) throws IOException {
        sendLine("DELE " + filename);
        return response();
    }

    public synchronized String list(String path) throws IOException {
        sendLine("PASV");
        ConexaoDados conexaoDados = new ConexaoDados(response()).invoke();
        Socket dataSocket = new Socket(conexaoDados.getIp(), conexaoDados.getPort());

        sendLine("LIST "+path);
        response();

        StringBuilder output = new StringBuilder();
        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            String recebido = new String(buffer, 0, bytesRead);
            output.append(recebido);
        }

        input.close();
        response();

        return output.toString();
    }

    public synchronized String listRecursive() throws IOException {
        sendLine("PASV");
        ConexaoDados conexaoDados = new ConexaoDados(response()).invoke();
        Socket dataSocket = new Socket(conexaoDados.getIp(), conexaoDados.getPort());

        sendLine("LIST -R");
        response();

        StringBuilder output = new StringBuilder();
        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            String recebido = new String(buffer, 0, bytesRead);
            output.append(recebido);
        }

        input.close();
        response();

        return output.toString();
    }

    /**
     * Returns the last-modified time of the given file on the remote host in the format
     * "YYYYMMDDhhmmss":
     * YYYY is the four-digit year,
     * MM is the month from 01 to 12,
     * DD is the day of the month from 01 to 31,
     * hh is the hour from 00 to 23,
     * mm is the minute from 00 to 59, and ss is the second from 00 to 59.
     * @return response
     */
    public synchronized String mdtm(String fileName) throws IOException {
        sendLine("MDTM "+fileName);
        return response();
    }

    public synchronized String mkd(String dirName) throws IOException {
        sendLine("MKD "+dirName);
        return response();
    }

    public synchronized String nlst() throws IOException {
        sendLine("PASV");
        ConexaoDados conexaoDados = new ConexaoDados(response()).invoke();
        Socket dataSocket = new Socket(conexaoDados.getIp(), conexaoDados.getPort());

        sendLine("NLST");
        response();
        StringBuilder output = new StringBuilder();
        BufferedInputStream input = new BufferedInputStream(dataSocket.getInputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.append(new String(buffer, 0, bytesRead));
        }
        input.close();

        System.out.println(output.toString());
        return response();
    }

    /**
     * Tells the server to enter "passive mode". In passive mode, the server will wait for the client to establish a connection with it rather than attempting to connect
     * to a client-specified port. The server will respond with the address of the port it is listening on, with a message like:
     * 227 Entering Passive Mode (a1,a2,a3,a4,p1,p2)
     * where a1.a2.a3.a4 is the IP address and p1*256+p2 is the port number.
     * @return response
     */
    private synchronized String pasv() throws IOException {
        sendLine("PASV");
        return response();
    }

    private synchronized String port() {
        return null;
    }

    /**
     * Returns the name of the current directory on the remote host.
     * @return response
     */
    public synchronized String pwd() throws IOException {
        sendLine("PWD");
        return response();
    }

    /**
     * Terminates the command connection.
     */
    public synchronized void quit() throws IOException {
        try {
            sendLine("QUIT");
        } finally {
            socket = null;
        }
    }


    public synchronized String retr(String filename) throws IOException {

        sendLine("PASV");
        ConexaoDados conexaoDados = new ConexaoDados(response()).invoke();
        Socket dataSocket = new Socket(conexaoDados.getIp(), conexaoDados.getPort());

        sendLine("RETR " + filename);
        response();

        BufferedInputStream dataIn = new BufferedInputStream(dataSocket.getInputStream());
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(new File(filename)));
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = dataIn.read(buffer)) != -1)
        {
            output.write(buffer, 0 ,bytesRead);
        }

        output.flush();
        output.close();
        dataIn.close();

        return response();
    }

    /**
     * Deletes the named directory on the remote host.
     */
    public synchronized String rmd(String dirName) throws IOException {
        sendLine("RMD " + dirName);
        return response();
    }

    private synchronized String rnfr() {
        return null;
    }

    private synchronized String rnto() {
        return null;
    }

    private synchronized String site() {
        return null;
    }

    private synchronized String size() {
        return null;
    }

    /**
     * Begins transmission of a file to the remote site. Must be preceded by either a PORT command or a PASV command so the server knows where to accept data from.
     * @param filename nome do arquivo que deseja guardar
     * @return response
     */
    private synchronized String stor(String filename) throws IOException {
        sendLine("STOR " + filename);
        return response();
    }

    /**
     * Sends a file to be stored on the FTP server. Returns true if the file
     * transfer was successful. The file is sent in passive mode to avoid NAT or
     * firewall problems at the client end.
     */
    public synchronized String stor(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("SimpleFTP cannot upload a directory.");
        }
        String filename = file.getName();

        return stor(new FileInputStream(file), filename);
    }

    /**
     * Sends a file to be stored on the FTP server. Returns true if the file
     * transfer was successful. The file is sent in passive mode to avoid NAT or
     * firewall problems at the client end.
     */
    private synchronized String stor(InputStream inputStream, String filename)
            throws IOException {

        sendLine("PASV");
        ConexaoDados conexaoDados = new ConexaoDados(response()).invoke();
        Socket dataSocket = new Socket(conexaoDados.getIp(), conexaoDados.getPort());

        stor(filename);

        BufferedInputStream input = new BufferedInputStream(inputStream);
        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        output.close();
        input.close();

        return response();
    }

    /**
     * Enter binary mode for sending binary files.
     */
    public synchronized String bin() throws IOException {
        sendLine("TYPE I");
        return response();
    }

    /**
     * Enter ASCII mode for sending text files. This is usually the default mode.
     * Make sure you use binary mode if you are sending images or other binary
     * data, as ASCII mode is likely to corrupt them.
     */
    public synchronized String ascii() throws IOException {
        sendLine("TYPE A");
        return response();
    }

    /**
     * Sends a raw command to the FTP server.
     */
    private void sendLine(String line) throws IOException {
        if (socket == null) {
            throw new IOException("SimpleFTP is not connected.");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
            if (DEBUG) {
                System.out.println("> " + line);
            }
        } catch (IOException e) {
            socket = null;
            throw e;
        }
    }

    private String response() throws IOException {
        String line = reader.readLine();
        if (DEBUG) {
            System.out.println("< " + line);
        }
        return line;
    }

    private Socket socket = null;

    private BufferedReader reader = null;

    private BufferedWriter writer = null;

    private static boolean DEBUG = false;

    protected class ConexaoDados {
        private String response;
        private String ip;
        private int port;

        public ConexaoDados(String response) {
            this.response = response;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public ConexaoDados invoke() throws IOException {
            ip = null;
            port = -1;
            int opening = response.indexOf('(');
            int closing = response.indexOf(')', opening + 1);
            if (closing > 0) {
                String dataLink = response.substring(opening + 1, closing);
                StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
                try {
                    ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                            + tokenizer.nextToken() + "." + tokenizer.nextToken();
                    port = Integer.parseInt(tokenizer.nextToken()) * 256
                            + Integer.parseInt(tokenizer.nextToken());
                } catch (Exception e) {
                    throw new IOException("SimpleFTP received bad data link information: "
                            + response);
                }
            }
            return this;
        }
    }
}

