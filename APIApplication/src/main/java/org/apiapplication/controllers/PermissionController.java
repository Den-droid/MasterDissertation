package org.apiapplication.controllers;

import org.apiapplication.dto.permission.PermissionDto;
import org.apiapplication.services.interfaces.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
@CrossOrigin
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/givePermission")
    public ResponseEntity<?> givePermission(@RequestBody PermissionDto permissionDto) {
        permissionService.givePermission(permissionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removePermission")
    public ResponseEntity<?> removePermission(@RequestBody PermissionDto permissionDto) {
        permissionService.removePermission(permissionDto);
        return ResponseEntity.ok().build();
    }
}
