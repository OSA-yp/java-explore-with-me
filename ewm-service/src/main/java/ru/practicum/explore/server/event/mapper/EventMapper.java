package ru.practicum.explore.server.event.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.practicum.explore.server.category.dal.CategoryMapper;
import ru.practicum.explore.server.category.model.Category;
import ru.practicum.explore.server.event.dto.EventFullDto;
import ru.practicum.explore.server.event.dto.EventShortDto;
import ru.practicum.explore.server.event.dto.NewEventDto;
import ru.practicum.explore.server.event.enums.EventState;
import ru.practicum.explore.server.event.model.Event;
import ru.practicum.explore.server.users.dal.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @InheritInverseConfiguration(name = "toEventShortDto")
    EventFullDto toEventFullDto(Event event);

    List<EventFullDto> toEventFullDtoList(List<Event> events);

    EventShortDto toEventShortDto(Event event);

    default Event toEntity(NewEventDto dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setAnnotation(dto.getAnnotation());
        event.setCategory(mapCategory(dto.getCategory()));
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());
        event.setState(EventState.PENDING);

        return event;
    }

    @Named("mapCategory")
    default Category mapCategory(Long id) {
        return id == null ? null : Category.builder().id(id).build();
    }
}