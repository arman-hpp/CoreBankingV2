package com.bank.models.rules;

import com.bank.models.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Rules")
@SuppressWarnings("JpaDataSourceORMInspection")
public class Rule extends BaseEntity {
    private String name;

    private String content;
}
