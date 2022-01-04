package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.api.response.StatisticsResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.UserRepository;
import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.PostVote;
import com.mareninss.blogapi.entity.User;
import com.mareninss.blogapi.service.StatisticsService;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsServiceImpl implements StatisticsService {

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  @Transactional
  public StatisticsResponse getStatistics(Principal principal) {
    if (principal != null) {
      User currentUser = userRepository.findByEmail(
              principal.getName())
          .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
      int idCurrentUser = currentUser.getId();
      final byte ACTIVE = 1;
      List<Post> posts = postRepository.getAllByIsActiveAndModerationStatusAndAuthorIdIs(ACTIVE,
          ModerationStatus.ACCEPTED.toString(), idCurrentUser);
      return countStatistics(posts);
    }
    return new StatisticsResponse();
  }

  @Override
  public StatisticsResponse getAllStatistics(Principal principal) {
    final byte ACTIVE = 1;
    List<Post> posts = postRepository.getAllByIsActiveAndModerationStatus(ACTIVE,
        ModerationStatus.ACCEPTED);
    return countStatistics(posts);
  }

  private StatisticsResponse countStatistics(List<Post> posts) {
    StatisticsResponse response = new StatisticsResponse();
    if (posts.size() > 0) {
      int postsCount = posts.size();
      int viewCount = posts.stream().mapToInt(Post::getViewCount).sum();

      long time =
          Objects.requireNonNull(posts.stream().min(Comparator.comparing(Post::getTime))
                  .orElse(null)).getTime()
              .getTime()
              / 1000;
      int likesCount = 0;
      int disLikesCount = 0;
      for (Post post : posts) {
        List<PostVote> votes = post.getPostVotes();
        int tempLikesCount = (int) votes.stream().filter(postVote -> postVote.getValue() == 1)
            .count();
        int tempDislikesCount = (int) votes.stream().filter(postVote -> postVote.getValue() == 0)
            .count();
        likesCount = likesCount + tempLikesCount;
        disLikesCount = disLikesCount + tempDislikesCount;
      }

      response.setViewsCount(viewCount);
      response.setPostsCount(postsCount);
      response.setDislikesCount(disLikesCount);
      response.setLikesCount(likesCount);
      response.setFirstPublication(time);
      return response;
    }
    response.setFirstPublication(0);
    response.setDislikesCount(0);
    response.setViewsCount(0);
    response.setPostsCount(0);
    response.setLikesCount(0);
    return response;
  }
}
