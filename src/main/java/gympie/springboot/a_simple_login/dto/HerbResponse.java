package gympie.springboot.a_simple_login.dto;

public record HerbResponse(Long id, String name, String botanicalName, String description, Long categoryId,
                           String categoryName) {
}
