package com.example.signature;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.png.PngDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import org.apache.commons.imaging.formats.jpeg.xmp.JpegXmpRewriter;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;
import java.util.Iterator;

@Service
public class SignatureService {

    private final RSASSASigner signer;
    private final RSASSAVerifier verifier;

    public SignatureService(KeyPair keyPair) {
        this.signer = new RSASSASigner(keyPair.getPrivate());
        this.verifier = new RSASSAVerifier((RSAPublicKey) keyPair.getPublic());
    }

    public byte[] sign(byte[] data, String format) throws Exception {
        byte[] digest = digest(data);
        JWSObject jws = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), new Payload(digest));
        jws.sign(signer);
        String detached = jws.serialize(true);
        if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
            return embedJwsJpeg(data, detached);
        } else if (format.equalsIgnoreCase("png")) {
            return embedJwsPng(data, detached);
        }
        throw new IllegalArgumentException("Unsupported format");
    }

    public boolean verify(byte[] data, String format) throws Exception {
        try {
            byte[] digest = digest(data);
            String jws;
            if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                jws = extractJwsJpeg(data);
            } else if (format.equalsIgnoreCase("png")) {
                jws = extractJwsPng(data);
            } else {
                return false;
            }
            if (jws == null) {
                return false;
            }
            JWSObject jwsObject = JWSObject.parse(jws, new Payload(digest));
            return jwsObject.verify(verifier);
        } catch (Exception e) {
            // Invalid image data, JWS format, or verification failure
            return false;
        }
    }

    private byte[] digest(byte[] data) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        if (image == null) {
            throw new IllegalArgumentException("Invalid image data");
        }
        DataBuffer buffer = image.getRaster().getDataBuffer();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (int i = 0; i < buffer.getSize(); i++) {
            int val = buffer.getElem(i);
            md.update((byte) (val >> 24));
            md.update((byte) (val >> 16));
            md.update((byte) (val >> 8));
            md.update((byte) val);
        }
        return md.digest();
    }

    private byte[] embedJwsJpeg(byte[] data, String jws) throws Exception {
        String xmp = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<x:xmpmeta xmlns:x=\"adobe:ns:meta/\">" +
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">" +
                "<rdf:Description xmlns:ns1=\"https://example.com/jws/1.0/\" ns1:JWS=\"" + jws + "\"/>" +
                "</rdf:RDF></x:xmpmeta>";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new JpegXmpRewriter().updateXmpXml(new ByteArrayInputStream(data), baos, xmp);
        return baos.toByteArray();
    }

    private byte[] embedJwsPng(byte[] data, String jws) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
        String formatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(formatName);
        
        // Only add iTXt metadata without modifying IHDR
        IIOMetadataNode text = new IIOMetadataNode("iTXt");
        IIOMetadataNode entry = new IIOMetadataNode("iTXtEntry");
        entry.setAttribute("keyword", "jws");
        entry.setAttribute("compressionFlag", "FALSE");
        entry.setAttribute("compressionMethod", "0");
        entry.setAttribute("languageTag", "");
        entry.setAttribute("translatedKeyword", "jws");
        entry.setAttribute("text", jws);
        text.appendChild(entry);
        root.appendChild(text);
        metadata.mergeTree(formatName, root);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        writer.write(metadata, new IIOImage(image, null, metadata), param);
        ios.close();
        writer.dispose();
        return baos.toByteArray();
    }

    private String extractJwsJpeg(byte[] data) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data));
        XmpDirectory dir = metadata.getFirstDirectoryOfType(XmpDirectory.class);
        if (dir != null && dir.getXMPMeta() != null) {
            return dir.getXMPMeta().getPropertyString("https://example.com/jws/1.0/", "JWS");
        }
        return null;
    }

    private String extractJwsPng(byte[] data) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(data));
        for (PngDirectory dir : metadata.getDirectoriesOfType(PngDirectory.class)) {
            // Check all available tags
            for (int i = 0; i < 1000; i++) { // Try a range of possible tag numbers
                if (dir.hasTagName(i)) {
                    String desc = dir.getDescription(i);
                    if (desc != null && desc.contains("jws")) {
                        // Handle different text formats
                        if (desc.startsWith("jws: ")) {
                            return desc.substring(5);
                        } else if (desc.contains("jws=")) {
                            return desc.substring(desc.indexOf("jws=") + 4);
                        } else {
                            String[] parts = desc.split(":", 2);
                            if (parts.length == 2 && "jws".equals(parts[0].trim())) {
                                return parts[1].trim();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
