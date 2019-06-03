package com.izam.app;

import com.izam.app.api.account.AccountInfoHandler;
import com.izam.app.api.income.IncomeHandler;
import com.izam.app.api.transfer.TransferHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static com.izam.app.Configuration.*;
import static com.izam.app.Configuration.getErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

public class DemoHttpServer {
    private static final Logger log = LoggerFactory.getLogger(DemoHttpServer.class);

    private String VERSION = "1.0";
    private int serverPort;
    HttpServer server;

    public DemoHttpServer(final int port) {
        this.serverPort = port;
    }

    public void start() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(serverPort), 0);

            AccountInfoHandler infoHandler = new AccountInfoHandler(getUserAccountService(), getObjectMapper(),
                    getErrorHandler());
            server.createContext("/api/account", infoHandler::handle);

            IncomeHandler incomeHandler = new IncomeHandler(getUserAccountService(), getObjectMapper(),
                    getErrorHandler());
            server.createContext("/api/account/deposit", incomeHandler::handle);

            TransferHandler transferHandler = new TransferHandler(getUserAccountService(), getObjectMapper(),
                    getErrorHandler());
            server.createContext("/api/account/transfer", transferHandler::handle);

            server.createContext("/api/version", (exchange -> {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String respText = String.format("version: %s", VERSION);
                    exchange.sendResponseHeaders(200, respText.getBytes().length);
                    OutputStream output = exchange.getResponseBody();
                    output.write(respText.getBytes());
                    output.flush();
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
                exchange.close();
            }));

            server.setExecutor(null); // creates a default executor
            server.start();

            log.info(String.format("HttpServer started on port: %d", this.serverPort));
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    public void stop(final int delayInSecounds) {
        server.stop(delayInSecounds);
    }
}
