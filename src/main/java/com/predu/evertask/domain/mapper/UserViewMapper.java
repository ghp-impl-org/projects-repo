package com.predu.evertask.domain.mapper;

import com.predu.evertask.domain.dto.user.UserDto;
import com.predu.evertask.domain.dto.user.UserForInvitationDto;
import com.predu.evertask.domain.dto.user.UserIssueDto;
import com.predu.evertask.domain.model.User;
import com.predu.evertask.repository.UserRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UUIDMapper.class, ImageMapper.class})
public abstract class UserViewMapper {

    @Autowired
    private UserRepository userRepository;

    public abstract UserDto toUserDto(User user);

    public abstract UserForInvitationDto toUserForInvitationDto(User user);

    public abstract UserIssueDto toUserIssueDto(User user);

    public abstract List<UserDto> toUserDto(List<User> users);

    public UserDto toUserDtoById(UUID id) {
        if (id == null) {
            return null;
        }

        return toUserDto(userRepository.getById(id));
    }
}
