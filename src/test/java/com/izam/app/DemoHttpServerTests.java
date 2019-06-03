package com.izam.app;

import com.izam.app.api.Constants;
import com.izam.app.api.StatusCode;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DemoHttpServerTests {
    private static final Logger log = LoggerFactory.getLogger(DemoHttpServerTests.class);

    private static final ApplicationProperties props =
            new ApplicationProperties("src/test/resources/application.properties");
    private final static int testPort = props.getServerPort();

    private static DemoHttpServer server = new DemoHttpServer(testPort);
    private String BASE_URL = String.format("http://localhost:%d/api", testPort);
    private String VERSION_URL = BASE_URL + "/version";

    @BeforeAll
    public static void setUp() {
        server.start();
    }

    @AfterAll
    public static void stop() {
        server.stop(1);
    }

    @Test
    void checkServerVersion() {
        try {
            URL url = new URL(VERSION_URL);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String line;
            if ((line = reader.readLine()) != null) {
                log.info(line);
            }
            reader.close();

            assertEquals(line, "version: 1.0");

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    JSONObject getAccountJSON(final String login) {
        try {
            // check account
            URL url = new URL(String.format("%s/%s=%s", BASE_URL, "account?login", login));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            String line;
            if ((line = reader.readLine()) != null) {
                log.info(line);
            }
            reader.close();
            return new JSONObject(line);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    void storeMoneyToAccount(final String login, final double value, final String controlLogin) {
        try {
            // Store money to account
            URL url = new URL(BASE_URL + "/account/deposit");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            String data = String.format(Locale.US, "{\"login\": \"%s\", \"amount\": \"%.2f\"}",
                    login, value);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            int responseCode = conn.getResponseCode();
            assertEquals(responseCode, 200);

            conn.disconnect();

            // check account data
            JSONObject obj = getAccountJSON(controlLogin);

            String testLogin = (String) obj.get("login");
            Double amount = (Double) obj.get("amount");
            assertEquals(testLogin, controlLogin);
            assertEquals(amount, value);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    void transferMoneyQuery(final String from, final String to,
                            final double value,
                            final int controlResponse) {
        try{
            // Transfer money
            URL url = new URL(BASE_URL + "/account/transfer");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            String data = String.format(Locale.US,
                    "{\"from\": \"%s\", \"to\": \"%s\", \"amount\": \"%.2f\"}",
                    from, to, value);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            int responseCode = conn.getResponseCode();
            assertEquals(responseCode, controlResponse);

            conn.disconnect();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    void transferMoney(final String from, final String to,
                       final double value,
                       final double controlFrom,
                       final double controlTo,
                       final int controlResponse) {
        try {
            // Transfer money
            transferMoneyQuery(from, to, value, controlResponse);

            // check accounts data after operation
            JSONObject objFrom = getAccountJSON(from);
            String testFrom = (String) objFrom.get("login");
            Double amountFrom = (Double) objFrom.get("amount");
            assertEquals(testFrom, from);
            assertEquals(amountFrom, controlFrom);

            JSONObject objTo = getAccountJSON(to);
            String testTo = (String) objTo.get("login");
            Double amountTo = (Double) objTo.get("amount");
            assertEquals(testTo, to);
            assertEquals(amountTo, controlTo);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }


    @Test
    void checkWorkflowOK() {
        storeMoneyToAccount("test", 50.50, "test");
        storeMoneyToAccount("testTwo", 20.02, "testTwo");
        transferMoney("test", "testTwo", 10.50,
                40.00, 30.52, StatusCode.OK.getCode());
    }

    @Test
    void checkTransferFailed() {
        storeMoneyToAccount("test", 60.51, "test");
        storeMoneyToAccount("testTwo", 20.02, "testTwo");

        // transfer money to non-existing acccount
        transferMoney("test", "wrong", 10.50,
                60.51, 30.52, StatusCode.NOT_FOUND.getCode());

        // transfer money from non-existing acccount
        transferMoney("dsffhfgh", "testTwo", 10.50,
                40.00, 30.52, StatusCode.NOT_FOUND.getCode());
    }
}
