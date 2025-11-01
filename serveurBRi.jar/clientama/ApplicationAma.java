package clientama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ApplicationAma {
    private final static int PORT_SERVICE = 3001;
    private final static String HOST = "localhost";

    public static void main(String[] args) {
        Socket s = null;
        try {
            s = new Socket(HOST, PORT_SERVICE);

            BufferedReader sin = new BufferedReader (new InputStreamReader(s.getInputStream ( )));
            PrintWriter sout = new PrintWriter (s.getOutputStream ( ), true);
            BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connecte au serveur " + s.getInetAddress() + ":"+ s.getPort());

            String line;
            // menu et choix du service
            line = sin.readLine();
            System.out.println(line.replaceAll("##", "\n"));
            // saisie/envoie du choix
            System.out.print("Votre choix: ");
            sout.println(clavier.readLine());

            // reception/affichage de la question
            System.out.println(sin.readLine());
            // saisie clavier/envoie au service de la reponse
            sout.println(clavier.readLine());
            // reception/affichage de la reponse
            System.out.println(sin.readLine());


        }
        catch (IOException e) { System.err.println("Fin de la connexion"); }
        // Refermer dans tous les cas la socket
        try { if (s != null) s.close(); }
        catch (IOException e2) { ; }
    }
}
