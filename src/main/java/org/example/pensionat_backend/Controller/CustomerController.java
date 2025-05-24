package org.example.pensionat_backend.Controller;

import jakarta.validation.Valid;
import org.example.pensionat_backend.DTO.CustomerDTO;
import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.example.pensionat_backend.Service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        CustomerDTO savedDto = customerService.save(dto);
        model.addAttribute("name", savedDto.getName());
        model.addAttribute("email", savedDto.getEmail());
        model.addAttribute("phone", savedDto.getPhone());
        model.addAttribute("ssn", savedDto.getSsn());
        model.addAttribute("id", savedDto.getId());

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
    public String listCustomers(Model model) {
        List<CustomerDTO> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "CustomerList";
    }

    @PostMapping("/deleteCustomer/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteById(id);
        if (!deleted) {
            return "redirect:/html/Customerlist?error=Kunden har aktiva bokningar och kan inte tas bort.";
        }
        return "redirect:/html/Customerlist";

//    @GetMapping("/Welcome")
//    public String Welcome() {
//        return "Welcome";
//    }
    }
}
