package ru.practicum.explore.server.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.explore.server.request.dto.ParticipationRequestDto;
import ru.practicum.explore.server.request.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {


    ParticipationRequestMapper INSTANCE = Mappers.getMapper(ParticipationRequestMapper.class);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "event", source = "dto.event")
    @Mapping(target = "requester", source = "dto.requester")
    @Mapping(target = "status", source = "dto.status")
    @Mapping(target = "created", source = "dto.created")
    ParticipationRequest toEntity(ParticipationRequestDto dto);

    ParticipationRequestDto toDto(ParticipationRequest request);

    List<ParticipationRequestDto> toDtoList(List<ParticipationRequest> requests);
}