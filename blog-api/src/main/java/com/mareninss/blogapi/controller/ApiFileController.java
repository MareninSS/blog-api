package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.ImageUploadResponse;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.service.FileStorageService;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ApiFileController {

  @Autowired
  private FileStorageService fileStorageService;

  @PostMapping("/api/image")
  @PreAuthorize("hasAnyAuthority('user:moderate','user:write')")
  public ResponseEntity<?> uploadImage(@RequestParam(name = "image") MultipartFile file)
      throws IOException {
    if (file.getSize() > 2097152) {
      ImageUploadResponse response = new ImageUploadResponse();
      ErrorDto error = new ErrorDto();
      error.setImage("размер файла больше 2.0 МБ");
      response.setResult(false);
      response.setErrors(error);
      return ResponseEntity.badRequest().body(response);
    } else if (!FilenameUtils.isExtension(
        file.getOriginalFilename(), "jpg", "png")) {
      ImageUploadResponse response = new ImageUploadResponse();
      ErrorDto error = new ErrorDto();
      error.setExtension("файл не формата jpg, png");
      response.setResult(false);
      response.setErrors(error);
      return ResponseEntity.badRequest().body(response);
    }
    return ResponseEntity.ok(fileStorageService.save(file));
  }

}
