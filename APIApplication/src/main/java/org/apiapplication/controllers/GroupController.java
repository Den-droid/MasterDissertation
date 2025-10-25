package org.apiapplication.controllers;

import org.apiapplication.dto.group.AddGroupDto;
import org.apiapplication.dto.group.SetStudentsDto;
import org.apiapplication.dto.group.SetSubjectsDto;
import org.apiapplication.dto.group.UpdateGroupDto;
import org.apiapplication.services.interfaces.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    private ResponseEntity<Integer> add(@RequestBody AddGroupDto addGroupDto) {
        int id = groupService.add(addGroupDto);
        return ResponseEntity.ok(id);
    }

    @PutMapping
    private ResponseEntity<?> update(@RequestBody UpdateGroupDto updateGroupDto) {
        groupService.update(updateGroupDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    private ResponseEntity<?> delete(@RequestParam Integer id) {
        groupService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setStudents")
    private ResponseEntity<?> setStudents(@RequestBody SetStudentsDto setStudentsDto) {
        groupService.setStudents(setStudentsDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/setSubjects")
    private ResponseEntity<?> setSubjects(@RequestBody SetSubjectsDto setSubjectsDto) {
        groupService.setSubjects(setSubjectsDto);
        return ResponseEntity.ok().build();
    }
}
