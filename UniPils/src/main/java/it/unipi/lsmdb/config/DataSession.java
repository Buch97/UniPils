package it.unipi.lsmdb.config;

import it.unipi.lsmdb.bean.User;

public class DataSession {
    private static User userLogged = null;
    private static String IdUserLogged=null;
    private static String UsernameUserLogged=null;
    private static int idBeerToShow;
    private static String userView=null;

    /*public static void setUserLogged(User userLogged){
        DataSession.userLogged = userLogged;
    }*/

    public static void setUserLogged(String username){ DataSession.UsernameUserLogged = username;}

    public static String getUserLogged(){
        return UsernameUserLogged;
    }

    public static void setIdBeerToShow(int idBeerToShow) {
        DataSession.idBeerToShow = idBeerToShow;
    }

    public static int getIdBeerToShow() {
        return idBeerToShow;
    }

    /*public static User getUserLogged(){
        return userLogged;
    }*/

    public static void IdUserLogged(String id){
        DataSession.IdUserLogged=id;
    }

    public static void logoutSession() {
        UsernameUserLogged = null;
    }

    public static void setUserView(String username){DataSession.userView=username;}

    public static String getUserView(){return userView;}
}
