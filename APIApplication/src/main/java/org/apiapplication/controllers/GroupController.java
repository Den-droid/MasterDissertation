package org.apiapplication.controllers;

import org.apiapplication.constants.EntityName;
import org.apiapplication.dto.common.IdDto;
import org.apiapplication.dto.group.*;
import org.apiapplication.exceptions.entity.EntityWithIdNotFoundException;
import org.apiapplication.services.interfaces.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getById(@PathVariable String groupId) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        GroupDto groupDto = groupService.getById(groupIdInt);
        return ResponseEntity.ok(groupDto);
    }

    @GetMapping
    public ResponseEntity<List<GroupDto>> get() {
        List<GroupDto> groupDtos = groupService.get();
        return ResponseEntity.ok(groupDtos);
    }

    @PostMapping
    private ResponseEntity<IdDto> add(@RequestBody AddGroupDto addGroupDto) {
        IdDto idDto = groupService.add(addGroupDto);
        return ResponseEntity.ok(idDto);
    }

    @PutMapping("/{groupId}")
    private ResponseEntity<?> update(@PathVariable String groupId,
                                     @RequestBody UpdateGroupDto updateGroupDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.update(groupIdInt, updateGroupDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    private ResponseEntity<?> delete(@PathVariable String groupId) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.delete(groupIdInt);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/setStudents")
    private ResponseEntity<?> setStudents(@PathVariable String groupId,
                                          @RequestBody SetStudentsDto setStudentsDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.setStudents(groupIdInt, setStudentsDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/addStudents")
    private ResponseEntity<?> addStudents(@PathVariable String groupId,
                                          @RequestBody SetStudentsDto setStudentsDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.addStudents(groupIdInt, setStudentsDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/removeStudents")
    private ResponseEntity<?> removeStudents(@PathVariable String groupId,
                                             @RequestBody SetStudentsDto setStudentsDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.removeStudents(groupIdInt, setStudentsDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/setSubjects")
    private ResponseEntity<?> setSubjects(@PathVariable String groupId,
                                          @RequestBody SetSubjectsDto setSubjectsDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.setSubjects(groupIdInt, setSubjectsDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/addSubjects")
    private ResponseEntity<?> addSubjects(@PathVariable String groupId,
                                          @RequestBody SetSubjectsDto setSubjectsDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.addSubjects(groupIdInt, setSubjectsDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{groupId}/removeSubjects")
    private ResponseEntity<?> removeSubjects(@PathVariable String groupId,
                                             @RequestBody SetSubjectsDto setSubjectsDto) {
        int groupIdInt;
        try {
            groupIdInt = Integer.parseInt(groupId);
        } catch (NumberFormatException e) {
            throw new EntityWithIdNotFoundException(EntityName.GROUP, groupId);
        }
        groupService.removeSubjects(groupIdInt, setSubjectsDto);
        return ResponseEntity.ok().build();
    }
}
