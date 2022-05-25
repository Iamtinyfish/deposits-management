package com.bank.depositsmanagement;

import com.bank.depositsmanagement.controller.CustomerController;
import com.bank.depositsmanagement.controller.MainController;
import com.bank.depositsmanagement.dao.AccountRepository;
import com.bank.depositsmanagement.dao.CustomerRepository;
import com.bank.depositsmanagement.dao.EmployeeRepository;
import com.bank.depositsmanagement.entity.Account;
import com.bank.depositsmanagement.entity.Customer;
import com.bank.depositsmanagement.entity.Employee;
import com.bank.depositsmanagement.entity.EmployeeStatus;
import com.bank.depositsmanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.SerializationUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.DataSource;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("Add customer function test:")
public class AddCustomerTests {
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
    private CustomerRepository mockCustomerRepository;
    @MockBean
    private UserService mockUserService;
    @MockBean
    private DataSource mockDataSource;

    @Captor
    ArgumentCaptor<Customer> customerCaptor;

    private final Customer customer = Customer.builder()
            .firstName("Hieu")
            .lastName("Nguyen")
            .gender(false)
            .birthday(LocalDate.of(2000, Month.DECEMBER,4))
            .IDCard("031200003331")
            .phone("0123456789")
            .email("employee1@bank.com")
            .address("Kien Thuy")
            .build();

    @BeforeTestMethod
    public void init() {
        when((Account) ((Authentication) mockPrincipal).getPrincipal()).thenReturn(mockAccount);
    }

    @Test
    @DisplayName("When access add customer page - should response add customer page")
    public void whenAccessAddCustomerPage() throws Exception{

        this.mvc.perform(get("/employee/customer/add")
                        .with(user(mockAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-customer"))
                .andExpect(model().attributeExists("customer"));
    }

    @Test
    @DisplayName("When add customer success - should redirect to customer detail page")
    public void whenAddCustomerSuccess() throws Exception{
        when(mockAccount.getEmployee()).thenReturn(mockEmployee);
        when(mockCustomerRepository.save(any(Customer.class))).thenReturn(customer);

        this.mvc.perform(post("/employee/customer/add")
                        .with(user(mockAccount))
                        .with(csrf())
                        .flashAttr("customer",customer))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/employee/customer/detail?IDCard=" + customer.getIDCard()));

        verify(mockCustomerRepository).save(customerCaptor.capture());

        Customer expectCustomerInfo = (Customer) SerializationUtils.deserialize(SerializationUtils.serialize(customer));
        assert expectCustomerInfo != null;
        expectCustomerInfo.setLastModifiedBy(mockEmployee);

        assertThat(customerCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expectCustomerInfo);
    }

    @Test
    @DisplayName("When add customer but not found my profile - should return 404 page")
    public void whenAddCustomerButNotFoundOldMyProfile() throws Exception{
        when(mockAccount.getEmployee()).thenReturn(null);

        this.mvc.perform(post("/employee/customer/add")
                        .with(user(mockAccount))
                        .with(csrf())
                        .flashAttr("customer",customer))
                .andExpect(status().isOk())
                .andExpect(view().name("404"))
                .andExpect(model().attribute("message", "Không tìm thấy thông tin của bạn"));
    }

    @Test
    @DisplayName("When add customer empty form - should return error field message in form")
    public void whenAddCustomerEmptyForm() throws Exception {
        when(mockAccount.getEmployee()).thenReturn(mockEmployee);
        Customer emptyCustomerInfo = Customer.builder()
                .firstName("")
                .lastName("")
                .gender(false)
                .IDCard("")
                .phone("")
                .email("")
                .address("")
                .build();

        MvcResult mvcResult = this.mvc.perform(post("/employee/customer/add")
                        .flashAttr("customer",emptyCustomerInfo)
                        .with(user(mockAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-customer"))
                .andExpect(model().attributeHasErrors("customer"))
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        BindingResult bindingResult = (BindingResult) mav.getModel().get("org.springframework.validation.BindingResult.customer");
        String expectBlankErrorMessage = "Không được bỏ trống trường này";
        String expectIDCardErrorMessage = "Độ dài 12 số";
        String expectPhoneErrorMessage = "Độ dài từ 7 - 20 số";

        assertEquals(expectIDCardErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("IDCard")).getDefaultMessage());
        assertEquals(expectPhoneErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("phone")).getDefaultMessage());
        for (String s : Arrays.asList("firstName","lastName","birthday","address")) {
            String actualBlankErrorMessage = Objects.requireNonNull(bindingResult.getFieldError(s)).getDefaultMessage();
            assertEquals(expectBlankErrorMessage,actualBlankErrorMessage);
        }
    }

    @Test
    @DisplayName("When add customer but new IDCard,Email existed - should return error field message in form")
    public void whenAddCustomerButIDCardAndEmailExisted() throws Exception {
        when(mockAccount.getEmployee()).thenReturn(mockEmployee);
        when(mockCustomerRepository.existsByIDCard(anyString())).thenReturn(true);
        when(mockCustomerRepository.existsByEmail(anyString())).thenReturn(true);

        MvcResult mvcResult = this.mvc.perform(post("/employee/customer/add")
                        .flashAttr("customer", customer)
                        .with(user(mockAccount))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("add-customer"))
                .andExpect(model().attributeHasErrors("customer"))
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        BindingResult bindingResult = (BindingResult) mav.getModel().get("org.springframework.validation.BindingResult.customer");
        String expectIDCardErrorMessage = "Số CCCD đã tồn tại";
        String expectEmailErrorMessage = "Email đã tồn tại";

        assertEquals(expectIDCardErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("IDCard")).getDefaultMessage());
        assertEquals(expectEmailErrorMessage, Objects.requireNonNull(bindingResult.getFieldError("email")).getDefaultMessage());
    }
}
