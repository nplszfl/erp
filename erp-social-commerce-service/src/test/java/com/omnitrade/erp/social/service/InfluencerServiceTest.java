package com.omnitrade.erp.social.service;

import com.omnitrade.erp.social.dto.InfluencerDTO;
import com.omnitrade.erp.social.model.Influencer;
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
@DisplayName("达人服务测试")
class InfluencerServiceTest {

    @Mock
    private InfluencerRepository repository;

    @InjectMocks
    private InfluencerService service;

    private Influencer testInfluencer;

    @BeforeEach
    void setUp() {
        testInfluencer = Influencer.builder()
                .id(1L)
                .name("测试达人")
                .platform(Influencer.Platform.TIKTOK)
                .followerCount(100000)
                .totalLikes(50000)
                .level(Influencer.InfluencerLevel.KOL)
                .contactEmail("test@example.com")
                .contactPhone("+8613800138000")
                .status(Influencer.InfluencerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("获取所有达人")
    void testFindAll() {
        when(repository.findAll()).thenReturn(Arrays.asList(testInfluencer));

        var result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("根据ID获取达人")
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(testInfluencer));

        var result = service.findById(1L);

        assertNotNull(result);
        assertEquals("测试达人", result.getName());
    }

    @Test
    @DisplayName("根据平台获取达人列表")
    void testFindByPlatform() {
        when(repository.findByPlatform(Influencer.Platform.TIKTOK))
                .thenReturn(Arrays.asList(testInfluencer));

        var result = service.findByPlatform("TIKTOK");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("根据状态获取达人列表")
    void testFindByStatus() {
        when(repository.findByStatus(Influencer.InfluencerStatus.ACTIVE))
                .thenReturn(Arrays.asList(testInfluencer));

        var result = service.findByStatus("ACTIVE");

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据品类获取达人列表")
    void testFindByCategory() {
        when(repository.findByCategory("美妆")).thenReturn(Arrays.asList(testInfluencer));

        var result = service.findByCategory("美妆");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("创建达人")
    void testCreate() {
        when(repository.save(any(Influencer.class))).thenReturn(testInfluencer);

        InfluencerDTO dto = InfluencerDTO.builder()
                .name("测试达人")
                .platform("TIKTOK")
                .followerCount(100000)
                .build();

        var result = service.create(dto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("更新达人信息")
    void testUpdate() {
        when(repository.findById(1L)).thenReturn(Optional.of(testInfluencer));
        when(repository.save(any(Influencer.class))).thenReturn(testInfluencer);

        InfluencerDTO updateData = InfluencerDTO.builder()
                .name("新名字")
                .followerCount(200000)
                .build();

        var result = service.update(1L, updateData);

        assertNotNull(result);
    }

    @Test
    @DisplayName("删除达人")
    void testDelete() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("获取高粉丝达人")
    void testFindTopInfluencers() {
        when(repository.findTopInfluencersByFollowers(10000)).thenReturn(Arrays.asList(testInfluencer));

        var result = service.findTopInfluencers(10000);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("测试达人", result.get(0).getName());
    }

    @Test
    @DisplayName("根据等级查询达人")
    void testFindByLevel() {
        when(repository.findByLevel(Influencer.InfluencerLevel.KOL))
                .thenReturn(Arrays.asList(testInfluencer));

        var result = service.findByLevel("KOL");

        assertNotNull(result);
    }

    @Test
    @DisplayName("创建达人 - 缺少平台信息")
    void testCreateWithMissingPlatform() {
        assertThrows(Exception.class, () -> {
            service.create(InfluencerDTO.builder().name("测试达人").build());
        });
    }
}