package com.fashiontrunk.fashiontrunkapi.Util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ImageValidation {

    private static boolean isValidPng(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] signature = new byte[8];
            if (inputStream.read(signature) != signature.length) {
                return false;
            }

            return signature[0] == (byte) 0x89 &&
                    signature[1] == (byte) 0x50 &&
                    signature[2] == (byte) 0x4E &&
                    signature[3] == (byte) 0x47 &&
                    signature[4] == (byte) 0x0D &&
                    signature[5] == (byte) 0x0A &&
                    signature[6] == (byte) 0x1A &&
                    signature[7] == (byte) 0x0A;
        } catch (IOException exception) {
            return false;
        }
    }

    private static boolean isValidJpeg(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] signature = new byte[2];
            if (inputStream.read(signature) != signature.length) {
                return false;
            }

            return signature[0] == (byte) 0xFF &&
                    signature[1] == (byte) 0xD8;
        } catch (IOException exception) {
            return false;
        }
    }

    public static boolean isValidImage(MultipartFile file){
        return isValidJpeg(file) || isValidPng(file);
    }

    public static boolean isValidImageBundle(MultipartFile[] files) {
        for (MultipartFile file: files) {
            String contentType = file.getContentType();

            if(!isValidImage(file)){
                return false;
            }
            if (!Objects.equals(contentType, "image/png") &&
                    !Objects.equals(contentType, "image/jpeg")) {
                return false;
            }
        }
        return true;
    }
}
