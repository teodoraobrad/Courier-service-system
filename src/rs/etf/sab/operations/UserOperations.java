/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.operations;

/**
 *
 * @author Teodora
 */
public interface UserOperations {
    boolean	declareAdmin​(java.lang.String userName);	//Postaviti zadatok korisnika za admina Svaki korisnik moze postati admin, ukoliko vec nije admin.
    int	deleteUsers​(java.lang.String... userNames);	//Brisanje korisnika na osnovu korisickih imena
    java.util.List<java.lang.String>	getAllUsers();	//Dohvatanje liste svih korisnickih imena
    int	getSentPackages​(java.lang.String... userNames);	//Dohvatanje ukupan broj poslatih posiljki za korisnike sa zadatim korisnickim imenima
    boolean	insertUser​(java.lang.String userName, java.lang.String firstName, java.lang.String lastName, java.lang.String password, int idAddress);	//Kreiranje korisnika
}
