package gator.lib.io.barcodes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/** Filesystem-only graphics and immutable rendered label jobs. No printer I/O. */
public final class ZplImageFiles {
    public static final int MAX_IMAGE_BYTES=16*1024*1024;
    private static final int MAX_ZPL_BYTES=1024*1024;
    private static final Pattern IMAGE=Pattern.compile("\\^FXGATOR_IMAGE:([a-zA-Z0-9/_-]+\\.zpl):([a-f0-9]{64})\\^FS");
    private static final Pattern JOB=Pattern.compile("\\^FXGATOR_JOB:([a-zA-Z0-9/_-]+\\.zpl)\\^FS(.*?)\\^FXGATOR_END\\^FS",Pattern.DOTALL);
    private static final Object[] LOCKS=new Object[64];
    static { for(int i=0;i<LOCKS.length;i++)LOCKS[i]=new Object(); }
    private ZplImageFiles() {}
    public record Graphic(String path,String sha256,String sourceSha256) {}

    public static String sha256(byte[] bytes) {
        try {return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}
        catch(NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}
    }
    public static byte[] readImage(Path source) throws IOException {
        if(Files.size(source)>MAX_IMAGE_BYTES)throw new IOException("Image too large");
        try(var in=Files.newInputStream(source)) {
            byte[] data=in.readNBytes(MAX_IMAGE_BYTES+1);
            if(data.length>MAX_IMAGE_BYTES)throw new IOException("Image too large");
            return data;
        }
    }
    public static String convert(byte[] source,int width,int height,int rotation) throws IOException {
        if(source.length>MAX_IMAGE_BYTES||width<1||height<1||width>2048||height>2048||rotation<0||rotation>270||rotation%90!=0)
            throw new IOException("Invalid image specification");
        BufferedImage original;
        try(ImageInputStream input=ImageIO.createImageInputStream(new ByteArrayInputStream(source))) {
            var readers=ImageIO.getImageReaders(input);
            if(!readers.hasNext())throw new IOException("Unsupported image");
            ImageReader reader=readers.next();
            try {
                String format=reader.getFormatName();
                if(!format.equalsIgnoreCase("PNG")&&!format.equalsIgnoreCase("JPEG"))throw new IOException("Only JPEG/PNG supported");
                reader.setInput(input);
                if((long)reader.getWidth(0)*reader.getHeight(0)>24_000_000)throw new IOException("Image pixel limit exceeded");
                original=reader.read(0);
            } finally {reader.dispose();}
        }
        int baseW=rotation%180==0?width:height,baseH=rotation%180==0?height:width;
        BufferedImage fitted=new BufferedImage(baseW,baseH,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics=fitted.createGraphics();
        try {
            graphics.setColor(Color.WHITE);graphics.fillRect(0,0,baseW,baseH);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            double scale=Math.min((double)baseW/original.getWidth(),(double)baseH/original.getHeight());
            int w=Math.max(1,(int)Math.round(original.getWidth()*scale)),h=Math.max(1,(int)Math.round(original.getHeight()*scale));
            graphics.drawImage(original,(baseW-w)/2,(baseH-h)/2,w,h,null);
        } finally {graphics.dispose();}
        int row=(width+7)/8;
        byte[] bitmap=new byte[row*height];
        int[][] bayer={{0,8,2,10},{12,4,14,6},{3,11,1,9},{15,7,13,5}};
        for(int y=0;y<height;y++)for(int x=0;x<width;x++) {
            int sx=x,sy=y;
            if(rotation==90){sx=y;sy=baseH-1-x;}
            if(rotation==180){sx=baseW-1-x;sy=baseH-1-y;}
            if(rotation==270){sx=baseW-1-y;sy=x;}
            int rgb=fitted.getRGB(sx,sy);
            int gray=(299*((rgb>>16)&255)+587*((rgb>>8)&255)+114*(rgb&255))/1000;
            if(gray<(bayer[y%4][x%4]*16+8))bitmap[y*row+x/8]|=(byte)(0x80>>(x%8));
        }
        return "^GFA,"+bitmap.length+","+bitmap.length+","+row+","+HexFormat.of().withUpperCase().formatHex(bitmap);
    }
    public static Graphic cache(Path root,int account,int warehouse,byte[] source,int dpi,int width,int height,int rotation) throws IOException {
        if(account<1||warehouse<1||!(dpi==203||dpi==300||dpi==600))throw new IOException("Invalid scope/resolution");
        String sourceHash=sha256(source);
        String relative="images/"+account+"/"+warehouse+"/"+sourceHash+"-"+dpi+"-"+width+"x"+height+"-"+rotation+"-v1.zpl";
        Path target=safePath(root,relative,true);
        synchronized(LOCKS[Math.floorMod(target.hashCode(),LOCKS.length)]) {
            try(var channel=FileChannel.open(safePath(root,relative+".lock",true),StandardOpenOption.CREATE,StandardOpenOption.WRITE);var lock=channel.lock()) {
                if(!Files.exists(target,LinkOption.NOFOLLOW_LINKS)) {
                    String generated=convert(source,width,height,rotation);
                    atomicWrite(safePath(root,relative+".sha256",true),sha256(generated.getBytes(StandardCharsets.US_ASCII)));
                    atomicWrite(target,generated);
                }
                byte[] graphic=readBounded(target);
                validateGraphic(new String(graphic,StandardCharsets.US_ASCII));
                if(!sha256(graphic).equals(Files.readString(safePath(root,relative+".sha256",false)).trim()))throw new IOException("Cached graphic checksum mismatch");
                return new Graphic(relative,sha256(graphic),sourceHash);
            }
        }
    }
    public static String resolve(Path root,String input) throws IOException {
        Matcher jobs=JOB.matcher(input);StringBuilder result=new StringBuilder();
        while(jobs.find()) {
            String path=jobs.group(1),template=jobs.group(2);
            if(!path.startsWith("jobs/"))throw new IOException("Invalid job path");
            Path target=safePath(root,path,true);
            String content;
            synchronized(LOCKS[Math.floorMod(target.hashCode(),LOCKS.length)]) {
                try(var channel=FileChannel.open(safePath(root,path+".lock",true),StandardOpenOption.CREATE,StandardOpenOption.WRITE);var lock=channel.lock()) {
                    if(Files.exists(target,LinkOption.NOFOLLOW_LINKS)) {
                        byte[] stored=readBounded(target);
                        String expected=Files.readString(safePath(root,path+".sha256",false),StandardCharsets.US_ASCII).trim();
                        if(!sha256(stored).equals(expected))throw new IOException("Stored label checksum mismatch");
                        content=new String(stored,StandardCharsets.UTF_8);
                    } else {
                        content=resolveGraphics(root,template);
                        atomicWrite(safePath(root,path+".sha256",true),sha256(content.getBytes(StandardCharsets.UTF_8)));
                        atomicWrite(target,content);
                    }
                }
            }
            jobs.appendReplacement(result,Matcher.quoteReplacement(content));
        }
        jobs.appendTail(result);
        if(result.indexOf("^FXGATOR_")>=0)throw new IOException("Unresolved label file reference");
        if(result.toString().getBytes(StandardCharsets.UTF_8).length>MAX_ZPL_BYTES)throw new IOException("Label payload too large");
        return result.toString();
    }
    private static String resolveGraphics(Path root,String template) throws IOException {
        Matcher images=IMAGE.matcher(template);StringBuilder result=new StringBuilder();
        while(images.find()) {
            byte[] bytes=readBounded(safePath(root,images.group(1),false));
            if(!sha256(bytes).equals(images.group(2)))throw new IOException("Graphic checksum mismatch");
            String graphic=new String(bytes,StandardCharsets.US_ASCII);validateGraphic(graphic);
            images.appendReplacement(result,Matcher.quoteReplacement(graphic));
        }
        images.appendTail(result);
        if(result.indexOf("^FXGATOR_")>=0)throw new IOException("Malformed graphic reference");
        return result.toString();
    }
    static void validateGraphic(String graphic) throws IOException {
        Matcher m=Pattern.compile("\\^GFA,(\\d+),(\\d+),(\\d+),([0-9A-F]+)").matcher(graphic);
        if(!m.matches())throw new IOException("Invalid graphic data");
        try {
            int total=Integer.parseInt(m.group(1)),size=Integer.parseInt(m.group(2)),row=Integer.parseInt(m.group(3));
            if(total<1||total!=size||row<1||row>256||total%row!=0||total/row>2048||m.group(4).length()!=total*2)throw new IOException("Invalid graphic lengths");
        } catch(NumberFormatException e){throw new IOException("Invalid graphic sizes",e);}
    }
    public static Path safePath(Path root,String relative,boolean createParents) throws IOException {
        if(relative.isEmpty()||Path.of(relative).isAbsolute()||relative.contains("\\")||relative.contains(".."))throw new IOException("Unsafe file reference");
        Files.createDirectories(root);Path base=root.toRealPath(),target=base.resolve(relative).normalize();
        if(!target.startsWith(base))throw new IOException("Unsafe file reference");
        Path cursor=base;
        for(Path part:base.relativize(target)) {
            cursor=cursor.resolve(part);
            if(Files.isSymbolicLink(cursor))throw new IOException("Symlink file reference rejected");
            if(!cursor.equals(target)&&!Files.exists(cursor)&&createParents) {
                try {Files.createDirectory(cursor);}catch(java.nio.file.FileAlreadyExistsException race) {
                    if(Files.isSymbolicLink(cursor)||!Files.isDirectory(cursor))throw new IOException("Unsafe directory",race);
                }
            }
        }
        return target;
    }
    private static byte[] readBounded(Path path) throws IOException {
        try(var in=Files.newInputStream(path,LinkOption.NOFOLLOW_LINKS)) {
            byte[] bytes=in.readNBytes(MAX_ZPL_BYTES+1);
            if(bytes.length>MAX_ZPL_BYTES)throw new IOException("ZPL file too large");
            return bytes;
        }
    }
    private static void atomicWrite(Path target,String data) throws IOException {
        byte[] bytes=data.getBytes(StandardCharsets.UTF_8);
        if(bytes.length>MAX_ZPL_BYTES)throw new IOException("ZPL file too large");
        Path temp=Files.createTempFile(target.getParent(),".label-",".tmp");
        try {
            Files.write(temp,bytes);
            try(var channel=FileChannel.open(temp,StandardOpenOption.WRITE)){channel.force(true);}
            Files.move(temp,target,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);
        } finally {Files.deleteIfExists(temp);}
    }
}
