package ru.netology;

import lombok.extern.slf4j.Slf4j;
import ru.netology.handler.Handler;
import ru.netology.model.Request;
import ru.netology.server.Server;

import java.io.BufferedOutputStream;
import java.io.IOException;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Main starting");
        var server = new Server();

        server.addHandler("GET", "/messages", new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                log.info("Request: {}", request);
                String msg = "Message: " + request.getMethod() + " " + request.getPath();
                responseStream.write(msg.getBytes());
                responseStream.flush();
            }
        });

        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                log.info("Request: {}", request);
                String msg = "Message: " + request.getMethod() + " " + request.getPath();
                responseStream.write(msg.getBytes());
                responseStream.flush();
            }
        });

        server.start();

        log.info("Main finished");
    }
}


