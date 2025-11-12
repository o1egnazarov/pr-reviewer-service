package ru.noleg.prreviewerservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noleg.prreviewerservice.entity.UserEntity;
import ru.noleg.prreviewerservice.exception.DomainException;
import ru.noleg.prreviewerservice.exception.ErrorCode;
import ru.noleg.prreviewerservice.repository.UserRepository;


@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity setActive(String userId, boolean isActive) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new DomainException(ErrorCode.NOT_FOUND, "User with id: " + userId + " not found")
        );

        userEntity.setActive(isActive);
        return userRepository.save(userEntity);
    }
}
