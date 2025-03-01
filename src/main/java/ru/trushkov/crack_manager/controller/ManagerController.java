package ru.trushkov.crack_manager.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trushkov.crack_manager.model.CrackPasswordDto;
import ru.trushkov.crack_manager.model.PasswordDto;
import ru.trushkov.crack_manager.service.ManagerService;

@RestController
@AllArgsConstructor
@RequestMapping("/api/hash")
public class ManagerController {
    private final ManagerService managerService;

    @PostMapping("/crack")
    public ResponseEntity<String> crackPassword(@RequestBody CrackPasswordDto crackPasswordDto) {
        return ResponseEntity.ok(managerService.crackPassword(crackPasswordDto));
    }

    @GetMapping("status")
    public ResponseEntity<PasswordDto> getPasswords(@RequestParam("requestId") String requestId) {
        return ResponseEntity.ok(managerService.getPasswords(requestId));
    }

}
