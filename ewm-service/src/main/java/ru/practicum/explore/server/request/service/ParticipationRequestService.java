package ru.practicum.explore.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.server.event.enums.EventState;
import ru.practicum.explore.server.event.model.Event;
import ru.practicum.explore.server.event.repository.EventRepository;
import ru.practicum.explore.server.exception.ConflictException;
import ru.practicum.explore.server.exception.NotFoundException;
import ru.practicum.explore.server.exception.ValidationException;
import ru.practicum.explore.server.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.server.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.server.request.dto.ParticipationRequestDto;
import ru.practicum.explore.server.request.enums.RequestStatus;
import ru.practicum.explore.server.request.mapper.ParticipationRequestMapper;
import ru.practicum.explore.server.request.model.ParticipationRequest;
import ru.practicum.explore.server.request.repository.ParticipationRequestRepository;
import ru.practicum.explore.server.users.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestService {

    private final UserService userService;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventRepository eventRepository;

    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено."));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя подать заявку на участие в неопубликованном событии.");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может подать заявку на участие в своём событии.");
        }

        if (participationRequestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ConflictException("Запрос на участие уже существует.");
        }

        long confirmedRequests = participationRequestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнуто максимальное количество участников для события c id: " + eventId + ".");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event.getId())
                .requester(userId)
                .status(Boolean.TRUE.equals(event.getRequestModeration()) ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();

        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        ParticipationRequest savedRequest = participationRequestRepository.save(request);

        return participationRequestMapper.toDto(savedRequest);
    }


    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Только инициатор может управлять заявками на участие.");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findAllById(request.getRequestIds());
        if (requests.isEmpty()) {
            throw new NotFoundException("Заявки не найдены.");
        }

        for (ParticipationRequest participationRequest : requests) {
            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Изменять статус можно только у заявок в ожидании.");
            }
        }

        long confirmedRequests = participationRequestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит заявок на участие.");
        }

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest participationRequest : requests) {
            if (request.getStatus() == RequestStatus.CONFIRMED) {
                if (confirmedRequests < event.getParticipantLimit() || event.getParticipantLimit() == 0) {
                    participationRequest.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(participationRequest);
                    confirmedRequests++;
                } else {
                    participationRequest.setStatus(RequestStatus.REJECTED);
                    rejected.add(participationRequest);
                }
            } else {
                participationRequest.setStatus(RequestStatus.REJECTED);
                rejected.add(participationRequest);
            }
        }

        participationRequestRepository.saveAll(requests);

        return new EventRequestStatusUpdateResult(
                participationRequestMapper.toDtoList(confirmed),
                participationRequestMapper.toDtoList(rejected)
        );
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        List<ParticipationRequest> requests = participationRequestRepository.findByRequester(userId);
        return requests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие с id=" + requestId + " не найден."));

        participationRequest.setStatus(RequestStatus.CANCELED);

        participationRequestRepository.save(participationRequest);

        return participationRequestMapper.toDto(participationRequest);
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено."));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только инициатор события может просматривать заявки на участие.");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findByEvent(eventId);
        return participationRequestMapper.toDtoList(requests);
    }

}