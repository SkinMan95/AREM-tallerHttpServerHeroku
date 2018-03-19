package edu.escuelaing.arem.taller.httpserverheroku;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author Alejandro Anzola email: alejandro.anzola@mail.escuelaing.edu.co
 */
public class HttpServer {

    public static final int DEFAULT_PORT = 36000;
    public static boolean finished = false;
    public static int port = DEFAULT_PORT;

    public static void main(String[] args) throws Exception {
        try {
            port = new Integer(System.getenv("PORT"));
        } catch(Exception ex) {
            System.err.println("ERROR: " + ex + " using default address (" + DEFAULT_PORT + ")");
            port = DEFAULT_PORT;
        }

        generateResponders();

        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(port);

        System.out.println("Ready to receive...");

        Socket clientSocket = null;
        while (!finished) {
            clientSocket = serverSocket.accept();

            (new ResponderThread(clientSocket)).start();
        }

        serverSocket.close();
    }

    public static Map<String, Responder> respondersMap;

    public static void generateResponders() {
        respondersMap = new HashMap<>();
        respondersMap.put("/", (OutputStream out) -> {
            PrintWriter o = new PrintWriter(out, true);
            o.println("HTTP/1.1 200 OK\n"
                    + "Content-Type: text/html; charset=utf-8\n"
                    + "Content-Encoding: raw\n"
                    + "\n"
                    + "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "  <head>\n"
                    + "    <meta charset=\"utf-8\" />\n"
                    + "    <title>BPMN Documentation</title>\n"
                    + "    <link rel=\"stylesheet\" href=\"css/styles.css\">\n"
                    + " </link>\n"
                    + "  </head>\n"
                    + "  <body>\n"
                    + "    <img src=\"images/invie2x.png\">"
                    + "    <div class=\"contenedor\">\n"
                    + "      <h1 class=\"title\">BPMN Documentation</h1>\n"
                    + "      <h1>Titulo 1</h1>\n"
                    + "      <h2>Titulo 2</h2>\n"
                    + "      <h3>Titulo 3</h3>\n"
                    + "      <h4>Titulo 4</h4>\n"
                    + "      <h5>Titulo 5</h5>\n"
                    + "      <h6>Titulo 6</h6>\n"
                    + "\n"
                    + "      <ol>\n"
                    + "	<h1><li>Item 1</li></h1>\n"
                    + "	<h1><li>Item 2</li></h1>\n"
                    + "	<h1><li>Item 3</li></h1>\n"
                    + "	<h1><li>Item 4</li></h1>\n"
                    + "	<h1><li>Item 5</li></h1>\n"
                    + "	  <ol>\n"
                    + "	    <h2><li>SumItem 1</li></h2>\n"
                    + "	    <h2><li>SubItem 2</li></h2>\n"
                    + "	    <h2><li>SubItem 3</li></h2>\n"
                    + "	    <h2><li>SubItem 4</li></h2>\n"
                    + "	    <h2><li>SubItem 5<br />\n"
                    + "		<p>Blabla de definición</p>\n"
                    + "		<ol>\n"
                    + "		  <h3><li>Subsubitem 1</li></h3>\n"
                    + "		  <h3><li>Subsubitem 2</li></h3>\n"
                    + "		  <h3><li>Subsubitem 3</li></h3>\n"
                    + "		</ol>\n"
                    + "	    </li></h2>\n"
                    + "	  </ol>\n"
                    + "	</li>\n"
                    + "      </ol>\n"
                    + "    </div>\n"
                    + "  </body>\n"
                    + "</html>");
            o.close();
        });

        respondersMap.put("/images/invie2x.png", (OutputStream out) -> {
            File f = new File("invie2x.png");

            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(f);
                byte[] data = new byte[(int) f.length()];
                inputStream.read(data);
                inputStream.close();

                DataOutputStream binaryOut = new DataOutputStream(out);
                binaryOut.writeBytes("HTTP/1.0 200 OK\r\n");
                binaryOut.writeBytes("Content-Type: image/png\r\n");
                binaryOut.writeBytes("Content-Length: " + data.length);
                binaryOut.writeBytes("\r\n\r\n");
                binaryOut.write(data);

                binaryOut.close();
            } catch (IOException ex) {
                System.err.println("Error: " + ex);
            }

        });

        respondersMap.put("/css/styles.css", (OutputStream out) -> {
            PrintWriter o = new PrintWriter(out, true);
            o.println("HTTP/1.1 200 OK\n"
                    + "Content-Type: text/css; charset=utf-8\n"
                    + "Content-Encoding: raw\n"
                    + "\n"
                    + "body {\n"
                    + "    background-color: #990000;\n"
                    + "    font-family: arial, helvetica, sans-serif;\n"
                    + "    font-size: 15px;\n"
                    + "}\n"
                    + "\n"
                    + "h1, h2, h3, h4, h5, h6 {\n"
                    + "    color: #990000;\n"
                    + "    font-size: 18px;\n"
                    + "}\n"
                    + "\n"
                    + ".title {\n"
                    + "    font-size: 25px\n"
                    + "}\n"
                    + "\n"
                    + ".contenedor {\n"
                    + "    width: 1500px;\n"
                    + "    margin: auto;\n"
                    + "    position: relative;\n"
                    + "    height: 40%;\n"
                    + "    background-color: white;\n"
                    + "    padding: 30px;\n"
                    + "}\n"
                    + "\n"
                    + "h2 {\n"
                    + "    font-size: 17px;\n"
                    + "}\n"
                    + "\n"
                    + "h3 {\n"
                    + "    font-size: 16px;\n"
                    + "}\n"
                    + "\n"
                    + "h4 {\n"
                    + "    font-size: 15px;\n"
                    + "}\n"
                    + "\n"
                    + "h4, h5, h6 {\n"
                    + "    font-size: 14px;\n"
                    + "}\n"
                    + "\n"
                    + "p {\n"
                    + "    font-family: arial, helvetica, sans-serif;\n"
                    + "    font-size: 14px;\n"
                    + "    color: black;\n"
                    + "}\n"
                    + "\n"
                    + "ol {\n"
                    + "    list-style: none;\n"
                    + "    padding-left: 0;\n"
                    + "}\n");
            o.close();
        });
    }

    public static void processPetition(String petition, OutputStream out) {
        StringTokenizer strtok = new StringTokenizer(petition, " ");
        if (strtok.hasMoreElements() && ((String) strtok.nextElement()).equals("GET")) {
            System.out.println("GET petition recognized: " + petition);

            String resourceName = "";
            if (strtok.hasMoreElements()) {
                resourceName = ((String) strtok.nextElement());
                if (respondersMap.containsKey(resourceName)) {
                    System.out.println("GET petition is valid");
                    respondersMap.get(resourceName).respond(out); // responds petition
                }
            }
        }
    }

}
