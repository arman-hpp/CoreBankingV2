package com.bank.core.models.rules;

import com.bank.core.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Rules")
public class Rule extends BaseEntity {
    private String name;

    private String content;
}
