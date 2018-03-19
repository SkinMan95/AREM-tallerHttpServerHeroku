package edu.escuelaing.arem.taller.httpserverheroku;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public class ResponderThread extends Thread {

    private Socket clientSocket;

    public ResponderThread(Socket clientSocket) {
        if (clientSocket == null) {
            throw new NullPointerException();
        }
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            OutputStream out = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;

            while ((inputLine = in.readLine()) != null) {
                HttpServer.processPetition(inputLine, out);
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex);
        }
    }

}
