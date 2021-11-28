package com.mareninss.blogapi.service;


import com.mareninss.blogapi.api.response.TagResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.dao.TagRepository;
import com.mareninss.blogapi.dto.DtoMapper;
import com.mareninss.blogapi.dto.TagDto;

import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Tag;
import java.util.ArrayList;
import java.util.Collections;
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
  private final String MODERATION_STATUS;
  private final Date CURRENT_TIME;


  public TagServiceImpl() {
    IS_ACTIVE = 1;
    CURRENT_TIME = new Date();
    MODERATION_STATUS = ModerationStatus.ACCEPTED.toString();
    tagResponse = new TagResponse();
  }

  @Override
  public TagResponse getTags(String query) {
    if (query == null || query.isEmpty()) {
      List<Tag> tagList = tagRepository.findAll();
      tagResponse.setTags(calculatingOfNormalizedWeight(tagList));
    } else {
      List<Tag> tagListWithQuery = tagRepository.findAllByNameIsContaining(query);
      tagResponse.setTags(calculatingOfNormalizedWeight(tagListWithQuery));
    }
    return tagResponse;
  }

  public List<TagDto> calculatingOfNormalizedWeight(List<Tag> tagList) {
    int count = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
            IS_ACTIVE,
            CURRENT_TIME, MODERATION_STATUS)
        .size();
    List<Integer> sortMax = new ArrayList<>();
    tagList.forEach(tag -> {
      int tagsCount = tag.getPosts().size();
      sortMax.add(tagsCount);
    });

    int maxPopularTag = Collections.max(sortMax);//самый популярный тэг
    double dWeightMax =
        (double) maxPopularTag / (double) count;//ненормализованный вес самого популярного тэга
    double k = 1 / dWeightMax;//коэфф для нормализации

    return tagList.stream().map(tag -> {
      int frequency = tag.getPosts().size();// количество постов с тэгом
      double dWeight = (double) frequency / (double) count;//ненормализованный вес
      double weight = (dWeight * k);
      return DtoMapper.mapToTagDto(tag, weight);
    }).collect(Collectors.toList());
  }
}


