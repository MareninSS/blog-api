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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
  public CalendarCountPostResponse getNumberOfPostByYear(Integer year) {
    CalendarCountPostResponse calendarCountPostResponse = new CalendarCountPostResponse();
    Set<Integer> currentYear = new TreeSet<>();
    if (year == null) {
      currentYear.add(LocalDate.now().getYear());

      calendarCountPostResponse.setYears(currentYear);

      List<String> date = new ArrayList<>();
      Map<String, Integer> postCountList = new TreeMap<>(Comparator.reverseOrder());
      SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");

      List<Post> postList = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_AcceptedWhereYearsEqual(
          IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS, currentYear);
      postList.forEach(post -> {
        date.add(formatDate.format(post.getTime()));
      });
      date.forEach(d -> {
        int count = Collections.frequency(date, d);
        postCountList.putIfAbsent(d, count);
      });
      calendarCountPostResponse.setPosts(postCountList);
      return calendarCountPostResponse;
    }
    List<String> date = new ArrayList<>();
    Set<Integer> yearsList = new TreeSet<>();
    Map<String, Integer> postCountList = new TreeMap<>(Comparator.reverseOrder());
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatYears = new SimpleDateFormat("yyyy");

    List<Post> postList = postRepository.getAllByIsActiveAndTimeIsLessThanAndModerationStatus_Accepted(
        IS_ACTIVE, CURRENT_TIME, MODERATION_STATUS);
    postList.forEach(post -> {
      date.add(formatDate.format(post.getTime()));
      yearsList.add(Integer.valueOf(formatYears.format(post.getTime())));
    });
    date.forEach(d -> {
      int count = Collections.frequency(date, d);
      postCountList.putIfAbsent(d, count);
    });

    calendarCountPostResponse.setYears(yearsList);
    calendarCountPostResponse.setPosts(postCountList);
    return calendarCountPostResponse;
  }

}
