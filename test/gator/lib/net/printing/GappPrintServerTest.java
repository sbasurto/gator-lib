package gator.lib.net.printing;
import com.google.gson.JsonObject;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.swing.JTextArea;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class GappPrintServerTest {
    private String request(int port) throws Exception {
        try(ServerSocket listener=new ServerSocket(0);Socket client=new Socket("127.0.0.1",listener.getLocalPort())) {
            Thread worker=new Thread(new GappPrintServer(0,new JTextArea(),listener.accept()));worker.start();client.setSoTimeout(3000);
            JsonObject j=new JsonObject();j.addProperty("tipo","red");j.addProperty("printerIP","127.0.0.1");j.addProperty("printerPort",Integer.toString(port));j.addProperty("comando","");j.addProperty("secuenciaEscape","^XA^FDáñ^FS^XZ");
            client.getOutputStream().write((j+"\n").getBytes(StandardCharsets.UTF_8));client.shutdownOutput();
            String ack=new BufferedReader(new InputStreamReader(client.getInputStream(),StandardCharsets.UTF_8)).readLine();worker.join(3000);assertFalse(worker.isAlive());return ack;
        }
    }
    @Test void refusesSuccessWhenPrinterConnectionFails() throws Exception {
        int port;try(ServerSocket closed=new ServerSocket(0)){port=closed.getLocalPort();}
        assertEquals("1",request(port));
    }
    @Test void sendsUtf8AndAcknowledgesTransportOnly() throws Exception {
        try(ServerSocket printer=new ServerSocket(0)) {
            var incoming=new java.util.concurrent.CompletableFuture<String>();
            Thread reader=new Thread(()->{try(Socket socket=printer.accept()){incoming.complete(new String(socket.getInputStream().readAllBytes(),StandardCharsets.UTF_8));}catch(Exception e){incoming.completeExceptionally(e);}});reader.start();
            assertEquals("0",request(printer.getLocalPort()));
            assertTrue(incoming.get(3,java.util.concurrent.TimeUnit.SECONDS).contains("áñ"));reader.join(3000);
        }
    }
}
