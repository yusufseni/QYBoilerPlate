package com.yoesoff.plate.entity.social;

import com.yoesoff.plate.entity.BaseEntity;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseSocialRelationship extends BaseEntity {

    /**
     * Template method for validating relationship-specific business rules.
     * Subclasses should override this method to implement their validation logic.
     */
    protected abstract void validateRelationship();

    /**
     * Template method for checking if the relationship is currently active/valid.
     * @return true if the relationship is active, false otherwise
     */
    public abstract boolean isActive();
}
