package com.tidy.tidy.domain.space;

import com.tidy.tidy.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "space_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Space extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceType type;

    protected Space(SpaceType type) {
        this.type = type;
    }
}
