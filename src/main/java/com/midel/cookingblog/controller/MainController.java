package com.midel.cookingblog.controller;

import com.midel.cookingblog.models.Recipe;
import com.midel.cookingblog.models.User;
import com.midel.cookingblog.repo.RecipeRepository;
import com.midel.cookingblog.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.ArrayList;
import java.util.Collections;


@Controller
public class MainController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String main(Model model, Authentication auth) {
        if (recipeRepository.count()>=5) {
            ArrayList<Recipe> recipes = (ArrayList<Recipe>) recipeRepository.findAll();
            Collections.shuffle(recipes);
            model.addAttribute("recipes", recipes.subList(0, 5));
        } else {
            model.addAttribute("recipes", "empty");
        }
        if (auth != null){
            User user = userRepository.findByUsername(auth.getName());
            String role = auth.getAuthorities().iterator().next().toString();

            model.addAttribute("user", user);
            model.addAttribute("role", role);
        }
        return "main";
    }

}
