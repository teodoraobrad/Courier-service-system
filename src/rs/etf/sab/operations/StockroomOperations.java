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
public interface StockroomOperations {
    boolean	deleteStockroom​(int idStockroom);	//Brisanje magacina na osnovu primarnog kljuca Magacin se moze obrisati, ukoliko on postoji i prazan je.
    int	deleteStockroomFromCity​(int idCity);	//Brisanje magacina iz odredjenog grada Magacin se moze obrisati, ukoliko on postoji i prazan je.
    java.util.List<java.lang.Integer>	getAllStockrooms();	//Dohvatanje liste primarnih kljuceva svih magacina
    int	insertStockroom​(int address);	//Dodavanje novog magacina.
}
