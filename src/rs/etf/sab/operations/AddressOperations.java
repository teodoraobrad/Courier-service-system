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
public interface AddressOperations {
    int	deleteAddresses​(String name, int number); //Brisanje adresa po nazivu ulice i broju
    boolean	deleteAdress​(int idAddress);	//Brisanje adrese na osnovu primarnog kljuca
    int	deleteAllAddressesFromCity​(int idCity);	//Brisanje svih adresa iz zadatog grada
    List<Integer>	getAllAddresses();	//Dohvatanje liste primarnih kljuceva svih adresa
    List<Integer>	getAllAddressesFromCity​(int idCity);	//Dohvatanje liste primarnih kljuceva svih adresa koje se nalaze u zadatom gradu
    int	insertAddress​(java.lang.String street, int number, int cityId, int xCord, int yCord);	//Dodavanje nove adrese
}
