package org.apiapplication.controllers;

import org.apiapplication.dto.permission.PermissionDto;
import org.apiapplication.dto.permission.UpdatePermissionDto;
import org.apiapplication.services.interfaces.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<List<PermissionDto>> get(@RequestParam(required = false) Integer userId) {
        List<PermissionDto> permissionDtos = permissionService.get(userId);
        return ResponseEntity.ok(permissionDtos);
    }

    @PutMapping
    public ResponseEntity<?> updatePermission(@RequestBody UpdatePermissionDto permissionDto) {
        permissionService.updatePermissions(permissionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<?> removePermissions(@RequestParam UpdatePermissionDto permissionDto) {
        permissionService.removePermissions(permissionDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> givePermissions(@RequestBody UpdatePermissionDto permissionDto) {
        permissionService.givePermissions(permissionDto);
        return ResponseEntity.ok().build();
    }
}
