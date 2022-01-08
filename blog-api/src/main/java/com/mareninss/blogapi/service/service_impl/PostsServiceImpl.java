package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.api.request.LikeDislikeRequest;
import com.mareninss.blogapi.api.request.ModerationPostRequest;
import com.mareninss.blogapi.api.request.PostDataRequest;
import com.mareninss.blogapi.api.response.ErrorsResponse;
import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.PostVotesRepository;
import com.mareninss.blogapi.dao.SettingsRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.ErrorDto;
import com.mareninss.blogapi.entity.GlobalSetting;
import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.PostVote;
import com.mareninss.blogapi.entity.Tag;
import com.mareninss.blogapi.entity.User;
import com.mareninss.blogapi.service.PostsService;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
  private PostVotesRepository votesRepository;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private SettingsRepository settingsRepository;

  private final Byte IS_ACTIVE = 1;
  private final String MODERATION_STATUS = ModerationStatus.ACCEPTED.toString();
  private final Date CURRENT_TIME = new Date();
  private final byte LIKE = 1;
  private final byte DISLIKE = -1;

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

  @Override
  public PostsResponse getPosts(int offset, int limit, String mode) {
    Pageable page = PageRequest.of((offset == 0 || offset < 0 ? 0 : (offset / 10)), limit);
    PostsResponse postsResponse = new PostsResponse();
    Page<Post> posts;

    switch (mode) {
      case "recent":
        posts = postRepository.getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByTimeDesc(
            IS_ACTIVE, ModerationStatus.ACCEPTED, CURRENT_TIME, page);
        return getPostsWithModeOffsetLimit(posts);
      case "popular":
        posts = postRepository.getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByPostVotes(
            IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, page);
        return getPostsWithModeOffsetLimit(posts);
      case "best":
        posts = postRepository.getAllByIsActiveAndModerationStatusAndTimeLessThan(
            IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, page);
        return getPostsWithModeOffsetLimit(posts);
      case "early":
        posts = postRepository.getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByTimeAsc(
            IS_ACTIVE, ModerationStatus.ACCEPTED, CURRENT_TIME, page);
        return getPostsWithModeOffsetLimit(posts);
    }
    return postsResponse;
  }


  @Override
  public PostsResponse getPostsByQuery(int offset, int limit, String query) {
    Page<Post> posts;
    Pageable page = PageRequest.of((offset == 0 || offset < 0 ? 0 : (offset / 10)), limit);

    if (query == null || query.isBlank()) {
      posts = postRepository.getAllByIsActiveAndModerationStatusAndTimeLessThanOrderByTimeDesc(
          IS_ACTIVE, ModerationStatus.ACCEPTED, CURRENT_TIME, page);
      return getPostsWithModeOffsetLimit(posts);
    }
    posts = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatusWithLimitAndOffsetAndQueryLike(
        IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, query, page);
    return getPostsWithModeOffsetLimit(posts);
  }

  @Override
  public PostsResponse getPostsByDates(int offset, int limit, String date) throws ParseException {
    Pageable page = PageRequest.of((offset == 0 || offset < 0 ? 0 : (offset / 10)), limit);
    Date dateNow = dateFormat.parse(date);
    String validDate = dateFormat.format(dateNow);

    Page<Post> posts = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
        IS_ACTIVE, CURRENT_TIME, validDate, MODERATION_STATUS, page);
    return getPostsWithModeOffsetLimit(posts);
  }

  @Override
  public PostsResponse getPostsByTag(int offset, int limit, String tag) {
    Pageable page = PageRequest.of((offset == 0 || offset < 0 ? 0 : (offset / 10)), limit);

    Page<Post> posts = postRepository
        .findPostsByTagNameAndIsActiveAndTimeIsLessThanAndModerationStatus(IS_ACTIVE, CURRENT_TIME,
            MODERATION_STATUS, tag, page);
    return getPostsWithModeOffsetLimit(posts);
  }

  @Override
  @Transactional
  public PostByIdResponse getPostById(int id, Principal principal) {
    PostByIdResponse postByIdResponse = new PostByIdResponse();

    final int LIKE = 1;
    final int DISLIKE = -1;
    final int MODERATOR = 1;

    Optional<Post> postById = postRepository.findById(id);
    if (postById.isPresent()) {
      postByIdResponse.setId(postById.get().getId());
      postByIdResponse.setTimestamp(postById.get().getTime().getTime() / 1000);
      postByIdResponse.setActive(postById.get().getIsActive() == 1);
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
          viewCount++;
          postByIdResponse.setViewCount(viewCount);//получаем
          postRepository.updateViewCountById(id);//обновляем + 1
        }
      } else {
        viewCount++;
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

  private PostsResponse getPostsWithModeOffsetLimit(Page<Post> posts) {
    PostsResponse postsResponse = new PostsResponse();

    postsResponse.setCount((int) posts.getTotalElements());
    postsResponse.setPosts(posts
        .stream()
        .map(DtoMapper::mapToPostDto)
        .collect(Collectors.toList()));
    return postsResponse;
  }

  @Override
  public PostsResponse getPostsForModeration(int offset, int limit, String status,
      Principal principal) {
    PostsResponse postsResponse = new PostsResponse();

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
    PostsResponse postsResponse = new PostsResponse();

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
  @Transactional
  public ErrorsResponse addPost(PostDataRequest dataRequest, Principal principal) {
    ErrorsResponse postDataResponse = new ErrorsResponse();

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
    return savePost(dataRequest, principal);
  }

  @Override
  @Transactional
  public ErrorsResponse updatePost(int id, PostDataRequest dataRequest, Principal principal) {
    Optional<Post> post = postRepository.findById(id);
    if (post.isPresent()) {
      return savePostById(dataRequest, principal, post.get());
    }
    return null;
  }

  private PostsResponse getPostsByModerationStatus(int offset, int limit, String status,
      Integer moderatorId) {
    Page<Post> posts;
    Pageable page = PageRequest.of((offset == 0 || offset < 0 ? 0 : (offset / 10)), limit);
    if (moderatorId == null) {
      posts = postRepository.getAllByIsActiveAndModerationStatusAndModeratorId(
          IS_ACTIVE, status, page);
      return getPostsWithModeOffsetLimit(posts);
    }
    posts = postRepository.getAllByIsActiveAndModerationStatusAndModeratorId(
        IS_ACTIVE, status, moderatorId, page);
    return getPostsWithModeOffsetLimit(posts);
  }

  private PostsResponse getPostsWithStatusParams(byte isActive, int offset, int limit,
      String status, int authorId) {
    Pageable page = PageRequest.of((offset == 0 || offset < 0 ? 0 : (offset / 10)), limit);
    Page<Post> posts = postRepository.getAllByIsActiveAndModerationStatusAndAuthorIdIs(
        isActive, status, authorId, page);
    return getPostsWithModeOffsetLimit(posts);
  }

  private boolean hasErrors(ErrorDto errors) {
    return errors.getName() != null || errors.getPassword() != null || errors.getEmail() != null
        || errors.getCaptcha() != null || errors.getText() != null || errors.getTitle() != null;
  }


  private ErrorsResponse savePost(PostDataRequest dataRequest, Principal principal) {
    ErrorsResponse postDataResponse = new ErrorsResponse();

    if (principal == null) {
      postDataResponse.setResult(false);
    } else {
      User currentUser = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      Post post = new Post();
      post.setIsActive(dataRequest.getActive());

      boolean active = dataRequest.getActive() == 1;
      List<GlobalSetting> globalSetting = settingsRepository.getAllBy();
      int PostPreModerationMode = 1;
      boolean isPostPreModeration = globalSetting.get(PostPreModerationMode).getValue()
          .equals("yes");
      if (active && !isPostPreModeration) {
        post.setModerationStatus(ModerationStatus.ACCEPTED);
      } else {
        post.setModerationStatus(ModerationStatus.NEW);
      }

      post.setModeratorId(null);
      post.setAuthorId(currentUser.getId());

      long publishTime = dataRequest.getTimestamp();
      long currentTime = new Date().getTime();

      if (publishTime <= currentTime) {
        post.setTime(new Date(currentTime));
      }
      post.setTime(new Date(publishTime * 1000));

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
    }
    return postDataResponse;
  }


  private ErrorsResponse savePostById(PostDataRequest dataRequest, Principal principal,
      Post postById) {
    ErrorsResponse postDataResponse = new ErrorsResponse();

    byte moderator = 1;
    if (principal == null) {
      postDataResponse.setResult(false);
    } else {
      User currentUser = userRepository.findByEmail(principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

      postById.setIsActive(dataRequest.getActive());

      if (currentUser.getIsModerator() != moderator) {
        postById.setModerationStatus(ModerationStatus.NEW);
      } else {
        postById.setModerationStatus(postById.getModerationStatus());
      }
      postById.setModeratorId(null);
      postById.setAuthorId(currentUser.getId());

      long publishedTime = dataRequest.getTimestamp();
      long currentTime = new Date().getTime();
      if (publishedTime <= currentTime) {
        postById.setTime(new Date(currentTime));
      }
      postById.setTime(new Date(publishedTime * 1000));

      List<Tag> tags = new ArrayList<>();
      dataRequest.getTags().forEach(tagRequest -> {
        Tag tag = new Tag();
        tag.setName(tagRequest);
        tags.add(tag);
      });
      postById.setTitle(dataRequest.getTitle());
      postById.setText(dataRequest.getText());
      postById.setViewCount(0);
      postById.setTags(tags);

      postRepository.saveAndFlush(postById);

      postDataResponse.setResult(true);
      postDataResponse.setErrors(null);
    }
    return postDataResponse;
  }

  @Override
  @Transactional
  public Map<String, Boolean> moderatePost(ModerationPostRequest request, Principal principal) {
    Map<String, Boolean> result = new HashMap<>();
    if (principal != null && request != null) {
      com.mareninss.blogapi.entity.User currentUser = userRepository.findByEmail(
              principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      Optional<Post> post = postRepository.findById(request.getPostId());

      if (post.isPresent()) {
        switch (request.getDecision()) {
          case "accept":
            post.get().setModerationStatus(ModerationStatus.ACCEPTED);
            post.get().setModeratorId(currentUser.getId());
            break;
          case "decline":
            post.get().setModerationStatus(ModerationStatus.DECLINED);
            post.get().setModeratorId(currentUser.getId());
            break;
        }
        postRepository.saveAndFlush(post.get());
        result.put("result", true);
      } else {
        result.put("result", false);
      }
    }
    return result;
  }

  @Override
  public Map<String, Boolean> likePost(LikeDislikeRequest id, Principal principal) {
    return getVoteByValue(id, principal, LIKE, DISLIKE);
  }

  @Override
  public Map<String, Boolean> dislikePost(LikeDislikeRequest id, Principal principal) {
    return getVoteByValue(id, principal, DISLIKE, LIKE);
  }

  private Map<String, Boolean> getVoteByValue(LikeDislikeRequest id, Principal principal,
      byte vote, byte reverseVote) {
    Map<String, Boolean> result = new HashMap<>();
    if (principal != null) {
      User currentUser = userRepository.findByEmail(
              principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      Optional<Post> post = postRepository.findById(id.getPostId());
      if (post.isEmpty()) {
        result.put("result", false);
        return result;
      }
      PostVote postVote = votesRepository.getByPostId(id.getPostId()).orElse(new PostVote());
      if (Objects.equals(currentUser.getId(), postVote.getUserId())
          && postVote.getValue() == vote) {
        result.put("result", false);
        return result;
      }

      if (Objects.equals(currentUser.getId(), postVote.getUserId())
          && postVote.getValue() == reverseVote) {
        postVote.setValue(vote);
      }
      postVote.setUserId(currentUser.getId());
      postVote.setPostId(id.getPostId());
      postVote.setTime(new Date());
      postVote.setValue(vote);

      votesRepository.saveAndFlush(postVote);

      result.put("result", true);
      return result;
    }
    result.put("result", false);
    return result;
  }
}
