package bri;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import appli.BRiLaunch;

//Thread qui gère la connexion d'un client (programmeur ou amateur)

class ServiceBRi implements Runnable {

	private Socket client;
	private boolean isProgrammerMode;
	private String authenticatedUser = null;  // Login si authentifié

	ServiceBRi(Socket socket, boolean isProgrammerMode) {
		this.client = socket;
		this.isProgrammerMode = isProgrammerMode;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);

			//  MODE PROGRAMMEUR
			if (isProgrammerMode) {
				handleProgrammer(in, out);
			}
			// MODE AMATEUR
			else {
				handleAmateur(in, out);
			}

		} catch (IOException e) {
			System.err.println(" Erreur connexion client: " + e.getMessage());
		} finally {
			try {
				if (client != null && !client.isClosed()) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//Gestion programmeur
	private void handleProgrammer(BufferedReader in, PrintWriter out) throws IOException {
		// 1. Authentification
		out.println(" BRi Platform - Programmeur ===##Login:");
		String login = in.readLine();
		if (login == null) return;

		out.println("Password:");
		String password = in.readLine();
		if (password == null) return;

		// Vérifier les credentials
		if (!UserManager.auth(login, password)) {
			out.println("Authentification échouée##");
			return;
		}

		authenticatedUser = login;
		out.println("Bienvenue " + login + " !##");

		// 2. Menu de gestion des services
		boolean running = true;
		while (running) {
			out.println("##=== Menu Programmeur ===##" +
					"1 - Ajouter un nouveau service##" +
					"2 - Lister mes services##" +
					"3 - Démarrer un service##" +
					"4 - Arrêter un service##" +
					"5 - Désinstaller un service##" +
					"6 - Changer l'URL FTP##" +
					"0 - Déconnexion##" +
					"Votre choix:");

			String choixStr = in.readLine();
			if (choixStr == null) break;

			int choix = Integer.parseInt(choixStr);

			switch (choix) {
				case 1:
					ajouterService(in, out);
					break;
				case 2:
					out.println(ServiceRegistry.toStringue());
					break;
				case 3:
					demarrerService(in, out);
					break;
				case 4:
					arreterService(in, out);
					break;
				case 5:
					desinstallerService(in, out);
					break;
				case 6:
					changerFTP(in, out);
					break;
				case 0:
					out.println("Déconnexion...##");
					running = false;
					break;
				default:
					out.println("Choix invalide##");
			}
		}
	}

	/**
	 * Ajouter un service depuis le FTP du programmeur
	 */
	private void ajouterService(BufferedReader in, PrintWriter out) throws IOException {
		out.println("Nom complet de la classe (ex: " + authenticatedUser + ".MonService):");
		String className = in.readLine();

		try {
			// Obtenir le ClassLoader du programmeur
			URLClassLoader classLoader = BRiLaunch.getClassLoader(authenticatedUser);

			// Charger la classe
			Class<?> serviceClass = classLoader.loadClass(className);

			// Ajouter au registry (vérifie la norme BRi)
			ServiceRegistry.addService(serviceClass, authenticatedUser, classLoader);

			out.println("Service ajouté avec succès !##");

		} catch (Exception e) {
			out.println(" Erreur: " + e.getMessage() + "##");
		}
	}

	/**
	 * Démarrer un service
	 */
	private void demarrerService(BufferedReader in, PrintWriter out) throws IOException {
		out.println(ServiceRegistry.toStringue());
		out.println("Numéro du service à démarrer:");
		int num = Integer.parseInt(in.readLine());

		if (ServiceRegistry.startService(num, authenticatedUser)) {
			out.println("Service démarré##");
		} else {
			out.println(" Impossible de démarrer ce service##");
		}
	}

	/**
	 * Arrêter un service
	 */
	private void arreterService(BufferedReader in, PrintWriter out) throws IOException {
		out.println(ServiceRegistry.toStringue());
		out.println("Numéro du service à arrêter:");
		int num = Integer.parseInt(in.readLine());

		if (ServiceRegistry.stopService(num, authenticatedUser)) {
			out.println("Service arrêté##");
		} else {
			out.println(" Impossible d'arrêter ce service##");
		}
	}

	/**
	 * Désinstaller un service
	 */
	private void desinstallerService(BufferedReader in, PrintWriter out) throws IOException {
		out.println(ServiceRegistry.toStringue());
		out.println("Numéro du service à désinstaller:");
		int num = Integer.parseInt(in.readLine());

		if (ServiceRegistry.removeService(num, authenticatedUser)) {
			out.println("Service désinstallé##");
		} else {
			out.println("Impossible de désinstaller ce service##");
		}
	}

	/**
	 * Changer l'URL FTP
	 */
	private void changerFTP(BufferedReader in, PrintWriter out) throws IOException {
		out.println("Nouvelle URL FTP:");
		String newUrl = in.readLine();

		if (UserManager.UpdateUrl(authenticatedUser, newUrl)) {
			// Recharger le ClassLoader
			try {
				BRiLaunch.reloadClassLoader(authenticatedUser);
				out.println("URL FTP mise à jour##");
			} catch (Exception e) {
				out.println(" Erreur lors du rechargement: " + e.getMessage() + "##");
			}
		} else {
			out.println(" Impossible de mettre à jour##");
		}
	}

	/**
	 * Gestion d'un amateur (comme dans le TP4, mais seulement services démarrés)
	 */
	private void handleAmateur(BufferedReader in, PrintWriter out) throws IOException {
		// Envoyer le menu des services DÉMARRÉS seulement
		out.println(ServiceRegistry.toStringueStartedOnly());

		// Lire le choix
		String choixStr = in.readLine();
		if (choixStr == null) return;

		int choix = Integer.parseInt(choixStr);

		// Récupérer le service parmi ceux démarrés
		ServiceInfo info = ServiceRegistry.getStartedServiceInfo(choix);
		if (info == null) {
			out.println("Service introuvable##");
			return;
		}

		try {
			// Instancier le service avec la socket
			Constructor<?> constructor = info.getServiceClass().getConstructor(Socket.class);
			Service service = (Service) constructor.newInstance(client);

			// Lancer le service dans un thread
			new Thread(service).start();

		} catch (Exception e) {
			out.println("Erreur lors du lancement: " + e.getMessage() + "##");
			e.printStackTrace();
		}
	}

	/**
	 * Lance ce ServiceBRi dans un thread
	 */
	public void start() {
		new Thread(this).start();
	}
}
