package com.mareninss.blogapi.service.service_impl;

import com.mareninss.blogapi.api.response.CalendarCountPostResponse;
import com.mareninss.blogapi.dao.PostRepository;
import com.mareninss.blogapi.entity.ModerationStatus;
import com.mareninss.blogapi.entity.Post;
import com.mareninss.blogapi.service.CalendarService;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalendarServiceImpl implements CalendarService {

  @Autowired
  private PostRepository postRepository;

  private final Byte IS_ACTIVE = 1;
  private final String MODERATION_STATUS = ModerationStatus.ACCEPTED.toString();
  private final Date CURRENT_TIME = new Date();

  @Override
  public CalendarCountPostResponse getNumberOfPostByYear(List<Integer> years) {
    CalendarCountPostResponse calendarCountPostResponse = new CalendarCountPostResponse();
    List<Integer> currentYear = new ArrayList<>();
    if (years == null) {
      currentYear.add(LocalDate.now().getYear());
      calendarCountPostResponse.setYears(currentYear);
      calendarCountPostResponse.setPosts(getPostsByYearsQuery(currentYear));
      return calendarCountPostResponse;
    } else {
      calendarCountPostResponse.setYears(years);
      calendarCountPostResponse.setPosts(getPostsByYearsQuery(years));
      return calendarCountPostResponse;
    }
  }

  private Map<String, Integer> getPostsByYearsQuery(List<Integer> years) {
    List<String> date = new ArrayList<>();
    Map<String, Integer> postCountList = new TreeMap<>(Comparator.reverseOrder());
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

    List<Post> postList = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_AcceptedWhereYearsEqual(
        IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, years);
    postList.forEach(post -> {
      date.add(formatDate.format(post.getTime()));
    });
    date.forEach(d -> {
      int count = Collections.frequency(date, d);
      postCountList.putIfAbsent(d, count);
    });
    return postCountList;
  }
}
