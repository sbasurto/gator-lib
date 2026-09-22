package gator.lib.net.printing;


import com.google.gson.Gson;
import gator.lib.io.files.GappFiles;
import gator.lib.logs.GappLogging;
import gator.lib.sec.ids.GappUUIDFactory;
import com.google.gson.JsonObject;
import java.io.*;
import java.net.*;
import javax.swing.JTextArea;

/**
* PrintServer is the base specification for print labels and tickets in SoftGator, these labels and tickets
* are formed by special strings on the database
*
* This table contains all the elements needed to print to network printer or share printer.
* 
* @author      Sergio Basurto
* @version     2.0, 14 Ago 2019
* lpr -P Zebra_TLP2844  /opt/gator-apps/printsrv/print/4a71d2d6-c67d-4d99-843e-3a10c0da1472
*/

public class GappPrintServer implements Runnable {
    private static final java.util.concurrent.ScheduledExecutorService DEADLINES=java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread=new Thread(r,"print-write-deadlines");thread.setDaemon(true);return thread;
    });
	private PrintWriter     out;
	private BufferedReader  in;
	private Socket          socket = null;
        private int debugLevel = 0;
	private final JTextArea eventLog;
        private final GappFiles gappFiles = new GappFiles();
        private final GappUUIDFactory gappUUIdFactory = new GappUUIDFactory();
        private final GappLogging gappLogging = new GappLogging();
	
	/**
	* Constructor for the class and essentially receive the swing are where the logs will been
	* written.
	* 
        * @param debugLevel Debug level to be applied in this run.
	* @param eventLog	The swing text are where log will be written.
	* @param s 	The socket that get the connection to this print server
	* 
	*/
	public GappPrintServer(int debugLevel, JTextArea eventLog, Socket s) {
		this.eventLog = eventLog;
		this.socket = s;
                this.debugLevel = debugLevel;
	}
	/**
	* run Method just run the runnable class
	*/
        @Override
    public synchronized void run() {
        try (Socket connection=socket) {
            connection.setSoTimeout(20000);
            out=new PrintWriter(connection.getOutputStream(),true,java.nio.charset.StandardCharsets.UTF_8);
            in=new BufferedReader(new InputStreamReader(connection.getInputStream(),java.nio.charset.StandardCharsets.UTF_8));
            try {
                StringBuilder line=new StringBuilder(); int c; boolean received=false;
                while((c=in.read())!=-1) {
                    if(c=='\n') { if(!line.isEmpty()){doPrint(line.toString());received=true;line.setLength(0);} }
                    else {line.append((char)c);if(line.length()>1048576)throw new IOException("Print request too large");}
                }
                if(!line.isEmpty()){doPrint(line.toString());received=true;}
                if(!received)throw new IOException("Empty print request");
                out.println("0");
            } catch(Exception failure) {
                eventLog.append("[Server] Envío no confirmado: "+failure.getClass().getSimpleName()+"\n");
                out.println("1");
            }
        } catch(IOException failure) {eventLog.append("[Server] Error de conexión\n");}
    }

	/**
	* doPrint function receives a JSON string that is specified as follows and 
	* process the printing to any valid network or share printer.
	* 
	* @param string4print	In this parameter comes all the information needed to print to any
	* 				valid printer, the string is formed as follows:
        *                               {
        *                                   "hash":"xyz",
        *                                   "idctrl":"1235",
        *                                   "usuario":"dummy",
        *                                   "password":"dummy", 
        *                                   "printerIP":"10.10.10.1",
        *                                   "os":"windows",
        *                                   "tipo":"red",
        *                                   "comando":"",
        *                                   "secuenciaEscape":"kaklkalsklaskkl"
        *                               }				
	*/
	private void doPrint(String str4print) throws IOException {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(str4print, JsonObject.class);              
                
                String hash = jsonObject.get("hash") == null?"none":jsonObject.get("hash").getAsString();
                String idctrl = jsonObject.get("idctrl") == null?"none":jsonObject.get("idctrl").getAsString();
                String usuario = jsonObject.get("usuario") == null?"none":jsonObject.get("usuario").getAsString();
                String password = jsonObject.get("password") == null?"none":jsonObject.get("password").getAsString();
                String printerIP = jsonObject.get("printerIP") == null?"":jsonObject.get("printerIP").getAsString();
                String os = jsonObject.get("os") == null?"none":jsonObject.get("os").getAsString();
                os = os.toLowerCase();
                String tipoImpresion = jsonObject.get("tipo") == null?"none":jsonObject.get("tipo").getAsString();
                String printerPort = jsonObject.get("printerPort") == null?"none":jsonObject.get("printerPort").getAsString();
                String comando = (jsonObject.get("comando") == null || jsonObject.get("comando").isJsonNull())?"none":jsonObject.get("comando").getAsString();
                String secuenciaEscape = jsonObject.get("secuenciaEscape") == null?"none":jsonObject.get("secuenciaEscape").getAsString();
                String fileName = jsonObject.get("file") == null?"none":jsonObject.get("file").getAsString();
                String fileDir  = jsonObject.get("directory") == null?"none":jsonObject.get("directory").getAsString();
                secuenciaEscape = secuenciaEscape.replaceAll(":@@:", System.getProperty("line.separator")).replaceAll("::nnn::","\0");
                printLog("Debug", "hash: " + hash, 0, 5);
                printLog("Debug", "idtcrl: " + idctrl, 0, 5);
                printLog("Debug", "usuario: " + usuario, 0, 5);
                printLog("Debug", "password: [redacted]", 0, 5);
                printLog("Debug", "printerIP: " + printerIP, 0, 5);
                printLog("Debug", "os: " + os, 0, 5);
                printLog("Debug", "tipo impresión: " + tipoImpresion, 0, 5);
                printLog("Debug", "puerto: " + printerPort, 0, 5);
                printLog("Debug", "comando: " + comando, 0, 5);
                printLog("Debug", "secuencia de escape: " + secuenciaEscape, 0, 5);
                if(secuenciaEscape.contains("^FXGATOR_"))throw new IOException("Unresolved label file reference");
                if(tipoImpresion.equals("red")) {
                    try (Socket target=new Socket()) {
                        target.connect(new InetSocketAddress(printerIP,Integer.parseInt(printerPort)),5000);
                        target.setSoTimeout(5000);
                        // SO_TIMEOUT only covers reads: close on deadline to interrupt a blocked write.
                        var deadline=DEADLINES.schedule(() -> {try {target.close();}catch(IOException ignored) {}},20,java.util.concurrent.TimeUnit.SECONDS);
                        try {
                            target.getOutputStream().write((secuenciaEscape+"\n").getBytes(java.nio.charset.Charset.defaultCharset()));
                            target.getOutputStream().flush();
                            if(target.isClosed())throw new IOException("Print write timed out");
                        } finally {deadline.cancel(false);}
                    }
                } else {
                    java.nio.file.Path directory=java.nio.file.Path.of(GappFiles.PRINT_DIR);
                    java.nio.file.Files.createDirectories(directory);
                    java.nio.file.Path printFile=java.nio.file.Files.createTempFile(directory,"label-",".zpl");
                    java.nio.file.Files.writeString(printFile,secuenciaEscape,java.nio.charset.Charset.defaultCharset());
                    Process process;
                    if(os.equals("windows")) {
                        comando=comando.replace("::usuario::",usuario).replace("::password::",password)
                            .replace("::printerIP::",printerIP).replace("::archivo::",printFile.toString());
                        process=Runtime.getRuntime().exec(comando);
                    } else if(os.equals("linux")) {
                        process=new ProcessBuilder("lp","-d",printerIP,printFile.toString()).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.DISCARD).start();
                    } else throw new IOException("Unsupported print OS");
                    try {
                        if(!process.waitFor(20,java.util.concurrent.TimeUnit.SECONDS)){process.destroyForcibly();throw new IOException("Print command timed out");}
                        if(process.exitValue()!=0)throw new IOException("Print command failed");
                    } catch(InterruptedException interrupted){Thread.currentThread().interrupt();throw new IOException("Print interrupted",interrupted);}
                }
	}	
	/**
	* Logs depending on debug level
	*/
	private void printLog(String who, String what2log, int withSys, int debugLevel){	
                eventLog.append("[" + who + "] : " + what2log  + "\n");
                if(withSys == 1){
                        System.out.println("[" + who + "] : " + what2log  + "\n");		
                }
	}
}
