package com.bank.depositsmanagement;

import com.bank.depositsmanagement.controller.MainController;
import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.entity.Account;
import com.bank.depositsmanagement.entity.Employee;
import com.bank.depositsmanagement.entity.EmployeeStatus;
import com.bank.depositsmanagement.service.UserService;
import com.mysql.cj.log.Log;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Equality;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.sql.DataSource;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
@DisplayName("Update my profile function test:")
public class UpdateMyProfileTests {
    @Autowired
    private MockMvc mvc;

    @Mock(extraInterfaces = {Authentication.class})
    private Principal mockPrincipal;
    @Mock
    private Account mockAccount;
    @Mock
    private Employee mockEmployee;

    private LocalDateTime mockTime = LocalDateTime.now();

    @MockBean
    private AccountRepository mockAccountRepository;
    @MockBean
    private EmployeeRepository mockEmployeeRepository;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private DataSource mockDataSource;

    @Captor
    ArgumentCaptor<Employee> employeeCaptor;

    private final Employee oldInfo = Employee.builder()
            .firstName("Unknown")
            .lastName("Unknown")
            .gender(false)
            .birthday(LocalDate.now())
            .IDCard("123456789100")
            .phone("1234567890")
            .email("employee1@bank.com")
            .address("Unknown")
            .status(EmployeeStatus.WORKING)
            .account(mockAccount)
            .createdAt(mockTime)
            .lastModifiedAt(mockTime)
            .build();

    private final Employee newInfo = Employee.builder()
            .firstName("Hieu")
            .lastName("Nguyen")
            .gender(false)
            .birthday(LocalDate.of(2000, Month.DECEMBER,4))
            .IDCard("031200003332")
            .phone("0123456789")
            .email("employee2@bank.com")
            .address("Kien Thuy")
            .build();

    @BeforeTestMethod
    public void init() {
        when((Account) ((Authentication) mockPrincipal).getPrincipal()).thenReturn(mockAccount);
    }

    @Test
    @DisplayName("When update my profile success - should redirect to my profile page")
    public void whenUpdateMyProfileSuccess() throws Exception{
        when(mockAccount.getEmployee()).thenReturn(oldInfo);
        when(mockEmployeeRepository.save(any(Employee.class))).thenReturn(newInfo);

        this.mvc.perform(post("/my-profile/update")
                        .with(user(mockAccount))
                        .with(csrf())
                        .flashAttr("employee",newInfo))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/my-profile"));

        verify(mockEmployeeRepository).save(employeeCaptor.capture());

        Employee expectInfo = (Employee) SerializationUtils.deserialize(SerializationUtils.serialize(newInfo));
        assert expectInfo != null;
        expectInfo.setStatus(EmployeeStatus.WORKING);
        expectInfo.setCreatedAt(mockTime);

        assertThat(employeeCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectInfo);
    }

    @Test
    @DisplayName("When update my profile but not found old my profile - should return 404 page")
    public void whenUpdateMyProfileButNotFoundOldMyProfile() throws Exception{
        when(mockAccount.getEmployee()).thenReturn(null);

        this.mvc.perform(post("/my-profile/update")
                        .with(user(mockAccount))
                        .with(csrf())
                        .flashAttr("employee",newInfo))
                .andExpect(status().isOk())
                .andExpect(view().name("404"))
                .andExpect(model().attribute("message", "Không tìm thấy thông tin của bạn"));
    }

    @Test
    @DisplayName("When update my profile empty form - should return error field message in form")
    public void whenUpdateMyProfileEmptyForm() throws Exception {
        when(mockAccount.getEmployee()).thenReturn(oldInfo);
        Employee emptyInfo = Employee.builder()
                .firstName("")
                .lastName("")
                .gender(false)
                .IDCard("")
                .phone("")
                .email("")
                .address("")
                .build();

        MvcResult mvcResult = this.mvc.perform(post("/my-profile/update").flashAttr("employee",emptyInfo).with(user(mockAccount)).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeHasErrors("employee"))
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        BindingResult bindingResult = (BindingResult) mav.getModel().get("org.springframework.validation.BindingResult.employee");
        String expectBlankErrorMessage = "Không được bỏ trống trường này";
        String expectIDCardErrorMessage = "Độ dài 12 số";
        String expectPhoneErrorMessage = "Độ dài từ 7 - 20 số";

        assertEquals(expectIDCardErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("IDCard")).getDefaultMessage());
        assertEquals(expectPhoneErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("phone")).getDefaultMessage());
        for (String s : Arrays.asList("firstName","lastName","birthday","email","address")) {
            String actualBlankErrorMessage = Objects.requireNonNull(bindingResult.getFieldError(s)).getDefaultMessage();
            assertEquals(expectBlankErrorMessage,actualBlankErrorMessage);
        }
    }

    @Test
    @DisplayName("When update my profile but new IDCard,Email existed - should return error field message in form")
    public void whenUpdateMyProfileButIDCardAndEmailExisted() throws Exception {
        when(mockAccount.getEmployee()).thenReturn(oldInfo);
        when(mockEmployeeRepository.existsByIDCard(anyString())).thenReturn(true);
        when(mockEmployeeRepository.existsByEmail(anyString())).thenReturn(true);

        MvcResult mvcResult = this.mvc.perform(post("/my-profile/update")
                        .flashAttr("employee",newInfo)
                        .with(user(mockAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("my-profile"))
                .andExpect(model().attributeHasErrors("employee"))
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        BindingResult bindingResult = (BindingResult) mav.getModel().get("org.springframework.validation.BindingResult.employee");
        String expectIDCardErrorMessage = "Số CCCD đã tồn tại";
        String expectEmailErrorMessage = "Email đã tồn tại";

        assertEquals(expectIDCardErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("IDCard")).getDefaultMessage());
        assertEquals(expectEmailErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("email")).getDefaultMessage());
    }
}
