package lv.bootcamp.shelter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.OptionalInt;

@Getter
@Setter
@AllArgsConstructor
public class Animal {

    private final String name;
    private final String species;
    private final OptionalInt age;
    private final boolean vaccinated;
    private final LocalDate intakeDate;

}
