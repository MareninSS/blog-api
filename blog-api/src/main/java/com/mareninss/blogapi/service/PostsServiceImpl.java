package com.mareninss.blogapi.service;


import com.mareninss.blogapi.api.request.PostDataRequest;
import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostDataResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.TagRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.dto.PostDto;
import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.Tag;
import com.mareninss.blogapi.entity.User;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
  @Autowired
  private TagRepository tagRepository;


  private final Byte IS_ACTIVE;
  private final String MODERATION_STATUS;
  private final Date CURRENT_TIME;
  private final PostsResponse postsResponse;
  private final PostByIdResponse postByIdResponse;
  private final PostDataResponse postDataResponse;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


  public PostsServiceImpl() {
    IS_ACTIVE = 1;
    CURRENT_TIME = new Date();
    postsResponse = new PostsResponse();
    postByIdResponse = new PostByIdResponse();
    postDataResponse = new PostDataResponse();
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
        } else {
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


  private PostsResponse getPostsWithModeOffsetLimit(Pageable page,
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

  @Override
  public PostsResponse getPostsForModeration(int offset, int limit, String status,
      Principal principal) {

    if (principal == null) {
      return postsResponse;
    } else {
      User currentUser = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      switch (status) {
        case "new":
          Integer moderator = null;
          return getPostsByModerationStatus(offset, limit, status.toUpperCase(Locale.ROOT),
              moderator);
        case "declined":
        case "accepted":
          return getPostsByModerationStatus(offset, limit, status.toUpperCase(Locale.ROOT),
              currentUser.getId());
      }
      return postsResponse;
    }
  }

  @Override
  public PostsResponse getMyPosts(int offset, int limit, String status, Principal principal) {
    if (principal == null) {
      return postsResponse;
    } else {
      User currentUser = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      byte noActive = 0;
      byte active = 1;
      String moderationStatusNew = "NEW";
      String moderationStatusDeclined = "DECLINED";
      String moderationStatusAccepted = "ACCEPTED";
      int authorId = currentUser.getId();

      switch (status) {
        case "inactive":
          return getPostsWithStatusParams(noActive, offset, limit, moderationStatusNew,
              authorId);
        case "pending":
          return getPostsWithStatusParams(active, offset, limit, moderationStatusNew,
              authorId);
        case "declined":
          return getPostsWithStatusParams(active, offset, limit, moderationStatusDeclined,
              authorId);
        case "published":
          return getPostsWithStatusParams(active, offset, limit, moderationStatusAccepted,
              authorId);
      }
      return postsResponse;
    }
  }

  @Override
  public PostDataResponse addPost(PostDataRequest dataRequest, Principal principal) {
    ErrorDto errorDto = new ErrorDto();
    if (principal == null) {
      postDataResponse.setResult(false);
    }
    if (dataRequest.getTitle().length() < 3) {
      errorDto.setTitle("Заголовок слишком короткий");
    }
    if (dataRequest.getText().length() < 50) {
      errorDto.setText("Текст публикации слишком короткий");
    }
    if (hasErrors(errorDto)) {
      postDataResponse.setResult(false);
      postDataResponse.setErrors(errorDto);
      return postDataResponse;
    }
    User currentUser = userRepository.findByEmail(principal.getName())
        .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

    Post post = new Post();
    post.setIsActive(dataRequest.getActive());
    post.setModerationStatus(ModerationStatus.NEW);
    post.setModeratorId(null);
    post.setAuthorId(currentUser.getId());

    long publishTime = dataRequest.getTimestamp();
    long currentTime = new Date().getTime();
    if (publishTime <= currentTime) {
      post.setTime(new Date(currentTime));
    } else {
      post.setTime(new Date(publishTime));
    }

    List<Tag> tags = new ArrayList<>();
    dataRequest.getTags().forEach(tagRequest -> {
      Tag tag = new Tag();
      tag.setName(tagRequest);
      tags.add(tag);
    });
    post.setTitle(dataRequest.getTitle());
    post.setText(dataRequest.getText());
    post.setViewCount(0);
    post.setTags(tags);

    postRepository.saveAndFlush(post);

    postDataResponse.setResult(true);
    postDataResponse.setErrors(null);
    return postDataResponse;
  }


  private PostsResponse getPostsByModerationStatus(int offset, int limit, String status,
      Integer moderatorId) {
    Pageable page = PageRequest.of(offset, limit);
    if (moderatorId == null) {
      int count = postRepository.getAllByIsActiveAndModerationStatusAndModeratorId(IS_ACTIVE,
          status, page).size();
      List<PostDto> posts = postRepository.getAllByIsActiveAndModerationStatusAndModeratorId(
              IS_ACTIVE, status, page)
          .stream()
          .map(DtoMapper::mapToPostDto)
          .collect(Collectors.toList());
      postsResponse.setCount(count);
      postsResponse.setPosts(posts);
      return postsResponse;
    } else {
      int count = postRepository.getAllByIsActiveAndModerationStatusAndModeratorId(IS_ACTIVE,
          status,
          moderatorId, page).size();

      List<PostDto> posts = postRepository.getAllByIsActiveAndModerationStatusAndModeratorId(
              IS_ACTIVE, status, moderatorId, page)
          .stream()
          .map(DtoMapper::mapToPostDto)
          .collect(Collectors.toList());
      postsResponse.setCount(count);
      postsResponse.setPosts(posts);
      return postsResponse;
    }
  }

  private PostsResponse getPostsWithStatusParams(byte isActive, int offset, int limit,
      String status, int authorId) {
    Pageable page = PageRequest.of(offset, limit);
    int count = postRepository.getAllByIsActiveAndModerationStatusAndAuthorIdIs(isActive, status,
        authorId, page).size();
    List<PostDto> myposts = postRepository.getAllByIsActiveAndModerationStatusAndAuthorIdIs(
            isActive, status, authorId, page)
        .stream()
        .map(DtoMapper::mapToPostDto)
        .collect(Collectors.toList());
    postsResponse.setCount(count);
    postsResponse.setPosts(myposts);
    return postsResponse;
  }

  private boolean hasErrors(ErrorDto errors) {
    return errors.getName() != null || errors.getPassword() != null || errors.getEmail() != null
        || errors.getCaptcha() != null || errors.getText() != null || errors.getTitle() != null;
  }
}
