package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
            //if we want to departmnet to show in our secure page we need to creat one in dataloader
    DepartmentRepository departmentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception{
        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));
        Role adminRole = roleRepository.findByRole("ADMIN");
        Role userRole = roleRepository.findByRole("USER");
        Department dpt = new Department();
        dpt.setName("cyber");
        departmentRepository.save(dpt);




        User user = new User("jim@jim.com" , "password", "jim", "jimmerson" , true, "jim",dpt);
        user.setRoles(Arrays.asList(userRole));
        userRepository.save(user);

        user=new User("admin@admin.com", "password", "Admin", "User",true, "admin",dpt);
        user.setRoles(Arrays.asList(adminRole));
        userRepository.save(user);
    }

}
