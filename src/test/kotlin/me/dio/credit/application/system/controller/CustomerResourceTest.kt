package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal

@ContextConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class CustomerResourceTest {
    @Autowired lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }
@BeforeEach fun setup() = customerRepository.deleteAll()
@AfterEach fun tearDown() = customerRepository.deleteAll()

@Test
fun `should create a customer and return 201 status`() {
    val customerDto: CustomerDto = builderCustomerDto()
    val valueAsString: String = objectMapper.writeValueAsString(customerDto)
    //when
    //then
    mockMvc.perform(MockMvcRequestBuilders.post(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsString))
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Thiago"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Jesus"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("33504653825"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("thiagovosc@gmail.com"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("3000.0"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("08122050"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Dot Com"))
        //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
        .andDo(MockMvcResultHandlers.print())
}
    @Test
    fun `should not save a customer with same CPF and return 409 status`() {
        //given
        customerRepository.save(builderCustomerDto().toEntity())
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the Documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }
    @Test
    fun `should not save a customer with empty firstName and return 400 status`() {
        //given
        val customerDto: CustomerDto = builderCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .content(valueAsString)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the Documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find customer by id and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Thiago"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Jesus"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("33504653825"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("thiagovosc@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("3000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("08122050"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Dot Com"))
            //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }
    @Test
    fun `should not find customer with invalid id and return 400 status`() {
        //given
        val invalidId: Long = 2L
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/$invalidId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the Documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }
    @Test
    fun `should delete customer by id and return 204 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer by id and return 400 status`() {
        //given
        val invalidId: Long = java.util.Random().nextLong()
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${invalidId}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the Documentation."))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class me.dio.credit.application.system.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update a customer and return 200 status`() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("ThiagoUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("JesusUpdate"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("33504653825"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("thiagovosc@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("5000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("07123000"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua Dot Comma Updated"))
            //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andDo(MockMvcResultHandlers.print())
    }


    private fun builderCustomerDto(
        firstName: String = "Thiago",
        lastName: String = "Jesus",
        cpf: String = "33504653825",
        email: String = "thiagovosc@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(3000.0),
        password: String = "012345",
        zipCode: String = "08122050",
        street: String = "Rua Dot Com"

    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street,
    )

    private fun builderCustomerUpdateDto(
        firstName: String = "ThiagoUpdate",
        lastName: String = "JesusUpdate",
        income: BigDecimal = BigDecimal.valueOf(3000.0),
        zipCode: String = "07123040",
        street: String = "Rua Dot Com Updated"
    ): CustomerUpdateDto = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )

}