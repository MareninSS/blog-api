package com.mareninss.blogapi.service;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

  String save(MultipartFile file) throws IOException;

  Path createFolderName(String uuid);

}
