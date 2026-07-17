package com.whlg.hospital;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SystemApiTest extends BaseApiTest {

    @Test
    void shouldGetConfig() throws Exception {
        mockMvc.perform(get("/api/configs/paymentMethods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.key").value("paymentMethods"))
                .andExpect(jsonPath("$.data.value").isArray());
    }

    @Test
    void shouldGetHealth() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("UP"));
    }
}