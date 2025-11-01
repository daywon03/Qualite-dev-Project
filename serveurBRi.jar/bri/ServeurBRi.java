package bri;


import java.io.*;
import java.net.*;


public class ServeurBRi implements Runnable {
	private ServerSocket listen_socket;
	private boolean isProgrammerMode;  // true = port prog, false = port amateur


	// Cree un serveur TCP - objet de la classe ServerSocket
	public ServeurBRi(int port, boolean isProgrammerMode) {
		try {
			this.listen_socket = new ServerSocket(port);
			this.isProgrammerMode = isProgrammerMode;
			String mode = isProgrammerMode ? "PROGRAMMEURS" : "AMATEURS";
			System.out.println("ServeurBRi [" + mode + "] ecoute sur port " + port);

		} catch (IOException e) {
			throw new RuntimeException("Impossible de creer le serveur sur port " + port ,e);
		}
	}

	// Le serveur ecoute et accepte les connections.
	// pour chaque connection, il cree un ServiceInversion, 
	// qui va la traiter.
	public void run() {
		try {
			while (true) {
				Socket clientSocket = listen_socket.accept();
				String clientInfo = clientSocket.getInetAddress().getHostAddress() +
						":" + clientSocket.getPort();

				System.out.println("Connexion depuis " + clientInfo);

				new ServiceBRi(clientSocket, isProgrammerMode).start();
			}
		} catch (Exception e){
			System.err.println("PB sur le port " + e.getMessage());
		}finally {
			try {
				if (listen_socket != null) {
					listen_socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	// lancement du serveur
	public void lancer() {
		(new Thread(this)).start();		
	}
}
