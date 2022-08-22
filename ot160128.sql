
--DROP TABLE [Admin]
--go

--DROP TABLE [Ponuda]
--go

--DROP TABLE [Paket]
--go

--DROP TABLE [Zahtev]
--go

--DROP TABLE [Ruta]
--go

--DROP TABLE [Voznja]
--go

--DROP TABLE [Vozilo]
--go

--DROP TABLE [Magacin]
--go

--DROP TABLE [Kurir]
--go

--DROP TABLE [Korisnik]
--go

--DROP TABLE [Adresa]
--go

--DROP TABLE [Grad]
--go

CREATE TABLE [Admin]
( 
	[KorisnickoIme]      varchar(100)  NOT NULL 
)
go

CREATE TABLE [Adresa]
( 
	[IdA]                integer  IDENTITY  NOT NULL ,
	[Ulica]              varchar(100)  NOT NULL ,
	[Broj]               integer  NOT NULL 
	CONSTRAINT [Nula_55414929]
		 DEFAULT  0,
	[X]                  integer  NOT NULL 
	CONSTRAINT [Nula_1272303061]
		 DEFAULT  0,
	[Y]                  integer  NOT NULL 
	CONSTRAINT [Nula_1272303060]
		 DEFAULT  0,
	[IdG]                integer  NOT NULL 
)
go

CREATE TABLE [Grad]
( 
	[IdG]                integer  IDENTITY  NOT NULL ,
	[Naziv]              varchar(100)  NOT NULL ,
	[PostanskiBroj]      varchar(100)  NULL 
)
go

CREATE TABLE [Korisnik]
( 
	[KorisnickoIme]      varchar(100)  NOT NULL ,
	[Ime]                varchar(100)  NOT NULL ,
	[Prezime]            varchar(100)  NOT NULL ,
	[Sifra]              varchar(100)  NOT NULL ,
	[IdA]                integer  NOT NULL 
)
go

CREATE TABLE [Kurir]
( 
	[KorisnickoIme]      varchar(100)  NOT NULL ,
	[Status]             integer  NOT NULL 
	CONSTRAINT [Nula_430418619]
		 DEFAULT  0
	CONSTRAINT [NulaJedan_219094226]
		CHECK  ( [Status]=0 OR [Status]=1 ),
	[Profit]             decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_664378298]
		 DEFAULT  0,
	[BrojPaketa]         integer  NOT NULL 
	CONSTRAINT [Nula_1828525816]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_1718452088]
		CHECK  ( BrojPaketa >= 0 ),
	[BrojDozvole]        varchar(100)  NOT NULL 
)
go

CREATE TABLE [Magacin]
( 
	[IdA]                integer  NOT NULL 
)
go

CREATE TABLE [Paket]
( 
	[IdP]                integer  IDENTITY  NOT NULL ,
	[Tip]                integer  NOT NULL 
	CONSTRAINT [Nula_321601847]
		 DEFAULT  0
	CONSTRAINT [TipPaketa_1968530725]
		CHECK  ( [Tip]=0 OR [Tip]=1 OR [Tip]=2 OR [Tip]=3 ),
	[Tezina]             decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_782471315]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_118819348]
		CHECK  ( Tezina >= 0 ),
	[Status]             integer  NOT NULL 
	CONSTRAINT [Nula_363763380]
		 DEFAULT  0
	CONSTRAINT [StatusPaketa_75083639]
		CHECK  ( [Status]=0 OR [Status]=1 OR [Status]=2 OR [Status]=3 OR [Status]=4 ),
	[Cena]               decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_213499770]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_1446707752]
		CHECK  ( Cena >= 0 ),
	[VremeKreiranja]     datetime  NULL ,
	[VremePrihvatanja]   datetime  NULL ,
	[KorisnickoImePos]   varchar(100)  NOT NULL ,
	[IdAPre]             integer  NOT NULL ,
	[IdADos]             integer  NOT NULL ,
	[IdVoz]              integer  NULL ,
	[Mag]                integer  NOT NULL 
	CONSTRAINT [Nula_321141038]
		 DEFAULT  0
	CONSTRAINT [NulaJedan_1986162741]
		CHECK  ( [Mag]=0 OR [Mag]=1 ),
	[IdATre]             integer  NULL 
)
go

CREATE TABLE [Ponuda]
( 
	[Cena]               decimal(10,3)  NOT NULL ,
	[IdP]                integer  NOT NULL 
)
go

CREATE TABLE [Ruta]
( 
	[RBR]                integer  NOT NULL 
	CONSTRAINT [Pozitivno_2108202645]
		CHECK  ( RBR >= 0 ),
	[IdVoz]              integer  NOT NULL ,
	[IdA]                integer  NOT NULL ,
	[Preuzima]           integer  NOT NULL 
	CONSTRAINT [Nula_1808184217]
		 DEFAULT  0
	CONSTRAINT [NulaJedan_739769124]
		CHECK  ( [Preuzima]=0 OR [Preuzima]=1 )
)
go

CREATE TABLE [Vozilo]
( 
	[IdV]                integer  IDENTITY  NOT NULL ,
	[RegBr]              varchar(100)  NOT NULL ,
	[Gorivo]             integer  NOT NULL 
	CONSTRAINT [Nula_2084723543]
		 DEFAULT  0
	CONSTRAINT [Gorivo_1431178458]
		CHECK  ( [Gorivo]=0 OR [Gorivo]=1 OR [Gorivo]=2 ),
	[Potrosnja]          decimal(10,3)  NULL 
	CONSTRAINT [Pozitivno_2125230976]
		CHECK  ( Potrosnja >= 0 ),
	[Nosivost]           decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_1916922339]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_563778675]
		CHECK  ( Nosivost >= 0 ),
	[IdAmag]             integer  NULL 
)
go

CREATE TABLE [Voznja]
( 
	[KorisnickoIme]      varchar(100)  NOT NULL ,
	[Distanca]           decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_1857815559]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_529038932]
		CHECK  ( Distanca >= 0 ),
	[BrojIsporucenihPaketa] integer  NOT NULL 
	CONSTRAINT [Nula_314097398]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_1429896750]
		CHECK  ( BrojIsporucenihPaketa >= 0 ),
	[IdVoz]              integer  IDENTITY  NOT NULL ,
	[Profit]             decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_2125242550]
		 DEFAULT  0,
	[Nosi]               decimal(10,3)  NOT NULL 
	CONSTRAINT [Nula_391356571]
		 DEFAULT  0
	CONSTRAINT [Pozitivno_1222307332]
		CHECK  ( Nosi >= 0 ),
	[Gotova]             integer  NOT NULL 
	CONSTRAINT [Nula_2085247840]
		 DEFAULT  0
	CONSTRAINT [NulaJedan_453575228]
		CHECK  ( [Gotova]=0 OR [Gotova]=1 ),
	[IdV]                integer  NOT NULL 
)
go

CREATE TABLE [Zahtev]
( 
	[BrojDozvole]        varchar(100)  NOT NULL ,
	[KorisnickoIme]      varchar(100)  NOT NULL 
)
go

ALTER TABLE [Admin]
	ADD CONSTRAINT [XPKAdmin] PRIMARY KEY  CLUSTERED ([KorisnickoIme] ASC)
go

ALTER TABLE [Adresa]
	ADD CONSTRAINT [XPKAdresa] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XPKGrad] PRIMARY KEY  CLUSTERED ([IdG] ASC)
go

ALTER TABLE [Grad]
	ADD CONSTRAINT [XAK1Grad] UNIQUE ([PostanskiBroj]  ASC)
go

ALTER TABLE [Korisnik]
	ADD CONSTRAINT [XPKKorisnik] PRIMARY KEY  CLUSTERED ([KorisnickoIme] ASC)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XPKKurir] PRIMARY KEY  CLUSTERED ([KorisnickoIme] ASC)
go

ALTER TABLE [Kurir]
	ADD CONSTRAINT [XAK1Kurir] UNIQUE ([BrojDozvole]  ASC)
go

ALTER TABLE [Magacin]
	ADD CONSTRAINT [XPKMagacin] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [XPKPaket] PRIMARY KEY  CLUSTERED ([IdP] ASC)
go

ALTER TABLE [Ponuda]
	ADD CONSTRAINT [XPKPonuda] PRIMARY KEY  CLUSTERED ([IdP] ASC)
go

ALTER TABLE [Ruta]
	ADD CONSTRAINT [XPKRuta] PRIMARY KEY  CLUSTERED ([RBR] ASC,[IdVoz] ASC)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XPKVozilo] PRIMARY KEY  CLUSTERED ([IdV] ASC)
go

ALTER TABLE [Vozilo]
	ADD CONSTRAINT [XAK1Vozilo] UNIQUE ([RegBr]  ASC)
go

ALTER TABLE [Voznja]
	ADD CONSTRAINT [XPKVoznja] PRIMARY KEY  CLUSTERED ([IdVoz] ASC)
go

ALTER TABLE [Zahtev]
	ADD CONSTRAINT [XPKZahtev] PRIMARY KEY  CLUSTERED ([KorisnickoIme] ASC)
go


ALTER TABLE [Admin]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([KorisnickoIme]) REFERENCES [Korisnik]([KorisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Adresa]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdG]) REFERENCES [Grad]([IdG])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Korisnik]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdA]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Kurir]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([KorisnickoIme]) REFERENCES [Korisnik]([KorisnickoIme])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Magacin]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IdA]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Paket]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([KorisnickoImePos]) REFERENCES [Korisnik]([KorisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdAPre]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdADos]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_27] FOREIGN KEY ([IdVoz]) REFERENCES [Voznja]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Paket]
	ADD CONSTRAINT [R_29] FOREIGN KEY ([IdATre]) REFERENCES [Magacin]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Ponuda]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([IdP]) REFERENCES [Paket]([IdP])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Ruta]
	ADD CONSTRAINT [R_25] FOREIGN KEY ([IdVoz]) REFERENCES [Voznja]([IdVoz])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Ruta]
	ADD CONSTRAINT [R_26] FOREIGN KEY ([IdA]) REFERENCES [Adresa]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Vozilo]
	ADD CONSTRAINT [R_28] FOREIGN KEY ([IdAmag]) REFERENCES [Magacin]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Voznja]
	ADD CONSTRAINT [R_21] FOREIGN KEY ([KorisnickoIme]) REFERENCES [Kurir]([KorisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Voznja]
	ADD CONSTRAINT [R_24] FOREIGN KEY ([IdV]) REFERENCES [Vozilo]([IdV])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Zahtev]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([KorisnickoIme]) REFERENCES [Korisnik]([KorisnickoIme])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go




CREATE FUNCTION euklidskaDistanca
(
	@idP int, @idD int
	
)
RETURNS float
AS
BEGIN
	
	DECLARE @ret float, @x1 float,@y1 float,@x2 float,@y2 float

	
	select @x1=X,@y1=Y from Adresa where IdA=@idP
	select @x2=X,@y2=Y from Adresa where IdA=@idD

	set @ret = SQRT(POWER(@x1-@x2,2)+POWER(@y1-@y2,2)) 

	
	RETURN @ret

END
GO


CREATE TRIGGER JednoVoziloJedanKurir
   ON  Voznja
   AFTER INSERT
AS 
BEGIN
	declare @kursor cursor
	declare @korisnicko varchar(100)
	declare @idv int
	declare @uslov int
	declare @gotova int

	declare @kursorpom cursor
	declare @korisnickopom varchar(100)
	declare @pom int
	declare @idvoz int

	set @kursor=cursor for select IdVoz, KorisnickoIme, IdV from inserted
	
	open @kursor
	fetch next from @kursor into @idvoz, @korisnicko, @idv
	
	while @@FETCH_STATUS=0
	begin

		select @uslov=count(*) from Voznja where (IdV=@idv and Gotova=0 and IdVoz!=@idvoz) or (KorisnickoIme=@korisnicko and Gotova=0 and IdVoz!=@idvoz)
	

		if ( @uslov =0)
		begin 
		print('Vozio je uspesno preuzeto iz magacina')
		end
		else begin 
		print('Vozio se vec koristi')
		rollback transaction
		end

		fetch next from @kursor into @idvoz, @korisnicko, @idv

	end

	close @kursor
	deallocate @kursor

END
GO



CREATE TRIGGER JednoVoziloJedanKurir1
   ON  Voznja
   AFTER UPDATE
AS 
BEGIN
	declare @kursor cursor
	declare @korisnicko varchar(200)
	declare @idv int
	declare @uslov int
	declare @idvoz int

	declare @kursorpom cursor
	declare @korisnickopom varchar(200)
	declare @pom int

	set @kursor=cursor for select IdVoz,KorisnickoIme, IdV from inserted 
	
	open @kursor
	fetch next from @kursor into @idvoz, @korisnicko, @idv
	
	while @@FETCH_STATUS=0
	begin

		  select @uslov=count(*) from Voznja where (IdV=@idv and IdVoz!=@idvoz and Gotova=0) or (KorisnickoIme=@korisnicko and IdVoz!=@idvoz and Gotova=0) 
		

		if ( @uslov = 0)
		begin 
		print('ok')
		
		end
		else begin 
		print('Vozio se vec koristi')
		
		rollback transaction
		end


		fetch next from @kursor into @idvoz, @korisnicko, @idv

	end

	close @kursor
	deallocate @kursor

END
GO

create TRIGGER TR_TransportOffer_Update
   ON  Paket
   AFTER UPDATE
AS 
BEGIN
	
	declare @cena decimal(10,3)
	declare @IdP int
	declare @Tip int
	declare @Tezina decimal(10,3)
	declare @IdAPre int
	declare @IdADos int
	declare @Status int
	declare @kursor cursor
	declare @distanca decimal(10,3)
	declare @pocetna decimal(10,3)
	declare @pokg decimal(10,3)
	declare @stara decimal(10,3)
	
	set @kursor= cursor for select i.IdP,i.Tip,i.Tezina,i.IdAPre,i.IdADos,i.Status from inserted i join deleted d on i.IdP=d.IdP
	
	open @kursor
	fetch next from @kursor
	into @IdP, @Tip, @Tezina, @IdAPre, @IdADos,@Status

	while @@FETCH_STATUS=0
	begin

	if (@Status = 0 )--zahtev kreiran
	begin
		
		SELECT @distanca = dbo.euklidskaDistanca (@IdAPre,@IdADos)

		set @pocetna= case @Tip when 0 then 115 when 1 then 175 when 2 then 250 when 3 then 350 else 0 end
		set @pokg= case @Tip when 0 then 0 when 1 then 100 when 2 then 100 when 3 then 500 else 0 end
		set @cena = (@pocetna+@Tezina*@pokg)*@distanca

		update Ponuda set Cena=@cena where IdP=@IdP 

	
	end
	


	fetch next from @kursor
	into @IdP, @Tip, @Tezina, @IdAPre, @IdADos,@Status

	end

	close @kursor
	deallocate @kursor

	

END
go

CREATE TRIGGER TR_TransportOffer
   ON  Paket
   AFTER INSERT
AS 
BEGIN
	--declare @statusZ int
	declare @cena decimal(10,3)
	declare @IdP int
	declare @Tip int
	declare @Tezina decimal(10,3)
	declare @IdAPre int
	declare @IdADos int
	declare @Status int
	declare @kursor cursor
	declare @distanca decimal(10,3)
	declare @pocetna decimal(10,3)
	declare @pokg decimal(10,3)

	--CenaIsporuke= (OSNOVNA_CENA[i] + weight * CENA_PO_KG[i] ) * euklidska_distanca
	set @kursor= cursor for select IdP,Tip,Tezina,IdAPre,IdADos,Status from inserted 
	--set @statusZ=0
	open @kursor
	fetch next from @kursor
	into @IdP, @Tip, @Tezina, @IdAPre, @IdADos,@Status

	while @@FETCH_STATUS=0
	begin

	if @Status = 0 --zahtev kreiran
	begin
		
		SELECT @distanca = dbo.euklidskaDistanca (@IdAPre,@IdADos)

		set @pocetna= case @Tip when 0 then 115 when 1 then 175 when 2 then 250 when 3 then 350 else 0 end
		set @pokg= case @Tip when 0 then 0 when 1 then 100 when 2 then 100 when 3 then 500 else 0 end
		set @cena = (@pocetna+@Tezina*@pokg)*@distanca
		insert into Ponuda(Cena,IdP) values(@cena, @IdP)

	
	end
	


	fetch next from @kursor
	into @IdP, @Tip, @Tezina, @IdAPre, @IdADos,@Status

	end

	close @kursor
	deallocate @kursor

	

END
GO


