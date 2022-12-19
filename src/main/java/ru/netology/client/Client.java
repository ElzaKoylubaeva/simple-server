package ru.netology.client;

import lombok.extern.slf4j.Slf4j;
import ru.netology.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

@Slf4j
public class Client {
//    GET /index.html HTTP1.1
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = Server.SERVER_PORT;
        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(
                     clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(clientSocket.getInputStream()))) {
            Scanner sc = new Scanner(System.in);
            String line = null;

//            while (!"exit".equalsIgnoreCase(line)) {
            // reading from user
            log.info("select method and path: ");
            line = sc.nextLine();
            log.info("Entered request: {}", line);
            String[] entered = line.split(" ");
            out.println(line + " HTTP1.1");
            out.println("Host: localhost");
            out.println("User-Agent: java");
            out.println("Accept: */*");
            out.println();
            out.println();
            if (entered[0].equals("POST")) {
                out.println("SOME TEST BODY");
                out.println("SOME TEST BODY 2");
            }
            log.info("Waiting response");
            String data = in.readLine();
            log.info("Response:");
            log.info("{}", data);
            if (data != null && data.contains("200 OK")) {
                String file = null;
                while ((file = in.readLine()) != null) {
                    log.info("{}", file);
                }
            }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
