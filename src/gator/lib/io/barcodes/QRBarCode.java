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

import gator.lib.io.files.GappFiles;
import gator.lib.logs.GappLogging;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Generate any QR bar code for Soft Gator applications.
 * @author <a href="mailto:sbasurto@soft-gator.com">Sergio Basurto Juárez</a>
 */
public class QRBarCode {
        /**
         * A logger for this class.
         * @see GappLogging
         */
        private final GappLogging gappLogging = new GappLogging();
        
        /**
         * A file access object for this class.
         * @see GappFiles
         */
        private final GappFiles gappFiles = new GappFiles();
        
        /**
         * Create an object.
         * @param fileName The name for file where the code will be stored.
         * @param qrStr The string to be use to generate the QR.
         */
        public QRBarCode(String fileName, String qrStr) {
            try {            
                byte[] b = qrStr.getBytes();
                String data;
                data = new String(b, "UTF8");
                BitMatrix matrix;
                gappLogging.logIt("getFFString", "QR Bar Code:" + GappFiles.IMAGE_DIR + "/spool/" + fileName + ".png", "qrbarcode","qrbarcode", 0);
                Path path = FileSystems.getDefault().getPath(GappFiles.IMAGE_DIR + "/spool/", fileName + ".png");
                com.google.zxing.Writer writer = new QRCodeWriter();
                matrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, 230, 230);                   
                MatrixToImageWriter.writeToPath(matrix, "png", path);
            } catch (Exception ex) {
                 gappLogging.logIt(this.getClass().getCanonicalName(), gappLogging.getStackTraceString(ex), "qrbarcode","qrbarcode", 0);
            }
        }
        /**
         * Create an object.
         * @param dir Directory where to store the bar code.
         * @param fileName The name for the file where the bar code will be stored.
         * @param qrStr The string to use for create the QR code.
         */
        public QRBarCode(String dir, String fileName, String qrStr) {
            try {            
                byte[] b = qrStr.getBytes();
                String data;
                data = new String(b, "UTF8");
                BitMatrix matrix;
                gappLogging.logIt("getFFString", "QR Bar Code:" + dir + "/spool/" + fileName + ".png", "qrbarcode","qrbarcode", 0);
                Path path = FileSystems.getDefault().getPath(dir + "/spool/", fileName + ".png");
                com.google.zxing.Writer writer = new QRCodeWriter();
                matrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, 230, 230);                        
                MatrixToImageWriter.writeToPath(matrix, "png", path);
            } catch (Exception ex) {
                 gappLogging.logIt(this.getClass().getCanonicalName(), gappLogging.getStackTraceString(ex), "qrbarcode","qrbarcode", 0);
            }
        }
}
