package ga.inptic.scolarite_api.entity;

import jakarta.persistence.*;
 import  java.math.BigDecimal ;

@Entity 

@Table( name="niveau")

public class Niveau {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;


 @Column(nullable = false , unique = true , length = 10)
       private String code ;

 @Column(nullable = false , length = 50)
       private String libelle;

 @Column(name = "frais_scolarite", nullable = false, precision = 10, scale = 2)
 private  BigDecimal fraisScolarite;

 public Niveau(){

 }

 

 public Niveau(String code, String libelle, BigDecimal fraisScolarite) {
    this.code = code;
    this.libelle = libelle;
    this.fraisScolarite = fraisScolarite ;
}



    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public BigDecimal getFraisScolarite() { return fraisScolarite; }
    public void setFraisScolarite(BigDecimal fraisScolarite) { this.fraisScolarite = fraisScolarite; }
}
