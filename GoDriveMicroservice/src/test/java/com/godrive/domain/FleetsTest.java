package com.godrive.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.godrive.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FleetsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Fleets.class);
        Fleets fleets1 = new Fleets();
        fleets1.setId(1L);
        Fleets fleets2 = new Fleets();
        fleets2.setId(fleets1.getId());
        assertThat(fleets1).isEqualTo(fleets2);
        fleets2.setId(2L);
        assertThat(fleets1).isNotEqualTo(fleets2);
        fleets1.setId(null);
        assertThat(fleets1).isNotEqualTo(fleets2);
    }
}
