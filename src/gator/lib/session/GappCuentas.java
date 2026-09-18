package gator.lib.session;

import java.util.LinkedHashMap;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 2022-05-10 12:16
 */
public class GappCuentas {
        /**
         * Accounts and ware house that user has access to.
         */
        private LinkedHashMap<String, GappCuenta> cuentas = new LinkedHashMap<>();
        
        public void addCuenta(String key, GappCuenta cuenta) {
                cuentas.put(key, cuenta);
        }
        public GappCuenta getCuenta(String key) {
                return cuentas.get(key);
        }
        public GappCuenta getCuenta(int index) {
                return cuentas.get(getKey(index));
        }
        public GappCuenta findById(String id) {
                for (GappCuenta cuenta : cuentas.values()) {
                        if (cuenta.getCuentaId().equals(id)) {
                                return cuenta;
                        }
                }
                return null;
        }
        public int size() {
                return cuentas.size();
        }
        public String getKey(int index) {
                return (String) cuentas.keySet().toArray()[index];
        }
        public LinkedHashMap<String, GappCuenta> getCuentas() {
                return cuentas;
        }
}
