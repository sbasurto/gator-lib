package gator.lib.session;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 2020-08-21 17:26
 */
public class GappCuenta {
        /**
         * Accounts and ware house that user has access to.
         */
        private LinkedHashMap<String, GappBodega> bodegas = new LinkedHashMap<>();
        
        private String id;        
        private String nombre;
        private String nombreCorto;
        private String email;
        private String rfc;
        private String estado; 
        private String sistema; 
        
        
        public void setCuentaId(String _cuentaId) {
            this.id = _cuentaId;
        }
        public String getCuentaId() {
            return this.id;
        }
        public void setCuentaNombre(String _cuentaNombre) {
            this.nombre = _cuentaNombre;
        }
        public String getCuentaNombre() {
            return this.nombre;
        }
        public void setCuentaNombreCorto(String _cuentaNombreCorto) {
            this.nombreCorto = _cuentaNombreCorto;
        }
        public String getCuentaNombreCorto() {
            return this.nombreCorto;
        }
        public void setCuentaEmail(String _cuentaEmail) {
            this.email = _cuentaEmail;
        }
        public String getCuentaEmail() {
            return this.email;
        }
        public void setCuentaRfc(String _cuentaRfc) {
            this.rfc = _cuentaRfc;
        }
        public String getCuentaRfc() {
            return this.rfc;
        }
        public void setCuentaEstado(String _cuentaEstado) {
            this.estado = _cuentaEstado;
        }
        public String getCuentaEstado() {
            return this.estado;
        }
        public void setCuentaSistema(String _cuentaSistema) {
            this.sistema = _cuentaSistema;
        }
        public String getCuentaSistema() {
            return this.sistema;
        }
        /**
         * Add a warehouse.
         * 
         * @param bodega Account as map.
         */
        public void addBodega(GappBodega bodega) {
                this.bodegas.put(bodega.getBodegaId(), bodega);
        }
        /**
         * Allow to get accounts.
         * 
         * @return An array list of accounts.
         */
        public LinkedHashMap<String, GappBodega> getBodegas() {
               return bodegas;
        }
        public String getBodegaKey(int index) {                        
                if(index > bodegas.keySet().size()) {                    
                    index = bodegas.keySet().size() - 1;
                }
                return (String) bodegas.keySet().toArray()[index];
        }
        public GappBodega getBodega(String key) {
                return bodegas.get(key);
        }
        public GappBodega findWarehouseById(String id) {
                for (GappBodega bodega : bodegas.values()) {
                        if (bodega.getBodegaId().equals(id)) {
                                return bodega;
                        }
                }
                return null;
        }
}
