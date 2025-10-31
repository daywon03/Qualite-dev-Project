package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static java.lang.Character.getName;

public class ServiceRegistry implements Service {
	// cette classe est un registre de services
	// partage en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	private final Socket client;


	//thread-safe
	private static List<ServiceInfo> servicesInfo;

	static {
		servicesInfo = new Vector<>();
	}
/*
	private static List<Class<?>> servicesClasses;
	static {
		servicesClasses = new Vector<>();
	}
    public ServiceRegistry(Socket socket) {
        client = socket;
    }*/

    // ajoute une classe de service apres controle de la norme BRi
	public static void addService(Class<?> classe, String loginProgrammeur, URLClassLoader classLoader) throws Exception{
		// verifier la conformite par introspection
		// si non conforme --> exception avec message clair
		//verification interface service
		if (!Service.class.isAssignableFrom(classe)) {
			throw new Exception("La classe n'implemente pas Service");
		}

		if (Modifier.isAbstract(classe.getModifiers()) || !Modifier.isPublic(classe.getModifiers())){
			throw new Exception("La classe doit être publique et non abstraite");
		}

		//Verif package
		String packageName = classe.getPackage().getName();
		if(!packageName.equals(loginProgrammeur)){
			throw new Exception("La classe doit etre dans le package '" + loginProgrammeur + "', trouve: '" + packageName + "'");
		}

		//Verification : constructeur public Service(Socket)
		Constructor<?> constructor = null;
		try {
			constructor = classe.getConstructor(Socket.class);
			if(!Modifier.isPublic(constructor.getModifiers())){
				throw new Exception("le constructeur n'est pas public");
			}
		} catch (NoSuchMethodException e){
				throw new Exception("Le constructeur (Socket) n'existe pas ");
		}

		//Verification attribut final prive Socket
		Field[] fields = classe.getDeclaredFields();
		boolean foundField = false;
		for(Field f : fields) {
			if(f.getType() == Socket.class && Modifier.isPrivate(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) {
				foundField = true;
				break;
			}
		}
		if (!foundField) {
			throw new Exception("Pas d'attribut Socket privé/final");
		}

		try {
			Method method = classe.getDeclaredMethod("toStringue");
			if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() != String.class){
				throw new Exception("toStringue doit être static et retourner String");
			}
		} catch (NoSuchMethodException e ){
			throw new Exception("Methode static toStringue absente");
		}

		ServiceInfo info = new ServiceInfo(classe, loginProgrammeur, classLoader);
		servicesInfo.add(info);
		//servicesClasses.add(classe);
		System.out.println(" Service ajouté: " + classe.getName());
		System.out.println("   Propriétaire: " + loginProgrammeur);


	}
	
// renvoie la classe de service (numService -1)	
	/*public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}*/

	public static ServiceInfo getServiceInfo(int numService) {
		if(numService < 1 || numService >servicesInfo.size() ){
			return null;
		}
		return servicesInfo.get(numService - 1);
	}

	public static Class<?> getServiceClass(int numService) {
		ServiceInfo info = getServiceInfo(numService);
		return (info != null) ? info.getServiceClass() : null;
	}

	public static boolean startService( int numService, String loginProgrammeur){
		//verif numservice info
		ServiceInfo info = getServiceInfo(numService);
		if(info == null){
			return  false;
		}
		//verif owner service
		if (!info.getOwnerLogin().equals(loginProgrammeur)){
			System.err.println("Seul le propriétaire peut démarrer ce service");
			return false ;
		}
		info.start();
		return true;
	}

	public  static boolean stopService(int numService, String loginProgrammeur){
		ServiceInfo info = getServiceInfo(numService);
		if(info == null) {
			return false;
		}
		if(!info.getOwnerLogin().equals(loginProgrammeur)){
			System.err.println("Seul le proprietaire peut arrete ce service");
			return false;
		}
		info.stop();
		return true;
	}
	public static boolean removeService(int numService, String loginProgrammeur) {
		ServiceInfo info = getServiceInfo(numService);
		if (info == null) {
			return false;
		}
		if (!info.getOwnerLogin().equals(loginProgrammeur)) {
			System.err.println(" Seul le propriétaire peut désinstaller ce service");
			return false;
		}
		servicesInfo.remove(info);
		System.out.println("Service désinstallé: " + info.getServiceClass().getName());
		return true;
	}

	public static String toStringue() {
		if(servicesInfo.isEmpty()){
			return "Aucun service disponible##";
		}
		StringBuilder result = new StringBuilder(" Services disponibles ##");
		for (int i = 0; i < servicesInfo.size(); i++) {
			ServiceInfo info = servicesInfo.get(i);
			result.append((i + 1)).append(". ").append(info.toString()).append("##");
		}
		return result.toString();
	}


	public static String toStringueStartedOnly() {
		List<ServiceInfo> startedServices = new ArrayList<>();
		for (ServiceInfo info : servicesInfo) {
			if (info.isStarted()) {
				startedServices.add(info);
			}
		}

		if (startedServices.isEmpty()) {
			return "Aucun service démarré##";
		}

		StringBuilder result = new StringBuilder(" Services disponibles ##");
		for (int i = 0; i < startedServices.size(); i++) {
			ServiceInfo info = startedServices.get(i);
			result.append((i + 1)).append(". ").append(info.getDescription()).append("##");
		}
		return result.toString();
	}

	public static ServiceInfo getStartedServiceInfo(int choix) {
		int count = 0;
		for (ServiceInfo info : servicesInfo) {
			if (info.isStarted()) {
				count++;
				if (count == choix) {
					return info;
				}
			}
		}
		return null;
	}

// liste les activit?s pr?sentes
/*public static String toStringue() {
	StringBuilder result = new StringBuilder("Activites presentes :##");
	for (int i = 0; i < servicesClasses.size(); i++) {
		try {
			Method method = servicesClasses.get(i).getMethod("toStringue");
			result.append((i + 1)).append(" - ").append(method.invoke(null)).append("##");
		} catch (Exception e) {
			result.append((i + 1)).append(" - ").append(servicesClasses.get(i).getName()).append("##");
		}
	}
	return result.toString();
}*/



	@Override
	public void run() {

	}
}
