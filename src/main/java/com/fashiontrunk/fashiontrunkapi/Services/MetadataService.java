package com.fashiontrunk.fashiontrunkapi.Services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class MetadataService extends ContainerServiceBase {

    @Override protected String getContainerName() { return "metadata-service"; }
    @Override protected String getImageName() { return "metadata-service"; }
    @Override protected int getPort() { return 5001; }
    @Override protected String getHealthCheckUrl() { return "http://localhost:5001/docs"; }
     public Map<String, Object> extractMetadata(MultipartFile model) throws IOException, InterruptedException {
         ensureContainerRunning();

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
}
