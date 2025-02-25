package ru.trushkov.crack_manager.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.trushkov.crack_manager.model.CrackPasswordDto;
import ru.trushkov.crack_manager.service.ManagerService;

@Controller
@AllArgsConstructor
@RequestMapping("api/hash")
public class ManagerController {
    private final ManagerService managerService;

    @PostMapping("/crack")
    public ResponseEntity<String> crackPassword(CrackPasswordDto crackPasswordDto) {
        return ResponseEntity.ok(managerService.)
    }

}
