package org.apiapplication.controllers;

import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.services.interfaces.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin
public class SubjectController {
    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectDto>> getByUniversityId(@RequestParam int universityId) {
        List<SubjectDto> subjectDtoList = subjectService.getByUniversityId(universityId);
        return ResponseEntity.ok().body(subjectDtoList);
    }
}
