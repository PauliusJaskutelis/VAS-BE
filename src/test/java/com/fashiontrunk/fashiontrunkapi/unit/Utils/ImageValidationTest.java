package com.fashiontrunk.fashiontrunkapi.unit.Utils;

import com.fashiontrunk.fashiontrunkapi.Util.ImageValidation;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class ImageValidationTest {

    @Test
    void validPngFileShouldPassValidation() {
        byte[] pngSignature = new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", pngSignature);
        assertTrue(ImageValidation.isValidImage(file));
    }

    @Test
    void validJpegFileShouldPassValidation() {
        byte[] jpegSignature = new byte[]{(byte)0xFF, (byte)0xD8};
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", jpegSignature);
        assertTrue(ImageValidation.isValidImage(file));
    }

    @Test
    void invalidFileShouldFailValidation() {
        byte[] wrongSignature = new byte[]{0x00, 0x01, 0x02, 0x03};
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", wrongSignature);
        assertFalse(ImageValidation.isValidImage(file));
    }

    @Test
    void validImageBundleShouldPassValidation() {
        byte[] pngSignature = new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        MockMultipartFile[] files = {
            new MockMultipartFile("file1", "test1.png", "image/png", pngSignature),
            new MockMultipartFile("file2", "test2.png", "image/png", pngSignature)
        };
        assertTrue(ImageValidation.isValidImageBundle(files));
    }

    @Test
    void invalidImageBundleShouldFailValidation() {
        byte[] wrongSignature = new byte[]{0x00, 0x01, 0x02, 0x03};
        MockMultipartFile[] files = {
            new MockMultipartFile("file1", "test1.png", "image/png", wrongSignature),
            new MockMultipartFile("file2", "test2.png", "image/png", wrongSignature)
        };
        assertFalse(ImageValidation.isValidImageBundle(files));
    }
}
