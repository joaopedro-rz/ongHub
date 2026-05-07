package com.onghub.api.service;

import com.onghub.api.dto.request.NgoRegisterRequest;
import com.onghub.api.dto.request.NgoUpdateRequest;
import com.onghub.api.entity.Ngo;
import com.onghub.api.entity.NgoCategory;
import com.onghub.api.entity.NgoStatus;
import com.onghub.api.entity.User;
import com.onghub.api.exception.ResourceNotFoundException;
import com.onghub.api.mapper.NgoMapper;
import com.onghub.api.repository.NgoCategoryRepository;
import com.onghub.api.repository.NgoRepository;
import com.onghub.api.repository.UserRepository;
import com.onghub.api.service.impl.NgoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NgoServiceTest {

    private NgoRepository ngoRepository;
    private UserRepository userRepository;
    private NgoCategoryRepository ngoCategoryRepository;
    private NgoMapper ngoMapper;

    private NgoService ngoService;

    @BeforeEach
    void setup() {
        ngoRepository = mock(NgoRepository.class);
        userRepository = mock(UserRepository.class);
        ngoCategoryRepository = mock(NgoCategoryRepository.class);
        ngoMapper = mock(NgoMapper.class);
        ngoService = new NgoServiceImpl(ngoRepository, userRepository, ngoCategoryRepository, ngoMapper);
    }

    @Test
    void registerNgoCreatesPendingNgo() {
        User manager = new User();
        manager.setId(10L);
        manager.setEmail("manager@test.com");

        when(userRepository.findByEmail("manager@test.com")).thenReturn(Optional.of(manager));
        when(ngoRepository.save(any(Ngo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ngoMapper.toResponse(any(Ngo.class))).thenReturn(null);

        // We won't use MapStruct impl in this unit test; just verify state via captured entity
        NgoRegisterRequest request = new NgoRegisterRequest(
            "ONG X",
            "123",
            "desc",
            null,
            null,
            "ong@test.com",
            null,
            null
        );

        ngoService.registerNgo("manager@test.com", request);

        verify(ngoRepository).save(argThat(ngo ->
            ngo.getName().equals("ONG X") &&
                ngo.getStatus() == NgoStatus.PENDING &&
                ngo.getManager() != null &&
                ngo.getManager().getEmail().equals("manager@test.com")
        ));
    }

    @Test
    void registerNgoCategoryNotFoundThrows() {
        User manager = new User();
        manager.setId(10L);
        manager.setEmail("manager@test.com");

        when(userRepository.findByEmail("manager@test.com")).thenReturn(Optional.of(manager));
        when(ngoCategoryRepository.findById(99L)).thenReturn(Optional.empty());

        NgoRegisterRequest request = new NgoRegisterRequest(
            "ONG X",
            null,
            null,
            null,
            null,
            null,
            99L,
            null
        );

        assertThrows(ResourceNotFoundException.class, () -> ngoService.registerNgo("manager@test.com", request));
    }

    @Test
    void updateStatusUpdatesNgo() {
        Ngo ngo = new Ngo();
        ngo.setId(1L);
        ngo.setStatus(NgoStatus.PENDING);

        when(ngoRepository.findById(1L)).thenReturn(Optional.of(ngo));
        when(ngoRepository.save(any(Ngo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ngoMapper.toResponse(any(Ngo.class))).thenReturn(null);

        ngoService.updateStatus(1L, NgoStatus.ACTIVE);

        assertEquals(NgoStatus.ACTIVE, ngo.getStatus());
        verify(ngoRepository).save(ngo);
    }
}
