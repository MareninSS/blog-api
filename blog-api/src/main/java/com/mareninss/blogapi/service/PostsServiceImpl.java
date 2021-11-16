package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.PostDto;
import com.mareninss.blogapi.entity.ModerationStatusEnum;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class PostsServiceImpl implements PostsService {

  private final Byte IS_ACTIVE = 1;
  private final ModerationStatusEnum MODERATION_STATUS = ModerationStatusEnum.ACCEPTED;
  private final long CURRENT_TIME = new Date().getTime();


  @Autowired
  private PostRepository postRepository;

  @Override
  public PostsResponse getPosts(int offset, int limit, String mode) {

    List<PostDto> postsDto = postRepository
        .getAllByIsActiveAndModerationStatus(
            IS_ACTIVE,
            MODERATION_STATUS
        ).stream()
        .filter(post -> post.getTime().getTime() < CURRENT_TIME)
        .map(DtoMapper::mapToPostDto)
        .collect(Collectors.toList());

    PostsResponse postsResponse = new PostsResponse();
    postsResponse.setCount(postsDto.size());
    postsResponse.setPosts(postsDto);
    return postsResponse;
  }
}
