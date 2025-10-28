package appli;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Scanner;

import bri.ServeurBRi;
import bri.ServiceRegistry;

public class BRiLaunch {
	private final static int PORT_SERVICE = 3000;
	
	public static void main(String[] args) throws MalformedURLException {
		@SuppressWarnings("resource")
		Scanner clavier = new Scanner(System.in);
		
		// URLClassLoader sur ftp
		URLClassLoader urlcl = new URLClassLoader(new URL[] {
				new URL("ftp://localhost:2121/home/")
		});
		
		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activite BRi");
		System.out.println("Pour ajouter une activite, celle-ci doit etre presente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'integrer");
		System.out.println("Les clients se connectent au serveur 3000 pour lancer une activite");
		
		new Thread(new ServeurBRi(PORT_SERVICE)).start();
		
		while (true){
				try {
					String classeName = clavier.next();
					Class<?> serviceClass = urlcl.loadClass(classeName);
					ServiceRegistry.addService(serviceClass);
					System.out.println("Service " + classeName + " ajouté avec succès.");
					System.out.println("Liste des services actuels :");
					System.out.println(ServiceRegistry.toStringue());

				} catch (Exception e) {
					System.out.println(e);
				}
			}		
	}
}
