# Fakturomat

Jest to aplikacja konsolowa, która bierze 3 przykładowe komputery z nazwą, datą księgowania oraz ceną w dolarach.
Następnie Fakturomat pobiera z API NBP średni kurs dolara z dnia księgowania (a jeśli NBP nie publikuje w tym dniu kursu, 
z ostatniego wcześniejszego dnia, w którym był dostępny, maksymalnie do tygodnia wstecz) i przelicza cenę na złotówki.
Po przeliczeniu zapisuje do bazy danych kupione komputery i wystawia na ich podstawie fakturę w formie pliku "faktura.xml" w głównym katalogu.
Gdy cała procedura dobiegnie końca, aplikacja daje opcję przeglądania zakupionych komputerów, wyszukiwania ich po 
nazwie oraz sortowania po nazwie lub dacie księgowania (rosnąco i malejąco).

## Wymagania

- JDK 21
- dostęp do internetu

## Uruchomienie

Rozpakuj plik zip lub sklonuj repozytorium:

```bash
git clone https://github.com/Sekanko/fakturomat.git
```

### 1. Otwórz terminal w folderze projektu

**Windows**
- Kliknij prawym przyciskiem myszy na folder projektu i wybierz **Otwórz w Terminalu**.

**Linux**
- Kliknij prawym przyciskiem myszy na folder projektu i wybierz **Otwórz w terminalu**.
- *Wskazówka:* nazwa tej opcji i jej dostępność zależą od menedżera plików. Zawsze możesz otworzyć terminal i przejść do folderu poleceniem `cd`.

**macOS**
- Kliknij prawym przyciskiem myszy na folder (lub naciśnij `Control` + kliknięcie) i wybierz **Usługi** → **Nowy terminal w folderze**.
- *Wskazówka:* jeśli nie widzisz tej opcji, włącz ją w: `Ustawienia systemowe` → `Klawiatura` → `Skróty klawiszowe` → `Usługi` → `Pliki i foldery` → `Nowy terminal w folderze` 
- *Alternatywa* Otwórz terminal i przejdź do folderu poleceniem `cd`.

### 2. Uruchom aplikację lub testy

**Windows**

```powershell
.\mvnw.cmd -q compile exec:java   # dla aplikacji
.\mvnw.cmd test                   # dla testów
```

**Linux i macOS**

```bash
./mvnw -q compile exec:java   # dla aplikacji
./mvnw test                   # dla testów
```

*Wskazówka:* jeśli pojawi się błąd `Permission denied`, nadaj plikowi uprawnienia do wykonania poleceniem `chmod +x mvnw`.

## Jak używać
Po uruchomieniu aplikacja przelicza ceny komputerów, zapisuje je do bazy i wystawia fakturę w pliku XML. Następnie wyświetla menu, 
w którym wystarczy postępować zgodnie z instrukcjami wyświetlanymi w konsoli.

Na wszystkie opcje odpowiadaj, wpisując cyfrę z menu. Wyjątkiem jest opcja wyszukiwania po nazwie, w której należy podać frazę wyszukiwania.
Wpisanie niepoprawnych znaków może skutkować ponownym wyświetleniem menu lub pominięciem danego kroku.
## Struktura projektu

Ze względu na podział odpowiedzialności i czytelność Fakturomat został podzielony na pakiety:

- **dto** - pakiet przechowujący klasy służące do transferu danych (np. odwzorowanie JSONa odpowiedzi API).
- **enums** - enumy, w których zdefiniowane są opcje w wymaganych polach w innych klasach (w projekcie tylko dla opcji sortowania).
- **exception** - pakiet własnych wyjątków.
- **model** - pakiet z klasami, które odpowiadają encjom w bazie danych.
- **repository** - pakiet repozytoriów dla modeli.
- **service** - pakiet z klasami zajmującymi się logiką biznesową (m.in. pobieranie kursów z API NBP i eksport do XML) oraz logiką użytkową, czyli obsługą konsoli (ConsoleUI).
- **plik Main.java** - główny plik aplikacji.


## Decyzje projektowe

- **Baza danych H2 w pamięci** - Wykorzystanie bazy danych w pamięci zapewnia brak konfiguracji przez użytkownika, który chce przetestować aplikację. Dane z bazy znikają po zamknięciu aplikacji.
- **Kurs NBP: średnia z dnia** - Zdecydowałem się na tabelę A z API NBP, czyli średni kurs z dnia, do przeliczania waluty, gdyż w przypadku faktur używa się właśnie uśrednionej wartości. Kurs kupna i sprzedaży (tabela C) zależy od kierunku transakcji, a ten nie jest określony.
- **Próba uzyskania kursu do 7 dni wstecz** - Gdy NBP nie publikuje kursu dla danej daty (weekend lub święto), pobieram kursy z 7 dni wstecz jednym zapytaniem i biorę najnowszy. Nawet w najgorszym przypadku, gdy najdłuższe święta i weekend ułożyłyby się jednym ciągiem, siedem dni wystarcza, aby dotrzeć do przynajmniej 1 dnia roboczego. Jeśli mimo to w tym zakresie nie ma żadnego kursu, rzucany jest wyjątek.
- **Zapis do pliku przed bazą danych** - Ze względu na prostotę sprawdzania i usuwania pliku zapis do pliku następuje przed zapisem do bazy danych. Gdy zapis do bazy zakończy się błędem, plik jest usuwany.
- **Użycie BigDecimal** - użyte w projekcie do przechowywania wszelkich kwot, aby błędy zmiennoprzecinkowe i zaokrągleń nie wpływały na wynik.
- **Użycie Jackson do mapowania** - zarówno do mapowania JSONa odpowiedzi API, jak i obiektów do XML została wybrana biblioteka Jackson ze względu na prostotę użycia i skuteczność.
- **HttpClient z JDK** - do zapytań do API NBP użyłem klienta wbudowanego w JDK, więc projekt nie potrzebuje do tego zewnętrznej biblioteki.


## Założenia
- **Sortowanie** - aplikacja oferuje dwie osobne opcje sortowania: po nazwie oraz po dacie księgowania, każdą rosnąco i malejąco. W razie potrzeby można je łatwo połączyć w sortowanie złożone.
- **Wyszukiwanie** - wyszukiwanie nie jest wrażliwe na wielkość liter, a szukana fraza może znajdować się w dowolnym miejscu nazwy komputera.
- **Data kursu** - kurs waluty jest brany z dnia księgowania komputera, a gdy tego dnia NBP go nie publikuje, z ostatniego wcześniejszego dnia, w którym był dostępny.
- **Dane wejściowe** - aplikacja korzysta z 3 przykładowych komputerów z nazwą, datą księgowania i ceną.
- **Waluta** - wszystkie ceny wejściowe są w dolarach (USD) i przeliczane na złotówki (PLN). Dane są zaokrąglane od połowy w górę do 2 miejsca po przecinku.

## Testy
Testy uruchamia się komendą opisaną w sekcji Uruchomienie. Podział testów odpowiada podziałowi na pakiety i obejmuje:

- **Klient NBP** - pobranie kursu dla poprawnych danych oraz obsługa błędów: błąd serwera, niepoprawne dane wejściowe, niepoprawna odpowiedź lub JSON. API NBP jest w testach symulowane przy pomocy WireMock.
- **Repozytorium komputerów** - tworzenie tabeli (także przy wielokrotnym wywołaniu), zapis komputerów, w tym wycofanie transakcji przy błędnych danych, oraz pobieranie z sortowaniem po nazwie i dacie księgowania (rosnąco i malejąco), wyszukiwaniem i ich połączeniem. Testowane jest też zachowanie przy zamkniętym połączeniu.
- **Serwis komputerów** - przetwarzanie zamówienia (utworzenie faktury, pusta lista, błędna ścieżka, błąd eksportera) oraz wyszukiwanie komputerów.
- **Eksporter XML** - utworzenie pliku z fakturą oraz obsługa błędnej ścieżki (pusta, null, nieistniejący katalog).

## Co dodałbym dalej
- **Dodawanie i usuwanie komputerów** - możliwość dodania komputera z podaniem wszystkich danych (nazwa, data księgowania, cena w dolarach) oraz usunięcia wybranego rekordu. Każdy komputer dostawałby unikalny identyfikator UUID, po którym byłby jednoznacznie wskazywany.
- **Unikalne nazwy plików z fakturami** - faktury nie byłyby nadpisywane przy kolejnym uruchomieniu, tylko miałyby w nazwie datę wystawienia lub kolejny numer.