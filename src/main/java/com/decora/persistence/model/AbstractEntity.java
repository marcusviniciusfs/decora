package com.decora.persistence.model;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.beanutils.BeanUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

@MappedSuperclass
public abstract class AbstractEntity {

    public static final int ID_GENERATOR_ALLOCATION_SIZE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "AbstractEntityGenerator")
    @TableGenerator(name = "AbstractEntityGenerator", allocationSize = ID_GENERATOR_ALLOCATION_SIZE)
    @ApiModelProperty(readOnly = true)
    private Integer id;

    public final <T extends AbstractEntity> T copy(final T entity) throws ReflectiveOperationException {
        BeanUtils.copyProperties(this, entity);
        return (T) this;
    }

    public final Integer getId() {
        return id;
    }

    protected final void setId(final int value) {
        id = value;
    }

    final ConstraintCheckResult validate() {
        final ConstraintCheckResult.Builder builder = new ConstraintCheckResult.Builder();
        checkConstraint(builder);
        return builder.build();
    }

    protected void checkConstraint(final ConstraintCheckResult.Builder builder) {
    }
}
