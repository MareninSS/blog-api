package com.mareninss.blogapi.service;


import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.PostDto;
import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.Tag;
import com.mareninss.blogapi.entity.User;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostsServiceImpl implements PostsService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;


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
    MODERATION_STATUS = ModerationStatus.ACCEPTED.toString();

  }

  @Override
  public PostsResponse getPosts(int offset, int limit, String mode) {
    Pageable page = PageRequest.of(offset, limit);

    Comparator<PostDto> recentMode = Comparator.comparing(PostDto::getTimestamp).reversed();
    Comparator<PostDto> popularMode = Comparator.comparing(PostDto::getCommentCount);
    Comparator<PostDto> bestMode = Comparator.comparing(PostDto::getLikeCount);
    Comparator<PostDto> earlyMode = Comparator.comparing(PostDto::getTimestamp);
    switch (mode) {
      case "recent":

        return getPostsWithModeOffsetLimit(page, recentMode);
      case "popular":
        return getPostsWithModeOffsetLimit(page, popularMode);
      case "best":
        return getPostsWithModeOffsetLimit(page, bestMode);
      case "early":
        return getPostsWithModeOffsetLimit(page, earlyMode);
    }
    return postsResponse;
  }


  @Override
  public PostsResponse getPostsByQuery(int offset, int limit, String query) {
    Pageable page = PageRequest.of(offset, limit);
    Comparator<PostDto> recentMode = Comparator.comparing(PostDto::getTimestamp).reversed();
    if (query == null || query.isBlank()) {
      return getPostsWithModeOffsetLimit(page, recentMode);
    } else {
      int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
              IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, query, page)
          .size();
      List<PostDto> postsDto = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
              IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, query, page)
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
  @Transactional
  public PostByIdResponse getPostById(int id, Principal principal) {
    final boolean isActive = true;
    final int LIKE = 1;
    final int DISLIKE = -1;
    final int MODERATOR = 1;

    Optional<Post> postById = postRepository
        .findPostByIdAndIsActiveAndTimeIsLessThanAndModerationStatus(id, IS_ACTIVE, CURRENT_TIME,
            ModerationStatus.ACCEPTED);
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

      int viewCount = postById.get().getViewCount();

      if (principal != null) {
        User currentUser = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (Objects.equals(currentUser.getId(), postById.get().getAuthorId())
            || currentUser.getIsModerator() == MODERATOR) {
          postByIdResponse.setViewCount(viewCount);//получаем
        }else{
          postByIdResponse.setViewCount(viewCount);//получаем
          postRepository.updateViewCountById(id);//обновляем + 1
        }
      } else {
        postByIdResponse.setViewCount(viewCount);//получаем
        postRepository.updateViewCountById(id);//обновляем + 1
      }

      postByIdResponse.setComments(DtoMapper.mapToCommentsDto(postById.get()));
      postByIdResponse.setTags(postById.get().getTags().stream().map(Tag::getName).collect(
          Collectors.toList()));
      return postByIdResponse;
    }
    return null;
  }


  public PostsResponse getPostsWithModeOffsetLimit(Pageable page,
      Comparator<PostDto> comparator) {

    int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
            IS_ACTIVE,
            CURRENT_TIME, MODERATION_STATUS)
        .size();
    List<PostDto> postsDto = postRepository
        .getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffset(
            IS_ACTIVE, CURRENT_TIME,
            MODERATION_STATUS, page
        ).stream()
        .map(DtoMapper::mapToPostDto)
        .sorted(comparator)
        .collect(Collectors.toList());
    postsResponse.setCount(count);
    postsResponse.setPosts(postsDto);
    return postsResponse;
  }
}