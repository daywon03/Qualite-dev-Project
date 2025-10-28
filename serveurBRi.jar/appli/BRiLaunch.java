package appli;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import bri.ServeurBRi;
import bri.ServiceRegistry;
import bri.User;
import bri.UserManager;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMA = 3001;

	private static Map<String,URLClassLoader> classLoaders = new HashMap<>();
	public static void main(String[] args) throws MalformedURLException {
		Scanner clavier = new Scanner(System.in);
		
		// URLClassLoader sur ftp
		URLClassLoader urlcl = new URLClassLoader(new URL[] {
				new URL("ftp://localhost:2121/home/")
		});

		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activite BRi");
		System.out.println("PORT programmeurs" + PORT_PROG);
		System.out.println("PORT amateurs" + PORT_PROG);

		//Lancement de serveurs pour programmeurs
		new Thread(new ServeurBRi(PORT_PROG)).start();
		//Lancement de serveurs pour amateurs
		new Thread(new ServeurBRi(PORT_PROG)).start();

		System.out.println("\n Serveurs demarrer, Tapez 'help' pour aide.");

		while (true){
			String cmd = clavier.next();
			if(cmd.equals("help")){
				System.out.println("Commandes: 'list', 'stop', 'help'");
			} else if (cmd.equals("list")) {
				System.out.println(ServiceRegistry.toStringue());

			}
		}
	}

	public static URLClassLoader getClassLoader(String login) throws MalformedURLException
	{
		if (!classLoaders.containsKey(login)){
			User user = UserManager.getUser(login);
			if (user == null) return null;
			URLClassLoader classLoad = new URLClassLoader(new URL [] {
					new URL(user.getFtpUrl())
			});
			classLoaders.put(login,classLoad);
		}
        return classLoaders.get(login);
    }
}
