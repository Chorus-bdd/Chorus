package org.chorusbdd.chorus.stepinvoker.catalogue;

import org.chorusbdd.chorus.results.CataloguedStep;

import java.util.Objects;

/**
 * Created by nickebbutt on 15/03/2018.
 */
public class CatalogueKey {

    private final String category;
    private final boolean deprecated;
    private final String pattern;

    public CatalogueKey(CataloguedStep cataloguedStep) {
        Objects.requireNonNull(cataloguedStep.getCategory(), "category cannot be null");
        Objects.requireNonNull(cataloguedStep.getPattern(), "pattern cannot be null");
        this.category = cataloguedStep.getCategory();
        this.deprecated = cataloguedStep.isDeprecated();
        this.pattern = cataloguedStep.getPattern();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogueKey that = (CatalogueKey) o;

        if (deprecated != that.deprecated) return false;
        if (!category.equals(that.category)) return false;
        return pattern.equals(that.pattern);
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + (deprecated ? 1 : 0);
        result = 31 * result + pattern.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CatalogueKey{" +
                "category='" + category + '\'' +
                ", deprecated=" + deprecated +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
