package com.bank.depositsmanagement;


import com.bank.depositsmanagement.controller.CustomerController;
import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.entity.Account;
import com.bank.depositsmanagement.entity.Customer;
import com.bank.depositsmanagement.entity.Employee;
import com.bank.depositsmanagement.entity.EmployeeStatus;
import com.bank.depositsmanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerController.class)
@DisplayName("View customer function test:")
public class ViewCustomerTests {

    @Autowired
    private MockMvc mvc;

    @Mock(extraInterfaces = {Authentication.class})
    private Principal mockPrincipal;
    @Mock
    private Account mockAccount;
    @Mock
    private Employee mockEmployee;

    @MockBean
    private CustomerRepository mockCustomerRepository;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private DataSource mockDataSource;

    private LocalDateTime mockTime = LocalDateTime.now();

    @BeforeTestMethod
    public void init() {
        when((Account) ((Authentication) mockPrincipal).getPrincipal()).thenReturn(mockAccount);
    }

    @Test
    @DisplayName("When view customer detail success - should return customer detail page")
    public void whenViewCustomerDetailSuccess() throws Exception{
        Customer customer = Customer.builder()
                .firstName("Hieu")
                .lastName("Nguyen")
                .gender(false)
                .birthday(LocalDate.now())
                .IDCard("123456789100")
                .phone("1234567890")
                .email("customer1@bank.com")
                .address("Kien Thuy")
                .createdAt(mockTime)
                .lastModifiedAt(mockTime)
                .lastModifiedBy(mockEmployee)
                .build();

        when(mockCustomerRepository.findByIDCard(anyString())).thenReturn(Optional.of(customer));
        when(mockAccount.getEmployee()).thenReturn(mockEmployee);
        when(mockEmployee.getId()).thenReturn(1L);

        this.mvc.perform(get("/employee/customer/detail").param("IDCard","031200003331").with(user(mockAccount)))
                .andExpect(status().isOk())
                .andExpect(view().name("customer-detail"))
                .andExpect(model().attribute("customer", customer));
    }

    @Test
    @DisplayName("When view customer detail but not found customer info - should return 404 page")
    public void whenViewCustomerDetailButNotFoundCustomerDetail() throws Exception{
        when(mockCustomerRepository.findByIDCard(anyString())).thenReturn(Optional.ofNullable(null));

        this.mvc.perform(get("/employee/customer/detail").param("IDCard","031200003331").with(user(mockAccount)))
                .andExpect(status().isOk())
                .andExpect(view().name("404"))
                .andExpect(model().attribute("message", "Không tìm thấy khách hàng với số CCCD là 031200003331"));
    }


}
