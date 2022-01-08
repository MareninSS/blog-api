package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.service.FileStorageService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

  private final Path root = Paths.get("src/main/resources/upload");

  @Override
  public String save(MultipartFile file) throws IOException {
    String uuidFile = UUID.randomUUID() + "." + file.getOriginalFilename();
    Path path = root.resolve(createFolderName(uuidFile));
    Files.createDirectories(path);
    if (!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
      try {
        Files.copy(file.getInputStream(), path.resolve(uuidFile),
            StandardCopyOption.REPLACE_EXISTING);
      } catch (Exception e) {
        throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
      }
    }
    return uuidFile;
  }

  public Path createFolderName(String id) {
    return Path.of(id.substring(0, 2), id.substring(2, 4), id.substring(4, 6));
  }
}
