package gator.lib.session;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 2020-08-21 17:26
 */
public class GappSessionData {
        /**
         * Accounts and ware house that user has access to.
         */
         private GappCuentas cuentas = new GappCuentas();
         
        /**
         * Servers and databases that user has access to.
         */
         private ArrayList<HashMap<String,String>> servidores = new ArrayList<>();

        /**
         * Printers that user has access to.
         */
         private ArrayList<HashMap<String,String>> impresoras = new ArrayList<>();
         
         /**
          * Language for user.
          */
         private String idioma = "es";

         private String usuarioEmail = "";

         private String usuarioTelefono = "";
         
         /**
          * The path to submit choose server form.
          */
         private String afterChoose = "dummy_frame.jsp";
         
         /**
          * The path to submit choose printer form.
          */
         private String afterPrinter = "dummy_frame.jsp";

         /**
          * Debug level of specific user.
          */
         private String debugLevel = "0";
         
         /**
          * Session logo.
          */
         private String empresaLogo = "logo.png";
         
         
         /**
          * Debug level of specific user.
          */
         private long sessionTimeout = 1800000;
                  
         
         /**
         * The apps that has this session, example sales, warehouse, purchase, etc.,.
         */
         private ArrayList<HashMap<String,String>> modulos = new ArrayList<>();
         
         /**
         * The menus that has this session access to.
         */
         private ArrayList<HashMap<String,String>> menus = new ArrayList<>();
         
         /**
          * Set account.
          * 
          * @param account Account as map.
          */
         public void addAccounts(GappCuenta account) {
                cuentas.addCuenta(account.getCuentaId(), account);
         }
         /**
          * Allow to get accounts.
          * 
          * @return An accounts object.
          */
         public GappCuentas getAccounts() {
                return cuentas;
         }
         
         /**
          * Set accounts.
          * 
          * @param accounts An accounts object.
          */
         public void setAccounts(GappCuentas accounts) {
                cuentas = accounts;
         }
         
         /**
          * Allow to get accounts.
          * 
          * @return An array list of accounts.
          */
         public String getLanguage() {
                return idioma;
         }
         
         /**
          * Set accounts.
          * 
          * @param language String representing the user language.
          */
         public void setLanguage(String language) {
                idioma = language;
         }

         public String getUserEmail() {
                return usuarioEmail == null ? "" : usuarioEmail;
         }

         public void setUserEmail(String email) {
                usuarioEmail = email;
         }

         public String getUserPhone() {
                return usuarioTelefono == null ? "" : usuarioTelefono;
         }

         public void setUserPhone(String phone) {
                usuarioTelefono = phone;
         }
         
         /**
          * Allow to get servers.
          * 
          * @return An array list of servers.
          */
         public ArrayList<HashMap<String, String>> getServers() {
                return servidores;
         }
         
         /**
          * Set servers.
          * 
          * @param servers An array list of servers map string, string.
          */
         public void setServers(ArrayList<HashMap<String,String>> servers) {
                servidores = servers;
         }
         
         /**
          * Allow to get after choose path.
          * 
          * @return Path to submit after choose.
          */
         public String getAfterChoose() {
                return afterChoose;
         }
         
         /**
          * Set after choose path.
          * 
          * @param path The path to submit form after choose.
          */
         public void setAfterChoose(String path) {
                afterChoose = path;
         }
         
         /**
          * Allow to get session debug level.
          * 
          * @return A string with debug level.
          */
         public String getDebugLevel() {
                return debugLevel;
         }
         
         /**
          * Set debug level for session.
          * 
          * @param newDebugLevel String representing the user language.
          */
         public void setDebugLevel(String newDebugLevel) {
                debugLevel = newDebugLevel;
         }
         
         /**
          * Allow to get session logo.
          * 
          * @return A string logo file name.
          */
         public String getLogo() {
                return empresaLogo;
         }
         
         /**
          * Set debug level for session.
          * 
          * @param logo String representing logo file name.
          */
         public void setLogo(String logo) {
                empresaLogo = logo;
         }
         
         /**
          * Allow to get modules.
          * 
          * @return An array list modules.
          */
         public ArrayList<HashMap<String, String>> getModules() {
                return modulos;
         }
         
         /**
          * Set modules.
          * 
          * @param modules An array list of modules map string, string.
          */
         public void setModules(ArrayList<HashMap<String,String>> modules) {
                modulos = modules;
         }
         
         /**
          * Allow to get menus.
          * 
          * @return An array list menus.
          */
         public ArrayList<HashMap<String, String>> getMenus() {
                return menus;
         }
         
         /**
          * Set menus.
          * 
          * @param newMenus An array list of modules map string, string.
          */
         public void setMenus(ArrayList<HashMap<String,String>> newMenus) {
                menus = newMenus;
         }
         /**
          * Allow to get session timeout.
          * 
          * @return The milliseconds for timeout.
          */
         public long getSessionTimeout() {
                return sessionTimeout;
         }
         
         /**
          * Set session timeout in milliseconds.
          * 
          * @param timeout Long representing the session timeout.
          */
         public void setSessionTimeout(long timeout) {
                sessionTimeout = timeout;
         }

         /**
          * Allow to get after choose path.
          * 
          * @return Path to submit after choose printer.
          */
         public String getAfterPrinter() {
                return afterPrinter;
         }
         
         /**
          * Set after choose path.
          * 
          * @param path The path to submit form after choose printer.
          */
         public void setAfterPrinter(String path) {
                afterPrinter = path;
         }
         /**
          * Allow to retrieve printers.
          * 
          * @return An array list of printers.
          */
         public ArrayList<HashMap<String, String>> getPrinters() {
                return impresoras;
         }
         
         /**
          * Set servers.
          * 
          * @param printer Hash map representing the printer to be added.
          */
         public void addPrinter(HashMap<String,String> printer) {
                impresoras.add(printer);
         }
         /**
          * Allows to detect a mobile browser with user agent.
          * 
          * @param ua User agent string to process.
          *
          * @return Flag telling if is a mobile user agent, true is mobile false otherwise.
          */
         public boolean isMobile(String ua) {
                return ua.matches("(?i).*((android|bb\\d+|meego).+mobile|avantgo|bada\\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\\.(browser|link)|vodafone|wap|windows ce|xda|xiino).*")||ua.substring(0,4).matches("(?i)1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\\-(n|u)|c55\\/|capi|ccwa|cdm\\-|cell|chtm|cldc|cmd\\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\\-s|devi|dica|dmob|do(c|p)o|ds(12|\\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\\-|_)|g1 u|g560|gene|gf\\-5|g\\-mo|go(\\.w|od)|gr(ad|un)|haie|hcit|hd\\-(m|p|t)|hei\\-|hi(pt|ta)|hp( i|ip)|hs\\-c|ht(c(\\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\\-(20|go|ma)|i230|iac( |\\-|\\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\\/)|klon|kpt |kwc\\-|kyo(c|k)|le(no|xi)|lg( g|\\/(k|l|u)|50|54|\\-[a-w])|libw|lynx|m1\\-w|m3ga|m50\\/|ma(te|ui|xo)|mc(01|21|ca)|m\\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\\-2|po(ck|rt|se)|prox|psio|pt\\-g|qa\\-a|qc(07|12|21|32|60|\\-[2-7]|i\\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\\-|oo|p\\-)|sdk\\/|se(c(\\-|0|1)|47|mc|nd|ri)|sgh\\-|shar|sie(\\-|m)|sk\\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\\-|v\\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\\-|tdg\\-|tel(i|m)|tim\\-|t\\-mo|to(pl|sh)|ts(70|m\\-|m3|m5)|tx\\-9|up(\\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\\-|your|zeto|zte\\-");
         }
}
