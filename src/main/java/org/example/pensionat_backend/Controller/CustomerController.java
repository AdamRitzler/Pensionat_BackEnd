package org.example.pensionat_backend.Controller;

import jakarta.validation.Valid;
import org.example.pensionat_backend.DTO.CustomerDTO;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.example.pensionat_backend.Service.CustomerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//det här är bara för att jag skulle testa :)
@Controller
@RequestMapping("/html")
public class CustomerController {
    CustomerService customerService;


    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/CustomerReg")
    public String CustomerReg(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "CustomerReg";
    }

    @PostMapping("/Welcome")
    public String CustomerRegSubmit(@ModelAttribute("customer") @Valid CustomerDTO dto,
                                    BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            return "CustomerReg"; // visa formuläret igen
        }

        try {
            CustomerDTO savedDto = customerService.save(dto);
            model.addAttribute("name", savedDto.getName());
            model.addAttribute("email", savedDto.getEmail());
            model.addAttribute("phone", savedDto.getPhone());
            model.addAttribute("ssn", savedDto.getSsn());
            model.addAttribute("id", savedDto.getId());
        } catch (DataIntegrityViolationException e) {
            String message = e.getRootCause().getMessage();
            boolean errorFound = false;

            if (message.contains("uk_customer_name")) {
                result.rejectValue("name", "error.user", "Namnet finns redan i databasen");
                errorFound = true;
            }
            if (message.contains("uk_customer_ssn")) {
                result.rejectValue("ssn", "error.user", "Det personnumret finns redan i databasen");
                errorFound = true;
            }
            if (message.contains("uk_customer_email")) {
                result.rejectValue("email", "error.user", "Email finns redan i databasen");
                errorFound = true;
            }
            if (message.contains("uk_customer_phone")) {
                result.rejectValue("phone", "error.user", "Telefonnumret finns redan i databasen");
                errorFound = true;
            }

            if (!errorFound) {
                result.reject("error.user", "oväntat fel uppstod");

            }
            return "CustomerReg";
        }

        return "Welcome";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("customer", new CustomerDTO());
        return "CustomerReg"; // namnet på din HTML-sida
    }

    @Controller
    public class HomeController {

        @GetMapping("/Startsida")
        public String home() {
            return "startsida";
        }
    }

    @GetMapping("/Customerlist")
    public String listCustomers(Model model, @RequestParam(value = "error", required = false) String error) {
        List<CustomerDTO> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        if(error != null){
            model.addAttribute("errorMessage", error);
        }
        return "CustomerList";
    }

    @PostMapping("/deleteCustomer/{id}")
    public String deleteCustomer(@PathVariable Long id, Model model) {
        boolean customerHasBooking = customerService.customerHasBooking(id);
        if (customerHasBooking) {
            return "redirect:/html/Customerlist?error=Kunden har aktiva bokningar och kan inte tas bort.";
        } else {
            customerService.deleteById(id);
        return "redirect:/html/Customerlist";

        }

    }

    @PostMapping("/customer/edit/{id}")
    public String changeInformation(@PathVariable Long id, Model model) {
        Optional<CustomerDTO> customerDTO = Optional.of(new CustomerDTO());
        customerDTO = customerService.findById(id);
        if (customerDTO.isPresent()) {
            model.addAttribute("customer", customerDTO.get());
            return "editCustomer";
        } else {
            return "redirect:/html/Customerlist?error=kund%20hittades%20inte";
        }
    }

    @PostMapping("/customer/edit")
    public String saveEditCustomer(@Valid @ModelAttribute("customer") CustomerDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "editCustomer";
        }
        CustomerDTO updatedDto = customerService.save(dto);
        model.addAttribute("message", "kund updaterad");
        return "redirect:/html/Customerlist";
    }
}
