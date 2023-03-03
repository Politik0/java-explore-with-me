package ru.practicum.ewmservice.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
  private String created;

  private long event;

  private long id;

  private long requester;

  private String status;
}
