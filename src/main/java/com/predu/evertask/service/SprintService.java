package com.predu.evertask.service;

import com.predu.evertask.domain.dto.sprint.SprintDto;
import com.predu.evertask.domain.dto.sprint.SprintSaveDto;
import com.predu.evertask.domain.mapper.SprintMapper;
import com.predu.evertask.domain.model.Sprint;
import com.predu.evertask.repository.SprintRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private final SprintRepository sprintRepository;
    private final SprintMapper sprintMapper;

    public SprintService(SprintRepository sprintRepository, SprintMapper sprintMapper) {
        this.sprintRepository = sprintRepository;
        this.sprintMapper = sprintMapper;
    }

    public List<SprintDto> findAll() {
        return sprintRepository.findAll()
                .stream()
                .map(sprintMapper::sprintToSprintDto)
                .collect(Collectors.toList());
    }

    public Optional<Sprint> findById(UUID id) {
        return sprintRepository.findById(id);
    }

    public Sprint save(SprintSaveDto toSave) {
        return sprintRepository.save(sprintMapper.sprintSaveDtoToSprint(toSave));
    }

    public boolean existsById(UUID id) {
        return sprintRepository.existsById(id);
    }

    public void delete(Sprint toDelete) {
        sprintRepository.delete(toDelete);
    }

    public void deleteById(UUID id) {
        sprintRepository.deleteById(id);
    }
}