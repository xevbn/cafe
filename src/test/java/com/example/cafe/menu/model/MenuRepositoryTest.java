package com.example.cafe.menu.model;

import com.example.cafe.TestcontainersConfiguration;
import com.example.cafe.common.config.JpaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaConfig.class, TestcontainersConfiguration.class})
@ActiveProfiles("test")
public class MenuRepositoryTest {
    @Autowired
    private MenuRepository menuRepository;

    @Test
    @DisplayName("findAllWhereStatusIsONSALE jpql 테스트")
    void findAllWhereStatusIsONSALE_jpql_테스트() {
        //given
        menuRepository.saveAndFlush(Menu.create("name", 1000));

        //when
        List<Menu> result = menuRepository.findAllWhereOrderStatusIsOnSale();

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isNotNull();
        assertThat(result.get(0).getStatus())
                .isEqualTo(MenuStatus.ON_SALE);
    }
}
