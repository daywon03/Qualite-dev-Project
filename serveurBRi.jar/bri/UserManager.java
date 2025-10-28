package bri;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {

    private static Map<String,User> users = new ConcurrentHashMap<>();

    static {
        // Ajouter quelques utilisateurs de test
        users.put("dupont", new User("dupont", "pass123", "ftp://localhost:2121/"));
        users.put("martin", new User("martin", "pass456", "ftp://localhost:2122/"));
    }

    //Auth
    public static boolean auth(String login, String password) {
        User user = users.get(login);
        return user != null && user.getPassword().equals(password);

    }
    //Récuperer user
    public static User getUser(String login){
        return users.get(login);
    }
    //Ajouter une user
    public static boolean addUser (User user){
        if (users.containsKey(user.getLogin())) {
            return false; // Utilisateur existe déjà
        }
        users.put(user.getLogin(), user);
        return true;
    }

    //MAJ Url FTP
    public static boolean UpdateUrl(String login, String NewUrlftp){
        //init user
        User user = users.get(login);
        //verif si logs
        if(user == null) return false;
        user.setFtpUrl(NewUrlftp);
        return true;
    }


}
