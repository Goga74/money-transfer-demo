/**
 * @author Igor Zamiatin, capra.lanigera@gmail.com
 */
package com.izam.app;

class Application {
    private static final ApplicationProperties props =
            new ApplicationProperties("src/main/resources/application.properties");
    private final static int serverPort = props.getServerPort();

    public static void main(String[] args) {
        DemoHttpServer demoServer = new DemoHttpServer(serverPort);
        demoServer.start();
    }
}
