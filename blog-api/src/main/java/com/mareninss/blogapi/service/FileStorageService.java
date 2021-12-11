package com.mareninss.blogapi.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

   String save(MultipartFile file) throws IOException;

}
