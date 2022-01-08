package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.ErrorsResponse;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.service.FileStorageService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiFileController {

  @Autowired
  private FileStorageService fileStorageService;

  private final Path root = Paths.get("src/main/resources/upload");

  @PostMapping("/api/image")
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<?> uploadImage(@RequestParam(name = "image") MultipartFile file)
      throws IOException {
    if (file.getSize() > 2097152) {
      ErrorsResponse response = new ErrorsResponse();
      ErrorDto error = new ErrorDto();
      error.setImage("размер файла больше 2.0 МБ");
      response.setResult(false);
      response.setErrors(error);
      return ResponseEntity.badRequest().body(response);
    } else if (!FilenameUtils.isExtension(
        file.getOriginalFilename(), "jpg", "png")) {
      ErrorsResponse response = new ErrorsResponse();
      ErrorDto error = new ErrorDto();
      error.setExtension("файл не формата jpg, png");
      response.setResult(false);
      response.setErrors(error);
      return ResponseEntity.badRequest().body(response);
    }
    return ResponseEntity.ok("/api/image/" + fileStorageService.save(file));
  }

  @GetMapping("/api/image/{id}")
  public ResponseEntity<?> downloadImage(@PathVariable String id) {

    String mimeType = URLConnection.guessContentTypeFromName(id);
    if (mimeType == null) {
      return ResponseEntity.badRequest().build();
    }
    MediaType mediaType = MediaType.parseMediaType(mimeType);
    File file = new File(root + "/" + fileStorageService.createFolderName(id) + "/" + id);

    InputStreamResource resource = null;
    try {
      resource = new InputStreamResource(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
        .contentType(mediaType)
        .contentLength(file.length())
        .body(resource);
  }
}
