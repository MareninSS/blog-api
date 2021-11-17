package com.mareninss.blogapi.service;


import static java.util.Collections.max;

import com.mareninss.blogapi.api.response.PostsResponse;
import com.mareninss.blogapi.api.response.TagResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.TagRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.TagDto;
import com.mareninss.blogapi.entity.ModerationStatusEnum;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.entity.Tag;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {

  @Autowired
  private TagRepository tagRepository;
  @Autowired
  private PostRepository postRepository;

  private final TagResponse tagResponse;

  private final Byte IS_ACTIVE;
  private final ModerationStatusEnum MODERATION_STATUS;
  private final Date CURRENT_TIME;


  public TagServiceImpl() {
    IS_ACTIVE = 1;
    CURRENT_TIME = new Date();
    MODERATION_STATUS = ModerationStatusEnum.ACCEPTED;
    tagResponse = new TagResponse();
  }

  @Override
  public TagResponse getTags(String query) {
    int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus(IS_ACTIVE,
            CURRENT_TIME, MODERATION_STATUS)
        .size();
    if (query == null) {
      List<Tag> tagList = tagRepository.findAll();

      List<Integer> sortMax = new ArrayList<>();
      tagList.forEach(tag -> {
        int tagsCount = tag.getPosts().size();
        sortMax.add(tagsCount);
      });

      int maxPopularTag = Collections.max(sortMax);//самый популярный тэг
      double dWeightMax =
          (double) maxPopularTag / (double) count;//ненормализованный вес самым популярным тэгом
      double k = 1 / dWeightMax;//коэфф для нормализации

      List<TagDto> tagDtos = tagList.stream().map(tag -> {
        int frequency = tag.getPosts().size();// количество постов с тэгом
        double dWeight = (double) frequency / (double) count;//ненормализованный вес
        double weight = (dWeight * k);
        return DtoMapper.mapToTagDto(tag, weight);
      }).collect(Collectors.toList());
      tagResponse.setTags(tagDtos);
    }
    return tagResponse;
  }
}
