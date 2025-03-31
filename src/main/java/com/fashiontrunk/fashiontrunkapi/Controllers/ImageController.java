package com.fashiontrunk.fashiontrunkapi.Controllers;

import com.fashiontrunk.fashiontrunkapi.Const;
import com.fashiontrunk.fashiontrunkapi.Util.ImageValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/image")
public class ImageController {

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (!Objects.equals(contentType, "image/png") &&
                !Objects.equals(contentType, "image/jpeg")) {
            return new ResponseEntity<>("Bad Header", HttpStatus.NOT_ACCEPTABLE);
        }
        if (!ImageValidation.isValidImage(file)) {
            return new ResponseEntity<>("File Not Accepted", HttpStatus.BAD_REQUEST);
        }

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        File destinationFile = Paths.get(Const.IMAGE_DIR, filename).toFile();

        // Sukuriam katalogą, jei reikia
        destinationFile.getParentFile().mkdirs();

        // Išsaugom failą
        file.transferTo(destinationFile);

        // TODO: čia kviesim Python skriptą ir gausim klasifikaciją

        return ResponseEntity.ok("Failas išsaugotas: " + destinationFile.getAbsolutePath());
    }
}
