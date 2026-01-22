package com.sebastianrodriguez.backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integracion del flujo CRUD con borrado logico y cascada.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FranchiseApiCrudTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Ejecuta un flujo completo: crear, actualizar, reportar, borrar y validar cascade.
     */
    @Test
    void crudFlowWithSoftDeleteCascade() throws Exception {
        long franchiseId = createFranchise("Franquicia Uno");

        long branchId1 = createBranch(franchiseId, "Sucursal Norte");
        long productId1 = createProduct(branchId1, "Producto A", 10);
        long productId1b = createProduct(branchId1, "Producto A2", 20);

        long branchId2 = createBranch(franchiseId, "Sucursal Sur");
        long productId2 = createProduct(branchId2, "Producto B", 5);
        long productId2b = createProduct(branchId2, "Producto B2", 7);

        mockMvc.perform(get("/api/franchises/{id}", franchiseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(franchiseId))
                .andExpect(jsonPath("$.branches").isArray());

        mockMvc.perform(put("/api/products/{id}", productId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Producto A\",\"stock\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(15));

        mockMvc.perform(get("/api/franchises/{id}/top-stock-products", franchiseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.branchId==%d)].productName", branchId1)
                        .value(contains("Producto A2")))
                .andExpect(jsonPath("$[?(@.branchId==%d)].stock", branchId1)
                        .value(contains(20)))
                .andExpect(jsonPath("$[?(@.branchId==%d)].productName", branchId2)
                        .value(contains("Producto B2")))
                .andExpect(jsonPath("$[?(@.branchId==%d)].stock", branchId2)
                        .value(contains(7)));

        mockMvc.perform(delete("/api/branches/{id}", branchId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/branches/{id}", branchId1))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/products/{id}", productId1))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/products/{id}", productId1b))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/franchises/{id}", franchiseId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/franchises/{id}", franchiseId))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/branches/{id}", branchId2))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/products/{id}", productId2))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/products/{id}", productId2b))
                .andExpect(status().isNotFound());
    }

    /**
     * Crea una franquicia via API y retorna su id.
     *
     * @param name nombre de la franquicia.
     * @return id creado.
     */
    private long createFranchise(String name) throws Exception {
        String response = mockMvc.perform(post("/api/franchises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    /**
     * Crea una sucursal via API y retorna su id.
     *
     * @param franchiseId id de la franquicia.
     * @param name nombre de la sucursal.
     * @return id creado.
     */
    private long createBranch(long franchiseId, String name) throws Exception {
        String response = mockMvc.perform(post("/api/franchises/{id}/branches", franchiseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }

    /**
     * Crea un producto via API y retorna su id.
     *
     * @param branchId id de la sucursal.
     * @param name nombre del producto.
     * @param stock stock inicial.
     * @return id creado.
     */
    private long createProduct(long branchId, String name, int stock) throws Exception {
        String response = mockMvc.perform(post("/api/branches/{id}/products", branchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"stock\":" + stock + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        return node.get("id").asLong();
    }
}
