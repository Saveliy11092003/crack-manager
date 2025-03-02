package ru.trushkov.crack_manager.model;

import lombok.*;
import ru.trushkov.crack_manager.model.enumeration.Status;

import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {

    private CopyOnWriteArrayList<String> data;
    private Status status;
    private Integer successWork;

}
