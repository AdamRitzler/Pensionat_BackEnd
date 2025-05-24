package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.Models.Customer;
import org.example.pensionat_backend.Repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//det här är bara för att jag skulle testa :)
@Controller
@RequestMapping("/html")
public class CustomerController {
    CustomerRepository customerRepository;
    public CustomerController(CustomerRepository customerRepo) {
        this.customerRepository = customerRepo;
    }

    Customer customer = new Customer();

    @GetMapping("/CustomerReg")
    public String CustomerReg(Model model) {
        model.addAttribute("user", new Customer());
        return "CustomerReg";
    }

    @PostMapping("/Welcome")
    public String CustomerRegSubmit(@RequestParam String name, @RequestParam(required = false) String email, @RequestParam(required = false) String phone, Model model) {
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);

        customer.setName(name);
        customer.setEmail(email);
        customer.setPhone(phone);

        customerRepository.save(customer);

        return "Welcome";
    }
    @Controller
    public class HomeController {

        @GetMapping("/Startsida")
        public String home() {
            return "startsida";
        }
    }

//    @GetMapping("/Welcome")
//    public String Welcome() {
//        return "Welcome";
//    }
}
