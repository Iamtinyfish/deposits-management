package com.bank.depositsmanagement.controller;

import com.bank.depositsmanagement.dao.*;
import com.bank.depositsmanagement.entity.*;
import com.bank.depositsmanagement.utils.CurrencyConstant;
import com.bank.depositsmanagement.utils.TimeConstant;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.bank.depositsmanagement.entity.CurrencyType.USD;

@Controller
public class DepositAccountController {

    private final CustomerRepository customerRepository;
    private final InterestRateReferenceRepository interestRateReferenceRepository;
    private final UserRepository userRepository;
    private final DepositAccountRepository depositAccountRepository;
    private final TransactionRepository transactionRepository;

    public DepositAccountController(CustomerRepository customerRepository, InterestRateReferenceRepository interestRateReferenceRepository, UserRepository userRepository, DepositAccountRepository depositAccountRepository, TransactionRepository transactionRepository) {
        this.customerRepository = customerRepository;
        this.interestRateReferenceRepository = interestRateReferenceRepository;
        this.userRepository = userRepository;
        this.depositAccountRepository = depositAccountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("user/deposit-account/add")
    public String addDepositAccountPage(Model model, @RequestParam(value = "customerID") Long customerID) {

        DepositAccount depositAccount = new DepositAccount();
        Customer customer = customerRepository.findById(customerID).orElse(null);

        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với mã là " + customerID);
            return "404";
        }

        depositAccount.setHolder(customer);
        model.addAttribute("depositAccount", depositAccount);

        List<InterestRateReference> interestRateReferences = new ArrayList<>() ;
        interestRateReferenceRepository.findAll().forEach(interestRateReferences::add);
        model.addAttribute("interestRateReferences", interestRateReferences);

        Set<Integer> periods = interestRateReferences.stream().map(InterestRateReference::getPeriod).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
        model.addAttribute("periods", periods);

        return "add-deposit-account";
    }

    @PostMapping("user/deposit-account/add")
    public String addDepositAccount(Model model, @ModelAttribute("depositAccount") @Valid DepositAccount depositAccount,
                                    @RequestParam(value = "holderID") String holderID,BindingResult bindingResult, Principal principal) {

        Customer customer = customerRepository.findById(Long.parseLong(holderID)).orElse(null);
        if (customer == null) {
            model.addAttribute("message", "Không tìm thấy khách hàng với mã là " + holderID);
            return "404";
        }
        depositAccount.setHolder(customer);

        InterestRateReference interestRateReference = interestRateReferenceRepository.findByPeriodAndCurrency(depositAccount.getPeriod(), depositAccount.getCurrency()).orElse(null);
        if (interestRateReference == null)  {
            model.addAttribute("errorInterest","Không tìm thấy lãi suất phù hợp với kì hạn và loại tiền tệ đã chọn");
        }
        depositAccount.setInterestRate(interestRateReference.getInterestRate());

        BigDecimal balance = depositAccount.getBalance();
        int period = depositAccount.getPeriod();
        String errorBalance = null;

        if (balance != null) {
            switch (depositAccount.getCurrency()) {
                case VND:
                    if (period > 0 && balance.compareTo(BigDecimal.valueOf(1000000)) < 0)
                        errorBalance = "Số tiền gửi có kì hạn với VND tối thiểu là 1,000,000đ";
                    else if (period == 0 && balance.compareTo(BigDecimal.valueOf(500000)) < 0)
                        errorBalance = "Số tiền gửi không kì hạn với VND tối thiểu là 500,000đ";
                    break;
                case USD:
                    if (period > 0 && balance.compareTo(BigDecimal.valueOf(100)) < 0)
                        errorBalance = "Số tiền gửi có kì hạn với USD tối thiểu là 100$";
                    else if (period == 0 && balance.compareTo(BigDecimal.valueOf(50)) < 0)
                        errorBalance = "Số tiền gửi không kì hạn với USD tối thiểu là 50$";
                    break;
            }
        } else {
            errorBalance = "Không được bỏ trống trường này";
        }

        if (bindingResult.hasErrors() || errorBalance != null) {
            model.addAttribute("depositAccount", depositAccount);
            model.addAttribute("errorBalance", errorBalance);

            List<InterestRateReference> interestRateReferences = new ArrayList<>() ;
            interestRateReferenceRepository.findAll().forEach(interestRateReferences::add);
            model.addAttribute("interestRateReferences", interestRateReferences);

            Set<Integer> periods = interestRateReferences.stream().map(InterestRateReference::getPeriod).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            model.addAttribute("periods", periods);
            return "add-deposit-account";
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }
        Employee employee = user.getEmployee();
        depositAccount.setCreateBy(employee);

        Long depositAccountID = depositAccountRepository.save(depositAccount).getId();

        return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
    }

    @GetMapping("user/deposit-account/detail")
    public String depositAccountDetail(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);

        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        model.addAttribute("depositAccount", depositAccount);
        model.addAttribute("dateFormatter", TimeConstant.DATE_FORMATTER);
        model.addAttribute("dateTimeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "deposit-account-detail";
    }

    @GetMapping("user/deposit-account/final-settlement")
    public String finalSettlementPage(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.isFinalSettlement()) {
            return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
        }

        model.addAttribute("depositAccount", depositAccount);
        HashMap<String, Object> result = calculateInterest(depositAccount);

        if (depositAccount.getPeriod() > 0) {
            model.addAttribute("timeDepositDays", result.get("timeDepositDays"));
            model.addAttribute("timeDepositInterestRate", depositAccount.getInterestRate());
            model.addAttribute("timeDepositInterest", depositAccount.getInterest());
            model.addAttribute("balance1", result.get("balance1"));

            model.addAttribute("demandDepositDays", result.get("demandDepositDays"));
            model.addAttribute("demandDepositInterestRate", result.get("demandDepositInterestRate"));
            model.addAttribute("demandDepositInterest", result.get("demandDepositInterest"));
        } else {
            model.addAttribute("demandDepositDays", depositAccount.getNumberOfDay());
            model.addAttribute("demandDepositInterestRate", depositAccount.getInterestRate());
            model.addAttribute("demandDepositInterest", depositAccount.getInterest());
        }

        model.addAttribute("finalBalance", result.get("finalBalance"));
        //round final balance with VND
        BigDecimal amount = (BigDecimal) result.get("finalBalance");
        if (depositAccount.getCurrency() == CurrencyType.VND) {
            final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
            amount = amount.divide(ONE_THOUSAND,0,RoundingMode.FLOOR).multiply(ONE_THOUSAND);
        }
        model.addAttribute("amount", amount.setScale(0,RoundingMode.FLOOR));
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "final-settlement";
    }

    @PostMapping("user/deposit-account/final-settlement")
    @Transactional
    public String finalSettlment(Model model, @RequestParam(value = "depositAccountID") Long depositAccountID, Principal principal) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.isFinalSettlement()) {
            return "redirect:/user/deposit-account/detail?depositAccountID="+depositAccountID;
        }

        //round final balance with VND
        BigDecimal amount = ((BigDecimal) calculateInterest(depositAccount).get("finalBalance")).setScale(0, RoundingMode.FLOOR);
        if (depositAccount.getCurrency() == CurrencyType.VND) {
            final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
            amount = amount.divide(ONE_THOUSAND,0,RoundingMode.FLOOR).multiply(ONE_THOUSAND);
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .depositAccount(depositAccount)
                        .amount(amount)
                        .description("Tất toán tài khoản")
                        .employee(employee)
                        .build()
        );

        depositAccount.setFinalSettlement(true);
        depositAccount.setBalance(BigDecimal.ZERO);
        depositAccount.setInterest(BigDecimal.ZERO);
        depositAccountRepository.save(depositAccount);

        model.addAttribute("transaction", transaction);
        model.addAttribute("timeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "transaction-result";
    }

    @GetMapping("user/deposit-account/withdraw-part")
    public String withdrawPartPage(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.getPeriod() != 0) {
            model.addAttribute("message", "Chức năng này không khả dụng với tài khoản tiền gửi có kì hạn");
            return "error";
        }

        model.addAttribute("depositAccountID",depositAccountID);
        model.addAttribute("currency", depositAccount.getCurrency().toString());
        model.addAttribute("balance", depositAccount.getBalance().add(depositAccount.getInterest()).setScale(2,RoundingMode.HALF_UP));
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "withdraw-part";
    }

    @PostMapping("user/deposit-account/withdraw-part")
    @Transactional
    public String withdrawPart(Model model,@RequestParam(value = "depositAccountID") Long depositAccountID,
                               @RequestParam(value = "amount") BigDecimal amount, Principal principal) {
        DepositAccount depositAccount = depositAccountRepository.findById(depositAccountID).orElse(null);
        if (depositAccount == null) {
            model.addAttribute("message", "Không tìm thấy tài khoản tiền gửi với mã là " + depositAccountID);
            return "404";
        }

        if (depositAccount.getPeriod() != 0) {
            model.addAttribute("message", "Chức năng này không khả dụng với tài khoản tiền gửi có kì hạn");
            return "error";
        }

        BigDecimal balance = depositAccount.getBalance().add(depositAccount.getInterest()).setScale(0,RoundingMode.HALF_UP);
        String error = null;

        //============validate============//
        if (amount == null) error = "Không được bỏ trống trường này";
        else {
            if (amount.compareTo(balance) == 1) {
                error = "Số tiền phải nhỏ hơn số dư khả dụng";
            } else {
                if (depositAccount.getCurrency() == CurrencyType.VND
                        && amount.remainder(BigDecimal.valueOf(100000)).compareTo(BigDecimal.ZERO) != 0) {
                    error = "Số tiền phải là bội số của 100,000 &#8363;";
                } else if (depositAccount.getCurrency() == CurrencyType.USD
                        && amount.remainder(BigDecimal.valueOf(5)).compareTo(BigDecimal.ZERO) != 0) {
                    error = "Số tiền phải là bội số của $5";
                }
            }
        }

        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("depositAccountID",depositAccountID);
            model.addAttribute("currency", depositAccount.getCurrency().toString());
            model.addAttribute("balance", balance);
            model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
            return "withdraw-part";
        }
        //=================================//

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user == null || user.getEmployee() == null) {
            model.addAttribute("message", "Không xác định được thông tin của bạn");
            return "404";
        }

        Employee employee = user.getEmployee();

        depositAccount.setBalance(balance.subtract(amount).setScale(0,RoundingMode.HALF_UP));
        depositAccount.setInterest(BigDecimal.ZERO);
        depositAccount.setPeriodStartAt(LocalDate.now());
        depositAccountRepository.save(depositAccount);

        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .depositAccount(depositAccount)
                        .amount(amount)
                        .description("Rút một phần")
                        .employee(employee)
                        .build()
        );

        model.addAttribute("transaction", transaction);
        model.addAttribute("timeFormatter", TimeConstant.DATE_TIME_FORMATTER);
        model.addAttribute("currencyFormatter", (depositAccount.getCurrency() == CurrencyType.VND) ? CurrencyConstant.VND_FORMATTER : CurrencyConstant.USD_FORMATTER);
        return "transaction-result";
    }

    private HashMap<String, Object> calculateInterest(DepositAccount depositAccount) {
        HashMap<String,Object> result = new HashMap<>();

        int period = depositAccount.getPeriod();

        BigDecimal finalBalance;

        if (period > 0) {
            int dayOfPeriod = period * TimeConstant.DAY_OF_MONTH;
            int timeDepositDays = (depositAccount.getNumberOfDay() / dayOfPeriod) * dayOfPeriod;
            result.put("timeDepositDays", timeDepositDays);
            BigDecimal balance1 = depositAccount.getBalance().add(depositAccount.getInterest()).setScale(2, RoundingMode.HALF_UP);
            result.put("balance1", balance1);

            int demandDepositDays = depositAccount.getNumberOfDay() - timeDepositDays;
            result.put("demandDepositDays", demandDepositDays);
            InterestRateReference demandInterestRateReference = this.interestRateReferenceRepository.findByPeriodAndCurrency(0,depositAccount.getCurrency()).orElse(null);
            float demandDepositInterestRate = demandInterestRateReference.getInterestRate();
            result.put("demandDepositInterestRate", demandDepositInterestRate);
            BigDecimal demandDepositInterest = balance1.multiply(
                    BigDecimal.valueOf(
                            demandDepositDays * demandDepositInterestRate * 0.01 / 360
                    )
            ).setScale(2, RoundingMode.HALF_UP);
            result.put("demandDepositInterest", demandDepositInterest);
            finalBalance = balance1.add(
                    demandDepositInterest
            ).setScale(2, RoundingMode.HALF_UP);
        } else {
            finalBalance = depositAccount.getBalance().add(
                    depositAccount.getInterest()
            ).setScale(2,RoundingMode.HALF_UP);
        }
        result.put("finalBalance", finalBalance);
        return result;
    }
}
