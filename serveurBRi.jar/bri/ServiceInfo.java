package bri;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class ServiceInfo {
    //classe de service charger dynamiquement
    private final Class<?> serviceClass;
   //etat : true = demarre : false : arrete
    private boolean isStarted;

    private final String ownerLogin;

    //etat du service
    private final URLClassLoader classLoader;

    public ServiceInfo(Class<?> serviceClass, String ownerLogin, URLClassLoader classLoader ){
        this.serviceClass = serviceClass;
        this.ownerLogin = ownerLogin;
        this.isStarted = false;
        this.classLoader = classLoader;
    }


    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public void start(){
        this.isStarted=true;
        System.out.println("Service demarre: " + serviceClass.getName());
    }

    public void stop(){
        this.isStarted = false;
        System.out.println("Service arrete : " + serviceClass.getName());
    }

    public String getDescription (){
       try {
           Method method = serviceClass.getMethod("toStringue");
           return (String) method.invoke(null); // null car methode static
       } catch (Exception e){
           return serviceClass.getName();
       }
    }

    @Override
    public String toString() {
        String state = isStarted ? "DÉMARRÉ" : "ARRÊTÉ";
        return String.format("[%s] %s (propriétaire: %s)",
                state, getDescription(), ownerLogin);
    }
}
