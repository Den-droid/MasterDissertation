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
    public ResponseEntity<List<SubjectDto>> get(@RequestParam(required = false)
                                                Integer universityId) {
        List<SubjectDto> subjectDtoList = subjectService.get(universityId);
        return ResponseEntity.ok().body(subjectDtoList);
    }
}
