package com.kneu.medications.prescription; //where each folder or namespace lives in 
import java.time.LocalTime; //A time without a date and without a timezone 

import com.kneu.medications.Medication; //Since medication is in a different package, we need to import it to use it in this class

import jakarta.persistence.*;   //JPA annotations.

@Entity  //Tells Hibernate that this class maps to a database table so each object is one row.
@Table(name = "prescriptions", uniqueConstraints = @UniqueConstraint (columnNames = {"medication_id", "time_of_day"}))
public class Prescription {
    @Id //primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Generates the id.
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY, optional = false) //Many prescriptions can have the same medication.
    @JoinColumn (name = "medication_id")
    private Medication medication; //The medication that is prescribed.

    @Column(name = "time_of_day", nullable = false) //The time of day that the prescription is for.
    private LocalTime timeOfDay;

    @Column(length = 100)
    private String label; //A label for the prescription.

    protected Prescription() { //Default constructor for JPA. HB creates an empty object and then fills in the fields using reflection.
    }

    public Prescription(Medication medication, LocalTime timeOfDay, String label) { //Constructor for creating a new prescription.
        this.medication = medication;
        this.timeOfDay = timeOfDay;
        this.label = label;
    }

    public Long getId(){
        return id;
    }

    public Medication getMedication(){
        return medication;
    }

    public String getLabel(){
        return label;
    }

    public LocalTime getTimeOfDay(){
        return timeOfDay;
    }
}
