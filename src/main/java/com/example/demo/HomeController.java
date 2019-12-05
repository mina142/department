package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    CloudinaryConfig cloudc;


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
        model.addAttribute("users", userRepository.findAll());
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
                                      Model model,@RequestParam(name = "departmentId") long id,@RequestParam("file") MultipartFile file) {
        model.addAttribute("user", user);
        model.addAttribute("users", userRepository.findAll());

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
            try{
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                user.setUserPicture(uploadResult.get("url").toString());
                userRepository.save(user);
            }catch(IOException e){
                e.printStackTrace();
                return "registration";
            }
            //the following lines is the hard way to connect , but if you have cascade type = all you in all relationships you do not need to right all these codes

            user.setDepartment(departmentRepository.findById(id).get());
            userRepository.save(user);
            Department department = departmentRepository.findById(id).get();
            Collection<User> users = department.getUsers();
            users.add(user);
            department.setUsers(users);
            departmentRepository.save(department);

            return "index";
        }

    }

    @PostMapping("/searchlist")
    public String search(Model model, @RequestParam("search") String search) {
        model.addAttribute("departments", departmentRepository.findBynameContainingIgnoreCase(search));

        return "searchlist";
    }

    @GetMapping("/adddept")
    public String showDepartmentPage(Model model) {
        model.addAttribute("department", new Department());
        return "dept";
    }

    @PostMapping("/processdepartment")
    public String processDirector(@Valid @ModelAttribute Department department, BindingResult result){

        if(result.hasErrors()){
            return "dept";
        }
        departmentRepository.save(department);
        return "index";
    }


    @RequestMapping("/updateUser/{id}")
    public String updateMovie(@PathVariable("id") long id, Model model){
        model.addAttribute("movie" , userRepository.findById(id).get());
        return "registration";
    }
    @RequestMapping("/deleteUser/{id}")
    public String deleteMovie(@PathVariable("id") long id, Model model){
        userRepository.deleteById(id);
        return "redirect:/";
    }
}
