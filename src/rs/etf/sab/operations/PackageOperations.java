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
public interface PackageOperations {
    boolean	acceptAnOffer​(int packageId);	//Prihvatanje ponude.
    boolean	changeType​(int packageId, int newType);	//Promena tipa paketa Paket moze promeniti tip, ukoliko je u statusu kreiran.
    boolean	changeWeight​(int packageId, java.math.BigDecimal newWeight);	//Promena tezine paketa Paket se moze obrisati, ukoliko je u statusu kreiran.
    boolean	deletePackage​(int packageId);	//Brisanje paketa.
    java.sql.Date	getAcceptanceTime​(int packageId);	//Dohvatanje vremena prihvatanja ponude
    java.util.List<java.lang.Integer>	getAllPackages();	//Dohvatanje liste svih paketa
    java.util.List<java.lang.Integer>	getAllPackagesCurrentlyAtCity​(int cityId)	;//Dohvatanje liste svih paketa koji se trenutno nalaze u nekom gradu.
    java.util.List<java.lang.Integer>	getAllPackagesWithSpecificType​(int type);	//Dohvatanje liste svih paketa odredjenog tipa
    java.util.List<java.lang.Integer>	getAllUndeliveredPackages();	//Dohvatanje liste svih neisporucenih paketa.
    java.util.List<java.lang.Integer>	getAllUndeliveredPackagesFromCity​(int cityId);	//Dohvatanje liste svih neisporucenih paketa koji se salju iz odredjenog grada Paketi su neisporuceni ukoliko su ponude za njihovo slanje prihvaceni ili su paketi vec preuzeti, ali oni i dalje nisu isporuceni.
    int	getCurrentLocationOfPackage​(int packageId);	//Dohvatanje trenutne lokacije paketa
    int	getDeliveryStatus​(int packageId);	//Dohvatanje statusa posiljke
    java.math.BigDecimal	getPriceOfDelivery​(int packageId);	//Dohvatanje cene isporuke
    int	insertPackage​(int addressFrom, int addressTo, java.lang.String userName, int packageType, java.math.BigDecimal weight);	//Kreiranje zahteva za slanje paketa Podrazumevana velicina paketa je 10
    boolean	rejectAnOffer​(int packageId);	//Odbijanje ponude.
}
