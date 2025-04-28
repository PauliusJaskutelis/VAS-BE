package com.fashiontrunk.fashiontrunkapi.Services;

import com.fashiontrunk.fashiontrunkapi.Dto.ImageMetadataDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class MetadataService extends ContainerServiceBase {

    @Override protected String getContainerName() { return "metadata-service"; }
    @Override protected String getImageName() { return "metadata-service"; }
    @Override protected int getPort() { return 5001; }
    @Override protected String getHealthCheckUrl() { return "http://localhost:" + this.getPort() + "/docs"; }
     public Map<String, Object> extractModelMetadata(MultipartFile model) throws IOException, InterruptedException {
         startContainer();

         HttpHeaders headers = new HttpHeaders();
         headers.setContentType(MediaType.MULTIPART_FORM_DATA);
         ByteArrayResource fileAsResource = new ByteArrayResource(model.getBytes()) {
             @Override public String getFilename() { return model.getOriginalFilename(); }
         };

         MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
         body.add("file", fileAsResource);

         HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

         ResponseEntity<Map> response = restTemplate.exchange(
                 "http://localhost:" + getPort() + "/extract-metadata",
                 HttpMethod.POST,
                 requestEntity,
                 Map.class
         );

         return response.getBody();
     }

    public ImageMetadataDTO extractImageMetadata(MultipartFile image) throws IOException, InterruptedException {
        startContainer(); // Užtikrina, kad servisas paleistas

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileAsResource = new ByteArrayResource(image.getBytes()) {
            @Override public String getFilename() { return image.getOriginalFilename(); }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileAsResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ImageMetadataDTO> response = restTemplate.exchange(
                "http://localhost:" + getPort() + "/extract-image-metadata",
                HttpMethod.POST,
                requestEntity,
                ImageMetadataDTO.class
        );

        return response.getBody();
    }
}
