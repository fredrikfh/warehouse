# Tilbakemelding på innlevering 2

## Bygging

- bygget greit

## Dokumentasjon

- gode brukerhistorier

## Design

- enkelt og funksjonelt, likte spesielt - og +-knappene

## Kodegjennomgang

### Item

- velg egnede datatyper, prøv å finne andre enn String hvis data har spesiell struktur, og Double brukes kun hvis null er en relevant mulig verdi (ser ikke helt at null er viktig her, ofte er -1 en grei verdi for "ikke satt")
- også kanskje mulig å kombinere flere av dem, som rad og hylle, og lengde og bredde, hvis de stort sett settes sammen (der kan record-klasser være nyttig, nytt i java 16)
- hvorfor trengs id?

### Warehouse

- getAllItemsSorted: bra bruk av ny switch-syntaks!

### User

- hvorfor er ikke feltene private?

### WarehouseTest

- testing av lyttere er enklere med mocking!

### WarehouseSerializer

- ObjectMapper-objektet er nokså dyrt og er laget for å kunne brukes mange ganger, så ikke lag et nytt i hver metode

### WarehouseSerializerTest

- klarer ikke å se at dere tester det spesifikke formatet noe sted

### InputValidator

- bruk gjerne @FunctionalInterface for å tydeliggjøre at det ikke er noen side-effekt

### ItemElementAnchorPane

- kunne brukt fxml her, og fx:root-teknikken

### WarehouseController

- tror et ListView<Item> ville vært bedre, spesielt hvis lista blir lang, selv om det blir litt mer komplisert med ListCell-elementer
