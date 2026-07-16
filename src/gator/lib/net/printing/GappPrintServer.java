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
	public synchronized void run(){
		String inLine;
		try{
			out = new PrintWriter(socket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			eventLog.append("[Server] : Peticion de impresion desde " + socket.getInetAddress() +"\n");                
			eventLog.append("[Server] : Leyendo \n");
			while((inLine = in.readLine()) != null){
                                if(this.debugLevel >= 200) {
                                    eventLog.append("[Server] : Linea leída \n");
                                    eventLog.append("[Server] : " + inLine + " \n");
                                }
				doPrint(inLine);
			}
			eventLog.append("[Server] : Terminó de leer \n");                                
			eventLog.append("[Server] : La impresion ha sido enviada\n");
			out.println("0");
		}catch(IOException e){
			eventLog.append("[Server] : Error de E/S  " + e.toString() + "\n");
			out.println("1");
			return;
		}catch(NullPointerException e){
			eventLog.append("[Server] : Error de un nulo " + e.toString() + "\n");
			out.println("1");
			return;
		}
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
	private void doPrint(String str4print){
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
                String comando = jsonObject.get("comando").isJsonNull()?"none":jsonObject.get("comando").getAsString();
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
                gappFiles.setDir(GappFiles.PRINT_DIR);
                gappFiles.write2File(secuenciaEscape, gappUUIdFactory.getUUID());
                if(tipoImpresion.equals("red")) {
                        printLog("Server", "Impresión en red inicio", 0, 5);
                        try {
                                Socket socketInterno = new Socket(printerIP, Integer.parseInt(printerPort));  
                                socketInterno.setSoTimeout(1000);
                                PrintWriter outBuffer = new PrintWriter(socketInterno.getOutputStream(), true);                
                                BufferedReader inBuffer = new BufferedReader(new InputStreamReader(socketInterno.getInputStream()));
                                outBuffer.println(secuenciaEscape);
                                outBuffer.close();
                                inBuffer.close();
                                socketInterno.close();
                        } catch(Exception e){
                                printLog("Server", "Excepción Host Desconocido:" + printerIP + ":" + printerPort, 0, 5);                            
                        }
                        printLog("Server", "Impresión en red finalizada", 0, 5);
                } else {
                        Process proc;
                        if(!fileName.equals("none") && !fileDir.equals("none") && !fileName.equals("") && !fileDir.equals("")) {
                                gappFiles.setDir(fileDir);
                                gappFiles.setFileName(fileName);
                        }
                        if(os.equals("windows")) {
                                printLog("Server", "Sistema Operativo:" + os, 0, 5);
                                printLog("Debug", "Es WINDOWS", 0, 5);
				/*String netCmdDel   = "cmd /c net use /delete LPT2 ";
				String netCmd   = "cmd /c net use LPT2 " + printerIP + "  /user:\"" + usuario + "\" "+  password;
				String copyCmd  = "cmd /c copy "+ Files.getFileName() +" LPT2";*/
                                comando = comando.replaceAll("::usuario::", usuario);
                                comando = comando.replaceAll("::password::", password);
                                comando = comando.replaceAll("::printerIP::", printerIP);
                                comando = comando.replaceAll("::archivo::", gappFiles.getFileName());
				printLog("Debug", "Comando ejecutado \"" + comando + "\"", 1, 5);				
                                try {
                                    proc = Runtime.getRuntime().exec(comando);
                                    printLog("Server", "Proceso ejecutado " + proc, 0, 5);
                                    /*proc = Runtime.getRuntime().exec(netCmd);
                                    printLog("Server", "Proceso ejecutado " + proc, 0, 5);
                                    proc = Runtime.getRuntime().exec(copyCmd);
                                    printLog("Server", "Proceso ejecutado " + proc, 0, 5);*/
                                } catch (Exception e) {
                                    printLog("Server", "Excepción:" + gappLogging.getStackTraceString(e) + ":" + printerPort, 0, 5);                            
                                }
                        } else if (os.equals("linux")) {                                
                                printLog("Server", "Sistema Operativo:" + os, 0, 5);
                                String copyCmd = "lp -d " + printerIP + " " + gappFiles.getFileName() ;						
                                printLog("Debug", "Comandos ejecutados \"" + copyCmd + "\"", 1, 5);
                                try {
                                    proc = Runtime.getRuntime().exec(new String[] {"sh","-c",copyCmd});
                                    printLog("Server", "Proceso ejecutado " + proc, 0, 5);
                                } catch (Exception e) {
                                    printLog("Server", "Excepción:" + gappLogging.getStackTraceString(e) + ":" + printerPort, 0, 5);                            
                                }                                						                                
                        } else {
                                printLog("Server", "Sistema Operativo Desconocido " + os, 0, 5);
                        }
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
