package gator.lib.io.barcodes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ZplImageFilesTest {
    @TempDir Path root;
    private byte[] image() throws Exception {
        BufferedImage image=new BufferedImage(8,1,BufferedImage.TYPE_INT_RGB);
        image.setRGB(0,0,0xffffff);
        ByteArrayOutputStream out=new ByteArrayOutputStream();ImageIO.write(image,"png",out);return out.toByteArray();
    }
    @Test void cachesAndFreezesLabelsOnFilesystem() throws Exception {
        var first=ZplImageFiles.cache(root,10,1,image(),203,8,1,0);
        Path graphic=root.resolve(first.path());
        var modified=Files.getLastModifiedTime(graphic);
        assertEquals("^GFA,1,1,1,7F",Files.readString(graphic));
        assertEquals(first,ZplImageFiles.cache(root,10,1,image(),203,8,1,0));
        assertEquals(modified,Files.getLastModifiedTime(graphic));
        String job="^FXGATOR_JOB:jobs/10/1/test.zpl^FS^XA^FO0,0^FXGATOR_IMAGE:"+first.path()+":"+first.sha256()+"^FS^FS^XZ^FXGATOR_END^FS";
        String resolved=ZplImageFiles.resolve(root,job);
        assertEquals("^XA^FO0,0^GFA,1,1,1,7F^FS^XZ",resolved);
        Files.delete(graphic);
        assertEquals(resolved,ZplImageFiles.resolve(root,job));
        Files.writeString(root.resolve("jobs/10/1/test.zpl"),"corrupted");
        assertThrows(java.io.IOException.class,()->ZplImageFiles.resolve(root,job));
    }
    @Test void rejectsUntrustedPathsAndImages() throws Exception {
        assertThrows(java.io.IOException.class,()->ZplImageFiles.safePath(root,"../outside",true));
        Path outside=Files.createTempDirectory("label-outside");
        try {
            Files.createSymbolicLink(root.resolve("linked"),outside);
            assertThrows(java.io.IOException.class,()->ZplImageFiles.safePath(root,"linked/file.zpl",true));
        } finally {Files.delete(outside);}
        assertThrows(java.io.IOException.class,()->ZplImageFiles.convert(new byte[]{1,2},20,20,0));
        assertThrows(java.io.IOException.class,()->ZplImageFiles.convert(image(),0,20,0));
        assertThrows(java.io.IOException.class,()->ZplImageFiles.resolve(root,"^FXGATOR_IMAGE:bad^FS"));
        assertEquals("^GFA,8,8,1,0080808080808080",ZplImageFiles.convert(image(),1,8,90));
    }
}
