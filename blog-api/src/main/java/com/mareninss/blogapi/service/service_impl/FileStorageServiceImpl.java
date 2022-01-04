package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.service.FileStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

  private final Path root = Paths.get("upload").normalize();


  @Override
  public String save(MultipartFile file) throws IOException {
    Path path = root.resolve(createThreeRandomFolders());
    Files.createDirectories(path);
    if (!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
      try {
        Files.copy(file.getInputStream(), path.resolve(file.getOriginalFilename()),
            StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
      }
    }
    String replaced = path.resolve(file.getOriginalFilename()).toString().replace("\\","/");
    return "/" + replaced;
  }

  public Path createThreeRandomFolders() {
    String hash = String.valueOf(Math.abs(new Random().hashCode()));
    return Path.of(hash.substring(0, 3), hash.substring(3, 6), hash.substring(6));
  }
}
