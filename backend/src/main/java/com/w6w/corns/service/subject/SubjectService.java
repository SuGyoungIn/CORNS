package com.w6w.corns.service.subject;

import com.w6w.corns.dto.subject.SubjectResponseDto;

import java.util.List;

public interface SubjectService {

    List<SubjectResponseDto> findAll();

    SubjectResponseDto findById(int subjectNo);

}
