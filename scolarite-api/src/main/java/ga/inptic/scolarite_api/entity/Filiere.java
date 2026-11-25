package ga.inptic.scolarite_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "filiere")
public class Filiere {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , unique = true , length = 20 )
    private String code ;

    @Column(nullable = false, length = 100)
    private String libelle;

    public Filiere(){

    }

    public Filiere(String code,String libelle){
        this.code = code ;
        this.libelle = libelle ;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
