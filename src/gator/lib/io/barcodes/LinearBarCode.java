/*
 * Copyright (C) 2023 Sergio Basurto Juarez
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gator.lib.io.barcodes;

import gator.lib.logs.GappLogging;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.krysalis.barcode4j.impl.code128.Code128;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
/**
 * Generate all linear bar codes used in Soft Gator applications.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class LinearBarCode {
    /**
     * The logger for this class.
     * @see GappLogging
     */
    private final GappLogging gappLogging = new GappLogging();
    
    /**
     * The name for the file to be generated and write the bar code to.
     */
    private String file = "barcode.jpg";
    /**
     * The dpis for the generated image.
     */
    private int dpi = 300;
    /**
     * A canvas for the bar code.
     */
    private BitmapCanvasProvider canvas;
    /**
     * A code 39 object.
     */
    private final Code39Bean bean = new Code39Bean();
    
    /**
     * A code 128 object.
     */
    private final Code128 code128 = new Code128();
    
    /**
     * An EAN13 object. 
     */
    private final EAN13 ean13 = new EAN13();
    
    /**
     * Create an object.
     * @param file File name for the bar code.
     * @param inDPI The dpis.
     */
    public LinearBarCode(String file,int inDPI) {
        this.dpi = inDPI;
        this.file = file;
        initCanvas();
    }
    /**
     * Create an object.
     * @param file  File name for the bar code.
     */
    public LinearBarCode(String file) {
        this.file = file;
        initCanvas();
    }
    /**
     * Initialize canvas.
     */
    private void initCanvas() {        
        
    }
    /**
     * Create an EAN13 linear bar code.
     * @param message The string to be converted to bar code.
     */
    public void createEAN13(String message) {
        try {
                File outputFile = new File(this.file);
                OutputStream out = new FileOutputStream(outputFile);
                System.setProperty("java.awt.headless", "true"); 
                try {
                        //Set up the canvas provider for monochrome JPEG output
                        this.canvas = new BitmapCanvasProvider(out, "image/jpeg", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                        ean13.generateBarcode(this.canvas, message);
                        canvas.finish();
                } finally {
                        out.close();            
                }
        } catch(Exception e) {
            gappLogging.logIt(this.getClass().getCanonicalName(), gappLogging.getStackTraceString(e), "createean13", "createean13", 0);
        }         
    }
    /**
     * Create a Code128 linear bar code.
     * @param message The string to be converted to bar code.
     */
    public void create128(String message) {
        try {
                File outputFile = new File(this.file);
                OutputStream out = new FileOutputStream(outputFile);
                System.setProperty("java.awt.headless", "true"); 
                try {
                        //Set up the canvas provider for monochrome JPEG output
                        this.canvas = new BitmapCanvasProvider(out, "image/jpeg", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                        code128.generateBarcode(this.canvas, message);
                        canvas.finish();
                } finally {
                        out.close();            
                }
        } catch(Exception e) {
            gappLogging.logIt(this.getClass().getCanonicalName(), gappLogging.getStackTraceString(e), "create128", "create128", 0);
        }        
    }
}
