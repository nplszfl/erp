package com.omnitrade.erp.social.service;

import com.omnitrade.erp.social.dto.CooperationDTO;
import com.omnitrade.erp.social.model.Cooperation;
import com.omnitrade.erp.social.model.Influencer;
import com.omnitrade.erp.social.repository.CooperationRepository;
import com.omnitrade.erp.social.repository.InfluencerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("达人合作服务测试")
class CooperationServiceTest {

    @Mock
    private CooperationRepository repository;

    @Mock
    private InfluencerRepository influencerRepository;

    @InjectMocks
    private CooperationService service;

    private Cooperation testCooperation;
    private CooperationDTO testDTO;
    private Influencer testInfluencer;

    @BeforeEach
    void setUp() {
        testInfluencer = Influencer.builder()
                .id(1L)
                .name("测试达人")
                .platform(Influencer.Platform.TIKTOK)
                .build();

        testCooperation = Cooperation.builder()
                .id(1L)
                .influencer(testInfluencer)
                .productId("PROD001")
                .productName("测试产品")
                .type(Cooperation.CooperationType.LIVE)
                .status(Cooperation.CooperationStatus.DRAFT)
                .plannedStartDate(LocalDateTime.now().plusDays(7))
                .plannedEndDate(LocalDateTime.now().plusDays(14))
                .agreedPrice(BigDecimal.valueOf(5000))
                .commissionRate(BigDecimal.valueOf(10.0))
                .expectedViews(100000)
                .expectedSales(100)
                .expectedGmv(BigDecimal.valueOf(20000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testDTO = CooperationDTO.builder()
                .id(1L)
                .influencerId(1L)
                .productId("PROD001")
                .productName("测试产品")
                .type("LIVE_STREAM")
                .status("DRAFT")
                .plannedStartDate(LocalDateTime.now().plusDays(7).toString())
                .plannedEndDate(LocalDateTime.now().plusDays(14).toString())
                .agreedPrice(BigDecimal.valueOf(5000))
                .commissionRate(BigDecimal.valueOf(10.0))
                .expectedViews(100000)
                .expectedSales(100)
                .expectedGmv(BigDecimal.valueOf(20000))
                .build();
    }

    @Test
    @DisplayName("获取所有合作")
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(testCooperation));

        var result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("根据ID获取合作")
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCooperation));

        var result = service.findById(1L);

        assertNotNull(result);
        assertEquals("PROD001", result.getProductId());
    }

    @Test
    @DisplayName("根据达人ID获取合作列表")
    void testFindByInfluencerId() {
        when(repository.findByInfluencerId(1L)).thenReturn(Arrays.asList(testCooperation));

        var result = service.findByInfluencerId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("根据产品ID获取合作列表")
    void testFindByProductId() {
        when(repository.findByProductId("PROD001")).thenReturn(Arrays.asList(testCooperation));

        var result = service.findByProductId("PROD001");

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据类型获取合作列表")
    void testFindByType() {
        when(repository.findByType(Cooperation.CooperationType.LIVE_STREAM))
                .thenReturn(Arrays.asList(testCooperation));

        var result = service.findByType("LIVE_STREAM");

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据状态获取合作列表")
    void testFindByStatus() {
        when(repository.findByStatus(Cooperation.CooperationStatus.DRAFT))
                .thenReturn(Arrays.asList(testCooperation));

        var result = service.findByStatus("DRAFT");

        assertNotNull(result);
    }

    @Test
    @DisplayName("获取已完成合作")
    void testFindCompleted() {
        when(repository.findCompletedCooperations()).thenReturn(Arrays.asList(testCooperation));

        var result = service.findCompleted();

        assertNotNull(result);
    }

    @Test
    @DisplayName("获取即将开始合作")
    void testFindUpcoming() {
        when(repository.findUpcomingCooperations()).thenReturn(Arrays.asList(testCooperation));

        var result = service.findUpcoming();

        assertNotNull(result);
    }

    @Test
    @DisplayName("创建合作")
    void testCreate() {
        when(influencerRepository.findById(1L)).thenReturn(Optional.of(testInfluencer));
        when(repository.save(any(Cooperation.class))).thenReturn(testCooperation);

        var result = service.create(testDTO);

        assertNotNull(result);
        assertEquals("DRAFT", result.getStatus());
    }

    @Test
    @DisplayName("确认合作")
    void testConfirm() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCooperation));
        when(repository.save(any(Cooperation.class))).thenReturn(testCooperation);

        var result = service.confirm(1L);

        assertNotNull(result);
        verify(repository, times(1)).save(any(Cooperation.class));
    }

    @Test
    @DisplayName("开始合作")
    void testStart() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCooperation));
        when(repository.save(any(Cooperation.class))).thenReturn(testCooperation);

        var result = service.start(1L);

        assertNotNull(result);
        assertNotNull(result.getActualStartDate());
    }

    @Test
    @DisplayName("完成合作")
    void testComplete() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCooperation));
        when(repository.save(any(Cooperation.class))).thenReturn(testCooperation);

        CooperationDTO completeDTO = CooperationDTO.builder()
                .actualViews(120000)
                .actualSales(150)
                .actualGmv(BigDecimal.valueOf(30000))
                .build();

        var result = service.complete(1L, completeDTO);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    @DisplayName("取消合作")
    void testCancel() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCooperation));
        when(repository.save(any(Cooperation.class))).thenReturn(testCooperation);

        var result = service.cancel(1L);

        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
    }

    @Test
    @DisplayName("删除合作")
    void testDelete() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("更新合作信息")
    void testUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(testCooperation));
        when(repository.save(any(Cooperation.class))).thenReturn(testCooperation);

        CooperationDTO updateDTO = CooperationDTO.builder()
                .agreedPrice(BigDecimal.valueOf(8000))
                .build();

        var result = service.update(1L, updateDTO);

        assertNotNull(result);
    }
}