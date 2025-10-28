package bri;

import java.io.Serializable;

public class User implements Serializable {
    private String  login;
    private String password;
    private String ftpUrl;

    public User (){};

    public User (String login, String password, String ftpUrl){
        this.ftpUrl=ftpUrl;
        this.login=login;
        this.password=password;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFtpUrl() {
        return ftpUrl;
    }

    public void setFtpUrl(String ftpUrl) {
        this.ftpUrl = ftpUrl;
    }

    @Override
    public String toString(){
        return "User [login = " + login + ",ftp = " + ftpUrl + "]";
    }
}
