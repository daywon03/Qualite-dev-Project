package clientprog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ApplicationProg {
    private final static int PORT_SERVICE = 3000;
    private final static String HOST = "localhost";

    public static void main(String[] args) {
        Socket s = null;
        try {
                s = new Socket(HOST, PORT_SERVICE);
                BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter sout = new PrintWriter(s.getOutputStream(), true);
                BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Connecte au serveur " + s.getInetAddress() + ":" + s.getPort());

                String line;
                boolean continuer = true;

                while (continuer && (line = sin.readLine()) != null) {
                    // Afficher le message du serveur (remplacer ## par \n)
                    String messageAffiche = line.replaceAll("##", "\n");
                    System.out.println(messageAffiche);

                    // Si le serveur demande une entrée
                    if (messageAffiche.contains(":") ||
                            messageAffiche.contains("?") ||
                            messageAffiche.toLowerCase().contains("choix")) {

                        System.out.print("> ");
                        String reponse = clavier.readLine();
                        sout.println(reponse);

                        // Si l'utilisateur choisit 0, terminer
                        if (reponse.equals("0")) {
                            continuer = false;
                        }
                    }
                    // Détecter la fin de connexion
                    if (messageAffiche.contains("Déconnexion") ||
                            messageAffiche.contains("échouée")) {
                        continuer = false;
                    }
                }
                System.out.println("\n Déconnecté du serveur.");


        } catch (UnknownHostException e) {
                System.err.println(" Hôte inconnu: " + HOST);
            } catch (IOException e) {
                System.err.println("Erreur de connexion: " + e.getMessage());
            } finally {
                // Toujours fermer la socket
                try {
                    if (s != null) {
                        s.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}


