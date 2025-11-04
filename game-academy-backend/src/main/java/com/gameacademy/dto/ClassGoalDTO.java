package com.gameacademy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassGoalDTO {

    private String goalDescription;
    private Integer targetPoints;
    private Integer currentProgress;
    private LocalDateTime deadline;
}
