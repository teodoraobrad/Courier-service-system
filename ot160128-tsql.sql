



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


