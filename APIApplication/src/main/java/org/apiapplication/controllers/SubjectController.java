package org.apiapplication.controllers;

import org.apiapplication.dto.subject.AddSubjectDto;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.dto.subject.UpdateSubjectDto;
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

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AddSubjectDto addSubjectDto) {
        subjectService.add(addSubjectDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UpdateSubjectDto updateSubjectDto) {
        subjectService.update(updateSubjectDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam int subjectId) {
        subjectService.delete(subjectId);
        return ResponseEntity.ok().build();
    }
}
