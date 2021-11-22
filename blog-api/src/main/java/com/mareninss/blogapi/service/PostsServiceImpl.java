package com.mareninss.blogapi.service;

import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.PostDto;
import com.mareninss.blogapi.entity.ModerationStatusEnum;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.Tag;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PostsServiceImpl implements PostsService {

  @Autowired
  private PostRepository postRepository;

  private final Byte IS_ACTIVE;
  private final String MODERATION_STATUS;
  private final Date CURRENT_TIME;
  private final PostsResponse postsResponse;
  private final PostByIdResponse postByIdResponse;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  public PostsServiceImpl() {
    IS_ACTIVE = 1;
    CURRENT_TIME = new Date();
    postsResponse = new PostsResponse();
    postByIdResponse = new PostByIdResponse();
    MODERATION_STATUS = ModerationStatusEnum.ACCEPTED.toString();// Почему-то при nativeQuery не видит тип String
    // если Enum (хотя в entity Enum строго типизирован в String)
  }

  @Override
  public PostsResponse getPosts(int offset, int limit, String mode) {

    Comparator<PostDto> recentMode = Comparator.comparing(PostDto::getTimestamp).reversed();
    Comparator<PostDto> popularMode = Comparator.comparing(PostDto::getCommentCount);
    Comparator<PostDto> bestMode = Comparator.comparing(PostDto::getLikeCount);
    Comparator<PostDto> earlyMode = Comparator.comparing(PostDto::getTimestamp);
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

  @Override
  public PostsResponse getPostsByQuery(int offset, int limit, String query) {
    Comparator<PostDto> recentMode = Comparator.comparing(PostDto::getTimestamp).reversed();
    if (query == null || query.isBlank()) {
      return getPostsWithModeOffsetLimit(offset, limit, recentMode);
    } else {
      int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
              IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, limit, offset, query)
          .size();
      List<PostDto> postsDto = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
              IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, limit, offset, query)
          .stream()
          .map(DtoMapper::mapToPostDto)
          .sorted(recentMode)
          .collect(Collectors.toList());
      postsResponse.setCount(count);
      postsResponse.setPosts(postsDto);
      return postsResponse;
    }
  }

  @Override
  public PostsResponse getPostsByDates(int offset, int limit, String date) throws ParseException {
    Pageable page = PageRequest.of(offset, limit);
    Date dateNow = dateFormat.parse(date);
    String validDate = dateFormat.format(dateNow);

    int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
        IS_ACTIVE, CURRENT_TIME, validDate, MODERATION_STATUS, page).size();
    List<PostDto> postDtos = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
            IS_ACTIVE, CURRENT_TIME, validDate, MODERATION_STATUS, page)
        .stream()
        .map(DtoMapper::mapToPostDto)
        .collect(Collectors.toList());
    postsResponse.setCount(count);
    postsResponse.setPosts(postDtos);
    return postsResponse;
  }

  @Override
  public PostsResponse getPostsByTag(int offset, int limit, String tag) {
    Pageable page = PageRequest.of(offset, limit);
    int count = postRepository
        .findPostsByTagNameAndIsActiveAndTimeIsLessThanAndModerationStatus(IS_ACTIVE, CURRENT_TIME,
            MODERATION_STATUS, tag, page).size();
    List<PostDto> postDtos = postRepository
        .findPostsByTagNameAndIsActiveAndTimeIsLessThanAndModerationStatus(IS_ACTIVE, CURRENT_TIME,
            MODERATION_STATUS, tag, page)
        .stream()
        .map(DtoMapper::mapToPostDto)
        .collect(Collectors.toList());
    postsResponse.setCount(count);
    postsResponse.setPosts(postDtos);
    return postsResponse;
  }

  @Override
  public PostByIdResponse getPostById(int id) {
    final boolean isActive = true;
    final int LIKE = 1;
    final int DISLIKE = 0;

    Optional<Post> postById = postRepository
        .findPostByIdAndIsActiveAndTimeIsLessThanAndModerationStatus(id, IS_ACTIVE, CURRENT_TIME,
            ModerationStatusEnum.ACCEPTED);
    if (postById.isPresent()) {
      postByIdResponse.setId(postById.get().getId());
      postByIdResponse.setTimestamp(postById.get().getTime().getTime() / 1000);
      postByIdResponse.setActive(isActive);
      postByIdResponse.setUser(DtoMapper.mapToUserPostDto(postById.get().getUser()));
      postByIdResponse.setTitle(postById.get().getTitle());
      postByIdResponse.setText(postById.get().getText());
      postByIdResponse.setLikeCount(
          (int) postById.get().getPostVotes().stream().filter(like -> like.getValue() == LIKE)
              .count());
      postByIdResponse.setDislikeCount(
          (int) postById.get().getPostVotes().stream()
              .filter(dislike -> dislike.getValue() == DISLIKE).count());
      postByIdResponse.setViewCount(
          1);// 22.11.2021  сделать счетчик (Спросить у куратора как узнать сессию текущего пользователя, авторизован ли пользователь)
      postByIdResponse.setComments(DtoMapper.mapToCommentsDto(postById.get()));
      postByIdResponse.setTags(postById.get().getTags().stream().map(Tag::getName).collect(
          Collectors.toList()));
      return postByIdResponse;
    }
    return null;
  }


  public PostsResponse getPostsWithModeOffsetLimit(int offset, int limit,
      Comparator<PostDto> comparator) {
    int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
            IS_ACTIVE,
            CURRENT_TIME, MODERATION_STATUS)
        .size();
    List<PostDto> postsDto = postRepository
        .getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffset(
            IS_ACTIVE, CURRENT_TIME,
            MODERATION_STATUS, limit, offset
        ).stream()
        .map(DtoMapper::mapToPostDto)
        .sorted(comparator)
        .collect(Collectors.toList());
    postsResponse.setCount(count);
    postsResponse.setPosts(postsDto);
    return postsResponse;
  }

}
