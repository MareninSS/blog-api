package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.TagResponse;

public interface TagService {

  TagResponse getTags(String query);

}
