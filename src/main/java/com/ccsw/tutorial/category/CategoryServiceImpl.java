package com.ccsw.tutorial.category;

import com.ccsw.tutorial.category.model.Category;
import com.ccsw.tutorial.category.model.CategoryDto;
import com.ccsw.tutorial.exceptions.NoIdFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ccsw
 *
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Category get(Long id) {

        return this.categoryRepository.findById(id).orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> findAll() {

        return (List<Category>) this.categoryRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Category save(Long id, CategoryDto dto) throws NoIdFoundException {

        Category category;

        if (id == null) {
            category = new Category();
        } else {
            category = this.get(id);
        }
        if (category == null) {
            throw new NoIdFoundException();
        }
        category.setName(dto.getName());

        this.categoryRepository.save(category);
        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws NoIdFoundException {
        if (this.get(id) == null) {
            throw new NoIdFoundException();
        }

        this.categoryRepository.deleteById(id);
    }

}
