package network.mychat;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {

    private List<Session> sessions = new ArrayList<>();

    public void add(Session session) {
        sessions.add(session);
    }

    public void remove(Session session) {
        sessions.remove(session);
    }

    public void broadcast(String message) {
        for (Session session : sessions) {
            session.send(message);
        }
    }

    public String getAllUsers() {
        StringBuilder sb = new StringBuilder();
        sb.append("== 채팅 접속 사용자 목록 ==\n");

        for (Session session : sessions) {
            User user = session.getUser();

            if (user != null) {
                sb.append("- ")
                  .append(user.getName())
                  .append("\n");
            }
        }

        return sb.toString();
    }

    public void closeAll() {
        for (Session session : sessions) {
            session.close();
        }
        sessions.clear();
    }
}
