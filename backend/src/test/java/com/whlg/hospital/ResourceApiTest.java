package com.whlg.hospital;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ResourceApiTest extends BaseApiTest {

    @Test
    void shouldGetHomeIndex() throws Exception {
        mockMvc.perform(get("/api/home/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.banners").isArray())
                .andExpect(jsonPath("$.data.recommendDoctors[*].id", hasItem(1)));
    }

    @Test
    void shouldListHospitals() throws Exception {
        mockMvc.perform(get("/api/hospitals").param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records").isArray())
                .andExpect(jsonPath("$.data.total").value(2));
    }

    @Test
    void shouldGetHospitalDetail() throws Exception {
        mockMvc.perform(get("/api/hospitals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("北京第一人民医院"));
    }

    @Test
    void shouldGetHospitalDepartments() throws Exception {
        mockMvc.perform(get("/api/hospitals/1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name", hasItem("心血管内科")));
    }

    @Test
    void shouldGetHospitalDoctors() throws Exception {
        mockMvc.perform(get("/api/hospitals/1/doctors").param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[*].hospitalId", hasItem(1)));
    }

    @Test
    void shouldGetDepartmentTree() throws Exception {
        mockMvc.perform(get("/api/departments/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..name", hasItem("内科")));
    }

    @Test
    void shouldListDoctors() throws Exception {
        mockMvc.perform(get("/api/doctors").param("departmentId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[*].departmentId", hasItem(2)));
    }

    @Test
    void shouldGetDoctorDetail() throws Exception {
        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hospitalName").value("北京第一人民医院"));
    }

    @Test
    void shouldGetDoctorSchedules() throws Exception {
        mockMvc.perform(get("/api/doctors/1/schedules").param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].timeSlot").isString());
    }

    @Test
    void shouldGetDoctorReviews() throws Exception {
        mockMvc.perform(get("/api/doctors/1/reviews").param("page", "1").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[*].rating", hasItem(5)));
    }

    @Test
    void shouldListDiseases() throws Exception {
        mockMvc.perform(get("/api/diseases").param("keyword", "高血压"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[*].name", hasItem("高血压")));
    }

    @Test
    void shouldGetDiseaseDetail() throws Exception {
        mockMvc.perform(get("/api/diseases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recommendDoctors").isArray());
    }

    @Test
    void shouldListArticles() throws Exception {
        mockMvc.perform(get("/api/articles").param("keyword", "康复"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[*].title", hasItem("膝关节康复指南")));
    }

    @Test
    void shouldGetArticleDetail() throws Exception {
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.departmentName").value("心血管内科"));
    }

    @Test
    void shouldGlobalSearch() throws Exception {
        mockMvc.perform(get("/api/search/global").param("keyword", "高血压"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.diseaseList").isArray())
                .andExpect(jsonPath("$.data.articleList").isArray());
    }
}