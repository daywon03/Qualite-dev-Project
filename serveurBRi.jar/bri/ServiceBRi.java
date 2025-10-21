package bri;


import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


class ServiceBRi implements Runnable {
	
	private Socket client;
	
	ServiceBRi(Socket socket) {
		client = socket;
	}

	public void run() {
		try {BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println(ServiceRegistry.toStringue()+" Tapez le numéro de service désiré :");
			int choix = Integer.parseInt(in.readLine());

			// instancier le service numéro "choix" en lui passant la socket "client"
			Class<?> classChoix = ServiceRegistry.getServiceClass(choix); //charges dynamique
			System.out.println("Classe choisie : " + classChoix.getName());

			//Nouvelle instance dynamique
			Constructor<?> classChoixConstructor = classChoix.getConstructor(Socket.class);
			Service serviceInstance = (Service) classChoixConstructor.newInstance(client);
			// invoquer run() pour cette instance ou la lancer dans un thread à part
			new Thread(serviceInstance).start();
			}
		catch (IOException e) {
			//Fin du service
		} catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {client.close();} catch (IOException e2) {}
	}


	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
