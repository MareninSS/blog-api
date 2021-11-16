package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.PostDto;
import com.mareninss.blogapi.entity.ModerationStatusEnum;
import java.util.Comparator;
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
  private final PostsResponse postsResponse = new PostsResponse();

  @Autowired
  private PostRepository postRepository;

  @Override
  public PostsResponse getPosts(int offset, int limit, String mode) {

    Comparator<PostDto> recentMode = Comparator.comparing(PostDto::getTimestamp);
    Comparator<PostDto> popularMode = Comparator.comparing(PostDto::getCommentCount);
    Comparator<PostDto> bestMode = Comparator.comparing(PostDto::getLikeCount);
    Comparator<PostDto> earlyMode = Comparator.comparing(PostDto::getTimestamp).reversed();
    switch (mode) {
      case "recent":
        return getPostsWithModeOffsetLimit(offset, limit, recentMode);
      case "popular":
        return getPostsWithModeOffsetLimit(offset, limit, popularMode);
      case "best":
        return getPostsWithModeOffsetLimit(offset, limit, bestMode);
      case "early":
        return getPostsWithModeOffsetLimit(offset, limit, earlyMode);
    }
    return postsResponse;
  }


  public PostsResponse getPostsWithModeOffsetLimit(int offset, int limit,
      Comparator<PostDto> comparator) {
        int count = postRepository.getAllByIsActiveAndModerationStatus(IS_ACTIVE, MODERATION_STATUS)
        .size();
    List<PostDto> postsDto = postRepository
        .getAllByIsActiveAndModerationStatus(
            IS_ACTIVE,
            MODERATION_STATUS
        ).stream()
        .filter(post -> post.getTime().getTime() < CURRENT_TIME)
        .map(DtoMapper::mapToPostDto)
        .sorted(comparator)
        .skip(offset)
        .limit(limit)
        .collect(Collectors.toList());
    postsResponse.setCount(count);
    postsResponse.setPosts(postsDto);
    return postsResponse;
  }
}
