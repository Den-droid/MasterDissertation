package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.subject.AddSubjectDto;
import org.apiapplication.dto.subject.SubjectDto;
import org.apiapplication.dto.subject.UpdateSubjectDto;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
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

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectDto> getById(@PathVariable String subjectId) {
        int subjectIdInt;
        try {
            subjectIdInt = Integer.parseInt(subjectId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.SUBJECT, subjectId);
        }
        SubjectDto subjectDto = subjectService.getSubjectById(subjectIdInt);
        return ResponseEntity.ok().body(subjectDto);
    }

    @GetMapping
    public ResponseEntity<List<SubjectDto>> get(@RequestParam(required = false)
                                                Integer universityId) {
        List<SubjectDto> subjectDtoList = subjectService.get(universityId);
        return ResponseEntity.ok().body(subjectDtoList);
    }

    @PostMapping
    public ResponseEntity<IdDto> add(@RequestBody AddSubjectDto addSubjectDto) {
        IdDto idDto = subjectService.add(addSubjectDto);
        return ResponseEntity.ok(idDto);
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<?> update(@PathVariable String subjectId,
                                    @RequestBody UpdateSubjectDto updateSubjectDto) {
        int subjectIdInt;
        try {
            subjectIdInt = Integer.parseInt(subjectId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.SUBJECT, subjectId);
        }
        subjectService.update(subjectIdInt, updateSubjectDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{subjectId}")
    public ResponseEntity<?> delete(@PathVariable String subjectId) {
        int subjectIdInt;
        try {
            subjectIdInt = Integer.parseInt(subjectId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.SUBJECT, subjectId);
        }
        subjectService.delete(subjectIdInt);
        return ResponseEntity.ok().build();
    }
}
