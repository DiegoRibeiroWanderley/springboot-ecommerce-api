package com.ecommerce.project.mapper;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    Category toEntity(CategoryDTO categoryDTO);
    List<CategoryDTO> toDTOs(List<Category> categories);
}
