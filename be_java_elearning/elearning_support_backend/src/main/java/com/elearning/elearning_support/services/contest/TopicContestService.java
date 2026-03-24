package com.elearning.elearning_support.services.contest;

import com.elearning.elearning_support.dtos.contest.TopicContestDto;

import java.util.List;

public interface TopicContestService {
    
    /**
     * Tạo mới topic contest
     */
    TopicContestDto createTopicContest(TopicContestDto requestDto);
    
    /**
     * Cập nhật topic contest
     */
    TopicContestDto updateTopicContest(Long id, TopicContestDto requestDto);
    
    /**
     * Lấy danh sách tất cả topic contest
     */
    List<TopicContestDto> getAllTopicContests();
    
    /**
     * Lấy chi tiết topic contest theo ID
     */
    TopicContestDto getTopicContestById(Long id);
    
    /**
     * Xóa topic contest
     */
    void deleteTopicContest(Long id);
}
