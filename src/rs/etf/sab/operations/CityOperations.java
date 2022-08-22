/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.operations;

import java.util.List;

/**
 *
 * @author Teodora
 */
public interface CityOperations {
    boolean	deleteCity​(int idCity);	//Brisanje grada na osnovu primarnog kljuca
    int	deleteCity​(String... names);	//Brisanje grada po nazivu
    List<Integer>	getAllCities();	//Dohvatanje liste primarnih kljuceva svih gradova
    int	insertCity​(String name, String postalCode);	//Dodavanje grada
}
