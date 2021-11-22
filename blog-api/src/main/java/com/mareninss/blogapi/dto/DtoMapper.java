package com.mareninss.blogapi.dto;

import com.mareninss.blogapi.api.response.PostByIdResponse;
import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.PostComment;
import com.mareninss.blogapi.entity.Tag;
import com.mareninss.blogapi.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;

public class DtoMapper {

  private PostsResponse postsResponse;

  public static UserDto mapToUserDto(User user) {
    return null;
  }

  public static UserPostDto mapToUserPostDto(User user) {
    UserPostDto userPostDto = new UserPostDto();
    userPostDto.setId(user.getId());
    userPostDto.setName(user.getName());
    return userPostDto;
  }

  public static PostDto mapToPostDto(Post post) {
    final int LIKE = 1;
    final int DISLIKE = 0;

    long countLike = post.getPostVotes()
        .stream()
        .filter(postVote -> postVote.getValue() == LIKE)
        .count();
    long contDislike = post.getPostVotes()
        .stream()
        .filter(postVote -> postVote.getValue() == DISLIKE)
        .count();

    long commentCount = post.getPostComments().stream()
        .filter(postComment -> postComment.getPostId().equals(post.getId()))
        .count();

    PostDto postDto = new PostDto();
    UserPostDto userPostDto = new UserPostDto();
    userPostDto.setId(post.getUserId());
    userPostDto.setName(post.getUser().getName());

    postDto.setId(post.getId());
    postDto.setTimestamp((post.getTime().getTime()) / 1000);
    postDto.setUser(userPostDto);
    postDto.setTitle(post.getTitle());
    postDto.setTitle(post.getTitle());
    postDto.setAnnounce(getAnnounce(post.getText()));
    postDto.setLikeCount((int) countLike);
    postDto.setDislikeCount((int) contDislike);
    postDto.setCommentCount((int) commentCount);
    postDto.setViewCount(post.getViewCount());
    return postDto;
  }

  public static List<CommentsDto> mapToCommentsDto(Post post) {
    List<PostComment> postComments = post.getPostComments();
    List<CommentsDto> commentsDto = new ArrayList<>();

    postComments.forEach(postComment -> {
      CommentsDto comment = new CommentsDto();

      comment.setId(postComment.getId());
      comment.setTimestamp(postComment.getTime().getTime() / 1000);
      comment.setText(postComment.getText());
      comment.setUser(mapToUserCommentsDto(postComment.getUser()));
      commentsDto.add(comment);
    });
    return commentsDto;
  }

  public static UserCommentsDto mapToUserCommentsDto(User user) {
    UserCommentsDto userCommentsDto = new UserCommentsDto();
    userCommentsDto.setId(user.getId());
    userCommentsDto.setName(user.getName());
    userCommentsDto.setPhoto(user.getPhoto());
    return userCommentsDto;
  }


  public static String getAnnounce(String text) {
    String textOut = Jsoup.parse(text).text();
    if (textOut.length() <= 150) {
      return textOut + "...";
    } else {
      return textOut.substring(0, 150) + "...";
    }
  }

  public static TagDto mapToTagDto(Tag tag, double weightTag) {
    TagDto tagDto = new TagDto();
    tagDto.setName(tag.getName());
    tagDto.setWeight(weightTag);

    return tagDto;
  }
}
