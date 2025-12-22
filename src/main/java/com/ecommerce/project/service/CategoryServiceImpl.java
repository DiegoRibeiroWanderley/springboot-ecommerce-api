package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.mapper.CategoryMapper;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        List<Category> categories = categoryPage.getContent();

        if (categories.isEmpty()) {
            throw new APIException("No categories found");
        }

        List<CategoryDTO> categoryDTOS = categoryMapper.toDTOs(categories);

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());

        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());

        if (categoryFromDb != null) {
            throw new APIException("Category with the name "  + category.getCategoryName() + " already exists !!!");
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        Category category = categoryMapper.toEntity(categoryDTO);

        category.setCategoryId(categoryId);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(category);
        return categoryMapper.toDTO(category);
    }
}
