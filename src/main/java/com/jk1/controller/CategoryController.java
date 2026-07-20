package com.jk1.controller;

import com.jk1.dto.response.CategoryResponseDTO;
import com.jk1.mapper.CategoryMapper;
import com.jk1.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public String listCategories(Model model) {
        List<CategoryResponseDTO> categories = categoryService.findAll().stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        return "category/list";
    }
}
