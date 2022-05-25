package com.bank.depositsmanagement;


import com.bank.depositsmanagement.controller.MainController;
import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.entity.Account;
import com.bank.depositsmanagement.entity.Employee;
import com.bank.depositsmanagement.entity.EmployeeStatus;
import com.bank.depositsmanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.sql.DataSource;
import java.security.Principal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@WebMvcTest(MainController.class)
@DisplayName("View my profile function test:")
public class ViewMyProfileTests {

    @Autowired
    private MockMvc mvc;

    @Mock(extraInterfaces = {Authentication.class})
    private Principal mockPrincipal;
    @Mock
    private Account mockAccount;
    @Mock
    private Employee mockEmployee;

    @MockBean
    private AccountRepository mockAccountRepository;
    @MockBean
    private EmployeeRepository mockEmployeeRepository;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private DataSource mockDataSource;

    @BeforeTestMethod
    public void init() {
        when((Account) ((Authentication) mockPrincipal).getPrincipal()).thenReturn(mockAccount);
    }

    @Test
    @DisplayName("When view my profile success - should return my profile page")
    public void whenViewMyProfileSuccess() throws Exception{
        when(mockAccount.getEmployee()).thenReturn(mockEmployee);
        when(mockEmployee.getStatus()).thenReturn(EmployeeStatus.WORKING);
        when(mockEmployee.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockEmployee.getLastModifiedAt()).thenReturn(LocalDateTime.now());

        this.mvc.perform(get("/my-profile").with(user(mockAccount)))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attribute("employee", mockEmployee));
    }

    @Test
    @DisplayName("When view my profile but not found my profile - should return 404 page")
    public void whenViewMyProfileButNotFoundMyProfile() throws Exception{
        when(mockAccount.getEmployee()).thenReturn(null);

        this.mvc.perform(get("/my-profile").with(user(mockAccount)))
                .andExpect(status().isOk())
                .andExpect(view().name("404"))
                .andExpect(model().attribute("message", "Không tìm thấy thông tin của bạn"));
    }


}
