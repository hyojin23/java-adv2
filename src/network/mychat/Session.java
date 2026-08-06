package network.mychat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static network.tcp.SocketCloseUtil.*;
import static util.MyLogger.log;

public class Session implements Runnable {

    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;
    private final SessionManager sessionManager;
    private User user;
    private boolean closed = false;

    public Session(Socket socket, SessionManager sessionManager) throws IOException {
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
        this.sessionManager = sessionManager;
        this.sessionManager.add(this);
    }

    @Override
    public void run() {
        try {
            while (!closed) {
                String[] lines = input.readUTF().split("\\|", 2);
                String command = lines[0];
                String content = lines.length > 1 ? lines[1] : null;
                log("command: " + command);
                switch (command) {
                    case "/join":
                        user = new User(content);
                        break;
                    case "/message":
                        if (!isJoined()) {
                            break;
                        }
                        sessionManager.broadcast(user.getName() + " : " + content);
                        break;
                    case "/change":
                        if (!isJoined()) {
                            break;
                        }
                        this.getUser().setName(content);
                        break;
                    case "/users":
                        send(sessionManager.getAllUsers());
                        break;
                    case "/exit":
                        sessionManager.broadcast(user.getName() + "님이 퇴장했습니다.");
                        sessionManager.remove(this);
                        close();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            log(e);
        } finally {
            sessionManager.remove(this);
            close();
        }
    }

    public synchronized void close() {
        if (closed) {
            return;
        }
        closeAll(socket, input, output);
        closed = true;
    }

    public synchronized void send(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            log(e);
        }
    }

    public User getUser() {
        return user;
    }

    private boolean isJoined() {
        if (user == null) {
            send("먼저 /join|이름 명렁어로 입장해 주세요.");
            return false;
        }
        return true;
    }
}
