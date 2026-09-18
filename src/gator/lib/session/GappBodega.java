package gator.lib.session;

/**
 *
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 * @version     0.1, 2020-08-21 17:26
 */
public class GappBodega {
        private String id;        
        private String nombre;
        private String nombreReal;
        private String capacidadInstalada;
        private String agrupador;
        public void setBodegaId(String _bodegaId) {
            this.id = _bodegaId;
        }
        public String getBodegaId() {
            return this.id;
        }
        public void setBodegaNombre(String _bodegaNombre) {
            this.nombre = _bodegaNombre;
        }
        public String getBodegaNombre() {
            return this.nombre;
        }
        public void setBodegaNombreReal(String _bdegaNombreReal) {
            this.nombreReal = _bdegaNombreReal;
        }
        public String getBodegaNombreReal() {
            return this.nombreReal;
        }
        public void setBodegaCapacidadInstalada(String _capacidadInstalada) {
            this.capacidadInstalada = _capacidadInstalada;
        }
        public String getBodegaCapacidadInstalada() {
            return this.capacidadInstalada;
        }
        public void setBodegaAgrupador(String _agrupador) {
            this.agrupador = _agrupador;
        }
        public String getBodegaAgrupador() {
            return this.agrupador;
        }
}
