package gympie.springboot.dto;

public record HerbResponse(Long id, String name, String botanicalName, String description, Long categoryId,
                           String categoryName) {
}
