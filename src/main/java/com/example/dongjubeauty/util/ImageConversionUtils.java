package com.example.dongjubeauty.util;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 이미지 포맷 변환 유틸리티.
 * 현재는 HEIC/HEIF 이미지를 JPEG로 변환하는 역할만 담당합니다.
 */
public final class ImageConversionUtils {

    private static final Set<String> HEIC_EXT = Set.of("heic", "heif", "heics");
    private static final Set<String> HEIC_MEDIA_TYPES = Set.of(
            "image/heic",
            "image/heif",
            "image/heic-sequence",
            "image/heif-sequence"
    );

    static {
        // TwelveMonkeys 플러그인 탐색
        ImageIO.scanForPlugins();
    }

    private ImageConversionUtils() {
    }

    public static Optional<ConvertedImage> convertToJpegIfHeic(MultipartFile file) throws IOException {
        if (file == null || !isHeic(file)) {
            return Optional.empty();
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IOException("HEIC 이미지를 디코딩할 수 없습니다.");
            }

            byte[] jpegBytes = writeJpeg(image);
            String targetFilename = buildTargetFilename(file.getOriginalFilename());
            return Optional.of(new ConvertedImage(jpegBytes, targetFilename, MediaType.IMAGE_JPEG));
        }
    }

    public static boolean isHeic(MultipartFile file) {
        if (file == null) {
            return false;
        }
        return isHeic(file.getOriginalFilename(), file.getContentType());
    }

    private static boolean isHeic(String filename, String contentType) {
        if (contentType != null && HEIC_MEDIA_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            return true;
        }
        String ext = extractExtension(filename);
        return ext != null && HEIC_EXT.contains(ext);
    }

    private static String extractExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return null;
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static String buildTargetFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "upload.jpg";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0) {
            return originalFilename + ".jpg";
        }
        return originalFilename.substring(0, dot) + ".jpg";
    }

    private static byte[] writeJpeg(BufferedImage image) throws IOException {
        java.util.Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("JPEG 인코더를 찾을 수 없습니다.");
        }
        ImageWriter writer = writers.next();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(image);
            writer.dispose();
            return baos.toByteArray();
        }
    }

    public record ConvertedImage(byte[] bytes, String filename, MediaType mediaType) {
        public Resource asResource() {
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
        }
    }
}
