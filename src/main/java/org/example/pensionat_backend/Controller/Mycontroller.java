package org.example.pensionat_backend.Controller;

import org.example.pensionat_backend.Models.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//det här är bara för att jag skulle testa :)
@Controller
@RequestMapping("/html")
public class Mycontroller {
    @GetMapping("/CustomerReg")
    public String CustomerReg(Model model) {
        model.addAttribute("user", new Customer());
        return "CustomerReg";
    }

    @PostMapping("/Welcome")
    public String CustomerRegSubmit(@RequestParam String name, @RequestParam(required = false) String email, @RequestParam(required = false) String phone, Model model,  RedirectAttributes redirectAttributes) {
//redirectAttributes.addFlashAttribute("name", name);
model.addAttribute("name", name);
return "Welcome.html";
    }

//    @GetMapping("/Welcome")
//    public String Welcome() {
//        return "Welcome";
//    }
}
