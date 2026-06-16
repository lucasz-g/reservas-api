package br.com.garcia.reservas_api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiErrorHandlingTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarErroPadronizadoQuandoAcessoNaoAutorizado() throws Exception {
        mockMvc.perform(get("/api/v1/reservas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value(containsString("Acesso nao autorizado")))
                .andExpect(jsonPath("$.path").value("/api/v1/reservas"));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoDadosInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/reservas/criar")
                .with(httpBasic("admin", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(containsString("salaId")))
                .andExpect(jsonPath("$.path").value("/api/v1/reservas/criar"));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoSalaNaoExiste() throws Exception {
        String payload = """
                {
                  "salaId": 999,
                  "nomeSolicitante": "Maria Lima",
                  "email": "maria.lima@example.com",
                  "dataReserva": "2026-06-20",
                  "horaInicio": "09:00:00",
                  "horaFim": "10:00:00",
                  "finalidade": "Reuniao"
                }
                """;

        mockMvc.perform(post("/api/v1/reservas/criar")
                .with(httpBasic("admin", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Sala nao encontrada"))
                .andExpect(jsonPath("$.path").value("/api/v1/reservas/criar"));
    }

    @Test
    void deveRetornarErroPadronizadoQuandoExisteConflitoDeHorario() throws Exception {
        String payload = """
                {
                  "salaId": 1,
                  "nomeSolicitante": "Maria Lima",
                  "email": "maria.lima@example.com",
                  "dataReserva": "2026-06-17",
                  "horaInicio": "09:30:00",
                  "horaFim": "10:30:00",
                  "finalidade": "Reuniao"
                }
                """;

        mockMvc.perform(post("/api/v1/reservas/criar")
                .with(httpBasic("admin", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Ja existe reserva para esta sala no horario informado"))
                .andExpect(jsonPath("$.path").value("/api/v1/reservas/criar"));
    }
}
