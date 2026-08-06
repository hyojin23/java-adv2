package network.mychat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static util.MyLogger.log;

public class Client {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        log("클라이언트 시작");

        try (Socket socket = new Socket("localhost", PORT);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            log("소켓 연결: " + socket);

            Scanner scanner = new Scanner(System.in);

            Thread receiver = new Thread(() -> {
                try {
                    while (true) {
                        String message = input.readUTF();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    log("클라이언트 소켓 종료");
                }
            });

            receiver.start();

            System.out.println("== 명령어를 입력해 주세요. ==");
            System.out.println("/join|{name} - 입장 " +
                    "\n /message|{내용} - 메시지 " +
                    "\n /change|{name} - 이름 변경 " +
                    "\n /users - 전체 사용자 출력 " +
                    "\n /exit - 종료");
            while (true) {
                String command = scanner.nextLine();
                output.writeUTF(command);

                if (command.equals("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            log(e);
        }
    }
}
