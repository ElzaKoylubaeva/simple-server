package ru.netology;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Server {

    public final static int SERVER_PORT = 9999;

    private final static int THREAD_COUNT = 64;

    private final ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

    public void start() {
        log.info("Server start");
        try (final var serverSocket = new ServerSocket(SERVER_PORT)) {
            while (true) {
                final var socket = serverSocket.accept();
                handleConnection(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection(Socket socket) {
        log.info("New client connected: {}", socket.getInetAddress());
        ServerTask task = new ServerTask(socket);
        pool.submit(task);
    }
}
