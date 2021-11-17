package com.mareninss.blogapi.dto;

import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.Tag;
import com.mareninss.blogapi.entity.User;

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
    postDto.setTimestamp(post.getTime().getTime());
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


  public static String getAnnounce(String text) {
    if (text.length() <= 150) {
      return text + "...";
    } else {
      return text.substring(0, 150) + "...";
    }
  }

  public static TagDto mapToTagDto(Tag tag, double weightTag) {
    TagDto tagDto = new TagDto();
    tagDto.setName(tag.getName());
    tagDto.setWeight(weightTag);

    return tagDto;
  }
}
