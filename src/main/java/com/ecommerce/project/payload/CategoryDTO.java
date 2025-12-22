package com.ecommerce.project.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {

    private Long categoryId;

    @NotBlank(message = "Must not be blank")
    @Size(min = 3, max = 100, message = "Category name size must be between 3 and 100")
    private String categoryName;
}
