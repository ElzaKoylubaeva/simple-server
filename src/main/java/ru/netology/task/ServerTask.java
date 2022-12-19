package ru.netology.task;

import lombok.extern.slf4j.Slf4j;
import ru.netology.handler.Handler;
import ru.netology.model.Metadata;
import ru.netology.model.Request;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ServerTask implements Runnable {

    private final static List<String> DEFAULT_PATHS = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/events.html", "/events.js");

    private final Socket clientSocket;

    private final String clientAddress;

    private final Map<Metadata, Handler> handlers;

    public ServerTask(Socket socket, Map<Metadata, Handler> handlers) {
        this.clientSocket = socket;
        this.clientAddress = socket.getInetAddress().toString();
        this.handlers = handlers;

        fillDefaultMethods(handlers);
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             final var out = new BufferedOutputStream(clientSocket.getOutputStream());
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");


            log.info("Received: {} from client: {}", requestLine, clientAddress);
            if (parts.length != 3) {
                // just close socket
                log.warn("Need at least 3 arguments separated by space. From client: {}", clientAddress);
                return;
            }

            final var method = parts[0];
            final var path = parts[1];
            final var headers = new HashMap<String, String>();
            //code to read and print headers
            String headerLine = null;
            while ((headerLine = in.readLine()) != null && !headerLine.equals("")) {
                String[] split = headerLine.split(":");
                headers.put(split[0].trim(), split[1].trim());
            }

            BufferedReader body = null;
            if (!method.equals("GET")) {
                in.readLine();
                body = in;
            }

            var metadata = Metadata.builder()
                    .method(method)
                    .path(path)
                    .build();
            Handler handler = handlers.get(metadata);

            if (handler == null) {
                log.warn("Not valid path: {}. From client: {}", metadata, clientAddress);
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
            } else {
                var requestBuilder = Request.builder()
                        .method(method)
                        .path(path);
                if (!headers.isEmpty()) {
                    requestBuilder.headers(headers);
                }
                if (body != null) {
                    requestBuilder.body(body);
                }
                handler.handle(requestBuilder.build(), out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void fillDefaultMethods(Map<Metadata, Handler> handlers) {
        for (String path : DEFAULT_PATHS) {
            var metadata = Metadata.builder()
                    .method("GET")
                    .path(path)
                    .build();
            handlers.put(metadata, new Handler() {
                @Override
                public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                    final var filePath = Path.of(".", "public", request.getPath());
                    final var mimeType = Files.probeContentType(filePath);

                    final var length = Files.size(filePath);
                    responseStream.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    Files.copy(filePath, responseStream);
                    responseStream.flush();
                }
            });
        }
        //
        var metadata = Metadata.builder()
                .method("GET")
                .path("/classic.html")
                .build();
        handlers.put(metadata, new Handler() {
            @Override
            public void handle(Request request, BufferedOutputStream responseStream) throws IOException {
                final var filePath = Path.of(".", "public", request.getPath());
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                log.info("Classic special case. From client: {}", clientAddress);
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                responseStream.write(content);
                responseStream.flush();
            }
        });
    }
}
