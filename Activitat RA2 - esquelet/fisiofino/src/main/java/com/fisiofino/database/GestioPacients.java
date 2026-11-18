package com.fisiofino.database;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;

public class GestioPacients {

//Apartat 1: Insercions a partir del XML
    public int CreatePatient(Pacient p) throws SQLException {

        int idPacient = 0;
        String sql = "INSERT INTO Patient (given_name, family_name, nif, birth_date, gender, address, city, postal_code, country, telephone, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getCognoms());
            ps.setString(3, p.getDniNie());
            ps.setDate(4, p.getDataNaixement() != null ? Date.valueOf(p.getDataNaixement()) : null);
            ps.setString(5, p.getGenere());
            ps.setString(6, p.getAdreca());
            ps.setString(7, p.getCiutat());
            ps.setString(8, p.getCodiPostal());
            ps.setString(9, p.getPais());
            ps.setString(10, p.getTelefon());
            ps.setString(11, p.getMail());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idPacient = rs.getInt(1);
                    p.setIdPacient(idPacient);
                }
            }
        }
        
        return idPacient;
    }

    public boolean CreateMalaltia(MalaltiaPacient mp) throws SQLException {

        boolean malaltiaCreada = false;

        String sql = "INSERT INTO Disease (snomed_code, name_disease) VALUES (?, ?)";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, mp.getSnomedCode());
            ps.setString(2, mp.getNomMalaltia());

            if (ps.executeUpdate() > 0) {
                malaltiaCreada = true;
            }
        }
        
        return malaltiaCreada;
    }    

    public boolean CreateMalaltiaPacient(MalaltiaPacient mp) throws SQLException {

        boolean malaltiaPacientCreada = false;

        String sql = "INSERT INTO Patient_disease (id_patient, snomed_code, status, treatment) VALUES (?, ?, ?, ?)";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, mp.getIdPacient());
            ps.setString(2, mp.getSnomedCode());
            ps.setString(3, mp.getStatus());
            ps.setString(4, mp.getTractament());

            if (ps.executeUpdate() > 0) {
                malaltiaPacientCreada = true;
            }
        }
        
        return malaltiaPacientCreada;
    }    

//Apartat 2: Cerca de pacient per DNI
    public Pacient findBydniNie(String dni_nie) throws SQLException {
            
        Pacient p = null;

        String sql = "SELECT * FROM Patient WHERE nif = ?";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni_nie);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Pacient();
                    p.setIdPacient(rs.getInt("id_patient"));
                    p.setNom(rs.getString("given_name"));
                    p.setCognoms(rs.getString("family_name"));
                    p.setDniNie(rs.getString("nif"));
                    Date birthDate = rs.getDate("birth_date");
                    if (birthDate != null) {
                        p.setDataNaixement(birthDate.toLocalDate());
                    }
                    p.setGenere(rs.getString("gender"));
                    p.setAdreca(rs.getString("address"));
                    p.setCiutat(rs.getString("city"));
                    p.setCodiPostal(rs.getString("postal_code"));
                    p.setPais(rs.getString("country"));
                    p.setTelefon(rs.getString("telephone"));
                    p.setMail(rs.getString("email"));
                }
            }
        }

        return  p;
    }

//Apartat 3: Malalties del pacient
    public List<MalaltiaPacient> findDiseaseBydniNie(String dni_nie) throws SQLException {
        List<MalaltiaPacient> malaltiespacient = new ArrayList<MalaltiaPacient>();

        String sql = "SELECT pd.id_diagnosis, pd.id_patient, p.nif, pd.snomed_code, d.name_disease, pd.status, pd.treatment " +
                     "FROM Patient_disease pd " +
                     "JOIN Patient p ON pd.id_patient = p.id_patient " +
                     "JOIN Disease d ON pd.snomed_code = d.snomed_code " +
                     "WHERE p.nif = ?";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni_nie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MalaltiaPacient mp = new MalaltiaPacient();
                    mp.setIdDiagnosis(rs.getInt("id_diagnosis"));
                    mp.setIdPacient(rs.getInt("id_patient"));
                    mp.setDniPacient(rs.getString("nif"));
                    mp.setSnomedCode(rs.getString("snomed_code"));
                    mp.setNomMalaltia(rs.getString("name_disease"));
                    mp.setStatus(rs.getString("status"));
                    mp.setTractament(rs.getString("treatment"));
                    malaltiespacient.add(mp);
                }
            }
        }    

        return malaltiespacient;
    }

//Apartat 4: Llistar tots els pacients
    public List<Pacient> findAllPacients () throws SQLException {
        List<Pacient> pacients = new ArrayList<>();

        String sql = "SELECT * FROM Patient ORDER BY id_patient";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pacient p = new Pacient();
                p.setIdPacient(rs.getInt("id_patient"));
                p.setNom(rs.getString("given_name"));
                p.setCognoms(rs.getString("family_name"));
                p.setDniNie(rs.getString("nif"));
                Date birthDate = rs.getDate("birth_date");
                if (birthDate != null) {
                    p.setDataNaixement(birthDate.toLocalDate());
                }
                p.setGenere(rs.getString("gender"));
                p.setAdreca(rs.getString("address"));
                p.setCiutat(rs.getString("city"));
                p.setCodiPostal(rs.getString("postal_code"));
                p.setPais(rs.getString("country"));
                p.setTelefon(rs.getString("telephone"));
                p.setMail(rs.getString("email"));
                pacients.add(p);
            }
        }
        
        return pacients;
    }

//Apartat 5: Modificar l'estat d'una malaltia d'un pacient
    public boolean updateStatus (int idPacient, String snomedCode, String status) throws SQLException {
        boolean modificat = false;

        String sql = "UPDATE Patient_disease SET status = ? WHERE id_patient = ? AND snomed_code = ?";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, idPacient);
            ps.setString(3, snomedCode);

            if (ps.executeUpdate() > 0) {
                modificat = true;
            }
        }

        return  modificat;
    }

// funcions i mètodes accesoris i necessaris per saber si s'ha d'insertar o modificar    
    public boolean findMalaltia (String SnomedCode) throws SQLException {
        boolean esMalaltia = false;
        
        String sql = "SELECT 1 FROM Disease WHERE snomed_code = ?";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, SnomedCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    esMalaltia = true;
                }
            }
        }
        
        return esMalaltia;
    }

    public boolean findMalaltiaPacient (String dniPacient, String SnomedCode, String status) throws SQLException {
        boolean esMalaltiaPacient = false;

        String sql = "SELECT 1 FROM Patient_disease pd " +
                     "JOIN Patient p ON pd.id_patient = p.id_patient " +
                     "WHERE p.nif = ? AND pd.snomed_code = ? AND pd.status = ?";

        try (Connection con = Connexio.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dniPacient);
            ps.setString(2, SnomedCode);
            ps.setString(3, status);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    esMalaltiaPacient = true;
                }
            }
        }
        
        return esMalaltiaPacient;
    }


}
