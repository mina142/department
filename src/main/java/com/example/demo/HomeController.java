package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model) {
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUserName(username));
        return "secure";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments",departmentRepository.findAll());

        return "registration";
    }


    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("user") User user, BindingResult result,
                                      Model model) {
        model.addAttribute("user", user);

        if (result.hasErrors()) {
            return "registration";
        } else {
            if (userService.countByUsername(user.getUserName()) > 0) {
                model.addAttribute("message", "username already exists");
                return "registration";
            }
            if (userService.countByEmail(user.getEmail()) > 0) {
                model.addAttribute("message", "this email has already been used to register");
                return "registration";
            }

            userService.saveUser(user);
            model.addAttribute("message", "User Acount Created");
        }
        return "index";
    }

    @GetMapping("/adddept")
    public String showDepartmentPage(Model model) {
        model.addAttribute("department", new Department());
        return "dept";
    }

    @PostMapping("/processDepartment")
    public String processDirector(@Valid @ModelAttribute Department department, BindingResult result){
        if(result.hasErrors()){
            return "dept";
        }
        departmentRepository.save(department);
        return "redirect:/";
    }


    @RequestMapping("/updateUser/{id}")
    public String updateMovie(@PathVariable("id") long id, Model model){
        model.addAttribute("movie" , userRepository.findById(id).get());
        return "MovieForm";
    }
    @RequestMapping("/deleteUser/{id}")
    public String deleteMovie(@PathVariable("id") long id, Model model){
        userRepository.deleteById(id);
        return "redirect:/";
    }
}



