package bri;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import static java.lang.Character.getName;

public class ServiceRegistry implements Service {
	// cette classe est un registre de services
	// partage en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	private final Socket client;

	static {
		servicesClasses = new Vector<>();
	}
	private static List<Class<?>> servicesClasses;

    public ServiceRegistry(Socket socket) {
        client = socket;
    }

    // ajoute une classe de service apres controle de la norme BRi
	public static void addService(Class<?> classe) throws Exception{
		// verifier la conformite par introspection
		// si non conforme --> exception avec message clair
		//verification interface service
		if (!Service.class.isAssignableFrom(classe)) {
			throw new Exception("La classe n'implemente pas Service");
		}

		if (Modifier.isAbstract(classe.getModifiers()) || !Modifier.isPublic(classe.getModifiers())){
			throw new Exception("La classe doit être publique et non abstraite");
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

		// si conforme, ajout au vector
		servicesClasses.add(classe);

	}
	
// renvoie la classe de service (numService -1)	
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.get(numService - 1);
	}
	
// liste les activit?s pr?sentes
	public static String toStringue() {
		StringBuilder result = new StringBuilder("Activites presentes :");
		// todo
		for (Class<?> classe : servicesClasses){
			result.append(classe.getName()).append("\n");
		}

		return result.toString();
	}


	@Override
	public void run() {

	}
}
