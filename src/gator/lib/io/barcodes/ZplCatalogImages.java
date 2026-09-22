package gator.lib.io.barcodes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gator.lib.db.GappSQLStatement;
import gator.lib.db.helpers.GappDBHelper;
import gator.lib.io.files.GappFiles;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Upload hook and backfill entry point. Stores only paths/checksums in PostgreSQL. */
public final class ZplCatalogImages {
    private ZplCatalogImages() {}
    public static Path root() {return Path.of(System.getProperty("gator.labels.root",GappFiles.PRINT_DIR+"labels"));}
    public static void main(String[] args) throws Exception {
        if(args.length!=3)throw new IllegalArgumentException("Usage: ZplCatalogImages databaseConfig account warehouse");
        refresh(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
    }
    public static void refresh(String config,int account,int warehouse) throws IOException {
        GappDBHelper database=new GappDBHelper(config);
        GappSQLStatement query=new GappSQLStatement();
        query.setQuery("select to_regclass('public.app_imagen_zpl')::text as present");
        var available=database.execute(query);
        if(available==null||available.isEmpty()||available.getFirst().get("present")==null)return; // Legacy databases.
        query=new GappSQLStatement();
        query.setQuery("SELECT DISTINCT i.imagen_id,i.imagen_ruta_real,d.dpi,g.value->>'width' AS width,g.value->>'height' AS height,coalesce(g.value->>'rotation','0') AS rotation FROM app_etiqueta_diseno d CROSS JOIN LATERAL jsonb_each(d.graficos) g JOIN app_productos p ON p.cuenta_id=d.cuenta_id AND p.bodega_id=d.bodega_id JOIN app_imagenes i ON i.imagen_id=p.imagen_id AND i.cuenta_id=p.cuenta_id AND i.bodega_id=p.bodega_id WHERE d.publicada AND d.cuenta_id=cast(? as integer) AND d.bodega_id=cast(? as integer) AND g.value->>'source'='producto'");
        query.addParam(Integer.toString(account));query.addParam(Integer.toString(warehouse));
        var images=database.execute(query);
        if(images==null)throw new IOException("Cannot read image catalog");
        IOException failure=null;
        // ponytail: scan this tenant on upload; add changed-image filtering only if upload latency requires it.
        for(var image:images) {
            try {
                String id=image.get("imagen_id"),sourcePath=image.get("imagen_ruta_real");
                if(sourcePath==null)throw new IOException("Missing catalog path");
                Path allowed=Path.of(GappFiles.IMAGE_DIR).toRealPath();
                Path source=Path.of(sourcePath);
                if(!source.isAbsolute())source=allowed.resolve(source);
                if(Files.isDirectory(source))source=source.resolve(id);
                source=source.toRealPath();
                if(!source.startsWith(allowed))throw new IOException("Image outside catalog root");
                byte[] bytes=ZplImageFiles.readImage(source);
                int dpi=Integer.parseInt(image.get("dpi")),width=Integer.parseInt(image.get("width")),height=Integer.parseInt(image.get("height")),rotation=Integer.parseInt(image.get("rotation"));
                var graphic=ZplImageFiles.cache(root(),account,warehouse,bytes,dpi,width,height,rotation);
                // Never publish a conversion if a concurrent upload changed the source.
                if(!graphic.sourceSha256().equals(ZplImageFiles.sha256(ZplImageFiles.readImage(source))))throw new IOException("Image changed during conversion");
                JsonObject request=new JsonObject();request.addProperty("cuentaId",account);request.addProperty("bodegaId",warehouse);
                request.addProperty("imagenId",id);request.addProperty("sourcePath",sourcePath);request.addProperty("sourceSha256",graphic.sourceSha256());
                request.addProperty("dpi",dpi);request.addProperty("width",width);request.addProperty("height",height);request.addProperty("rotation",rotation);
                request.addProperty("path",graphic.path());request.addProperty("sha256",graphic.sha256());request.addProperty("usuario","catalog-image-converter");
                GappSQLStatement save=new GappSQLStatement();save.setStoreProcedure("app_fn_register_image_zpl");save.addParam(request.toString());
                var response=JsonParser.parseString(database.executeStore(save)).getAsJsonObject().getAsJsonArray("responses").get(0).getAsJsonObject();
                if(!response.get("resNum").getAsString().equals("0"))throw new IOException("Cannot register image derivative");
            } catch(IOException|RuntimeException e) {
                // A replaced/corrupt source must not keep serving the preceding photograph.
                JsonObject invalidate=new JsonObject();invalidate.addProperty("action","invalidate");
                invalidate.addProperty("cuentaId",account);invalidate.addProperty("bodegaId",warehouse);
                invalidate.addProperty("imagenId",image.get("imagen_id"));invalidate.addProperty("sourcePath",image.get("imagen_ruta_real"));
                GappSQLStatement save=new GappSQLStatement();save.setStoreProcedure("app_fn_register_image_zpl");save.addParam(invalidate.toString());
                database.executeStore(save);
                failure=new IOException("One or more product images could not be prepared",e);
            }
        }
        if(failure!=null)throw failure;
    }
}
