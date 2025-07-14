package ru.practicum.explore.server.Category.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explore.server.Category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
