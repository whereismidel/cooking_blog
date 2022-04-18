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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/recipes")
    public String recipes(Model model, Authentication auth) {
        Iterable<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);

        if (auth != null){

            User user = userRepository.findByUsername(auth.getName());
            String role = auth.getAuthorities().iterator().next().toString();

            model.addAttribute("user", user);
            model.addAttribute("role", role);
        }

        return "recipes";
    }

    @GetMapping("/recipes/new-recipe")
    public String createRecipe(Model model, Authentication auth) {

        if (auth != null){
            User user = userRepository.findByUsername(auth.getName());
            String role = auth.getAuthorities().iterator().next().toString();

            model.addAttribute("user", user);
            model.addAttribute("role", role);
        }

        return "new-recipe";
    }

    @PostMapping("/recipes/new-recipe")
    public String addNewRecipe(@RequestParam String title, @RequestParam String description, @RequestParam String ingredient, @RequestParam String fullText, Authentication auth) {
        fullText = fullText.replace("\n", "<br>");
        Recipe recipe = new Recipe(title, description, ingredient, fullText, auth.getName());
        recipeRepository.save(recipe);

        return "redirect:/recipes";
    }

    @GetMapping("/recipes/{id}")
    public String getRecipe(@PathVariable(value = "id") long id, Model model, Authentication auth){
        if(!recipeRepository.existsById(id)) {
            return "redirect:/recipes";
        }
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        assert recipe != null;
        recipe.setViews(recipe.getViews() + 1);
        recipeRepository.save(recipe);

        model.addAttribute("recipe", recipe);
        model.addAttribute("username", auth.getName());
        String role = auth.getAuthorities().iterator().next().toString();
        model.addAttribute("role", role);

        User user = userRepository.findByUsername(auth.getName());

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        return "recipe-page";
    }

    @GetMapping("/recipes/{id}/edit")
    public String editMenuRecipe(@PathVariable(value = "id") long id, Model model, Authentication auth){
        if(!recipeRepository.existsById(id)) {
            return "redirect:/recipes";
        }

        Recipe recipe = recipeRepository.findById(id).orElse(null);
        assert recipe != null;
        if (!recipe.getAuthor().equals(auth.getName()) && !auth.getAuthorities().iterator().next().toString().equals("ADMIN")){
            return "redirect:/recipes";
        }

        User user = userRepository.findByUsername(auth.getName());
        String role = auth.getAuthorities().iterator().next().toString();

        model.addAttribute("user", user);
        model.addAttribute("role", role);

        model.addAttribute(recipe);

        return "recipe-edit";
    }

    @PostMapping("/recipes/{id}/edit")
    public String editRecipe(@PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String description, @RequestParam String ingredient, @RequestParam String fullText, Authentication auth) {
        if(!recipeRepository.existsById(id)) {
            return "redirect:/recipes";
        }

        Recipe recipe = recipeRepository.findById(id).orElse(null);
        assert recipe != null;
        if (!recipe.getAuthor().equals(auth.getName()) && !auth.getAuthorities().iterator().next().toString().equals("ADMIN")){
            return "redirect:/recipes";
        }

        recipe.setTitle(title);
        recipe.setDescription(description);
        recipe.setIngredients(ingredient);

        fullText = fullText.replace("\n", "<br>");
        recipe.setFull_text(fullText);

        recipeRepository.save(recipe);

        return "redirect:/recipes/"+id;
    }

    @GetMapping("/recipes/{id}/remove")
    public String removeRecipe(@PathVariable(value = "id") long id, Authentication auth){
        if(!recipeRepository.existsById(id)) {
            return "redirect:/recipes";
        }
        Recipe recipe = recipeRepository.findById(id).orElse(null);
        assert recipe != null;
        if (!recipe.getAuthor().equals(auth.getName()) && !auth.getAuthorities().iterator().next().toString().equals("ADMIN")){
            return "redirect:/recipes";
        }

        recipeRepository.deleteById(id);

        return "redirect:/recipes";
    }

}
