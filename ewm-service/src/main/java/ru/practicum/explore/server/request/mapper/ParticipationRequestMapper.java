package ru.practicum.explore.server.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore.server.request.dto.ParticipationRequestDto;
import ru.practicum.explore.server.request.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    ParticipationRequestDto toDto(ParticipationRequest request);

    default ParticipationRequest toEntity(ParticipationRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return ParticipationRequest.builder()
                .event(dto.getEvent())
                .requester(dto.getRequester())
                .status(dto.getStatus())
                .created(dto.getCreated())
                .build();
    }

    List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> requests);
}