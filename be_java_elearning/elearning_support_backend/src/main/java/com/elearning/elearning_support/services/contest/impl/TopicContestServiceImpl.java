package com.elearning.elearning_support.services.contest.impl;

import com.elearning.elearning_support.dtos.contest.TopicContestDto;
import com.elearning.elearning_support.entities.contest.TopicContest;
import com.elearning.elearning_support.repositories.contest.TopicContestRepository;
import com.elearning.elearning_support.services.contest.TopicContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicContestServiceImpl implements TopicContestService {

    private final TopicContestRepository topicContestRepository;

    @Override
    public TopicContestDto createTopicContest(TopicContestDto requestDto) {
        TopicContest topicContest = new TopicContest();
        topicContest.setName(requestDto.getName());
        
        TopicContest savedTopicContest = topicContestRepository.save(topicContest);
        return convertToDto(savedTopicContest);
    }

    @Override
    public TopicContestDto updateTopicContest(Long id, TopicContestDto requestDto) {
        TopicContest topicContest = topicContestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicContest not found with id: " + id));
        
        topicContest.setName(requestDto.getName());
        
        TopicContest updatedTopicContest = topicContestRepository.save(topicContest);
        return convertToDto(updatedTopicContest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopicContestDto> getAllTopicContests() {
        return topicContestRepository.findAllWithProblemCount()
                .stream()
                .map(p -> new TopicContestDto(
                        p.getId(),
                        p.getName(),
                        p.getNumsOfProblems() == null ? 0 : p.getNumsOfProblems().intValue()
                ))
                .collect(Collectors.toList());    }

    @Override
    @Transactional(readOnly = true)
    public TopicContestDto getTopicContestById(Long id) {
        TopicContest topicContest = topicContestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TopicContest not found with id: " + id));
        return convertToDto(topicContest);
    }

    @Override
    public void deleteTopicContest(Long id) {
        if (!topicContestRepository.existsById(id)) {
            throw new RuntimeException("TopicContest not found with id: " + id);
        }
        topicContestRepository.deleteById(id);
    }

    private TopicContestDto convertToDto(TopicContest topicContest) {
        TopicContestDto dto = new TopicContestDto();
        dto.setId(topicContest.getId());
        dto.setName(topicContest.getName());
        return dto;
    }
}
