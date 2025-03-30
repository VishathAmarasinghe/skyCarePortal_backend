package com.skycareSolutions.skyCarePortal.utils;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionRegistry {
    private static final ConcurrentHashMap<String, List<String>> userSessions = new ConcurrentHashMap<>();

    public static void addUserSession(String username,String sessoon) {
        if (userSessions.get(username)==null){
//            userSessions.remove(username);
            List<String> sessionList = new ArrayList<>();
            sessionList.add(sessoon);
            userSessions.put(username, sessionList);
        }else{
            List<String> previosuSessions = userSessions.get(username);
            previosuSessions.add(sessoon);
            userSessions.put(username, previosuSessions);
        }
    }

    public static boolean getSession(String userName,String session) {
        if (userSessions.get(userName)!=null){
            List<String> sessionValues = userSessions.get(userName);
            if (sessionValues.contains(session)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public static void removeUserSession(String userName) {
        userSessions.remove(userName);
    }
    // Get all active sessions
    public static ConcurrentHashMap<String, List<String>> getAllSessions() {
        return userSessions;
    }


//    public static String getSessionIDByUser(String username) {
//        for (Map.Entry<String, String> entry : userSessions.entrySet()) {
//            if (entry.getValue().equals(username)) {
//                return entry.getKey();  // Return the session ID associated with the username
//            }
//        }
//        return null;  // Return null if no session is found for the given username
//    }

    public static void printAllSessions() {
        for (Map.Entry<String, List<String>> entry : userSessions.entrySet()) {
            System.out.println("username "+entry.getKey());
            for (String val:entry.getValue()){
                System.out.print(val+" ");
            }

        }
    }

}
