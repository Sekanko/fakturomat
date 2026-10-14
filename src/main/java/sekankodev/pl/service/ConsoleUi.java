package sekankodev.pl.service;

import sekankodev.pl.dto.ComputerRequestDto;
import sekankodev.pl.enums.SortOption;
import sekankodev.pl.model.Computer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ConsoleUi {
    private final Scanner scanner;
    private final ComputerService computerService;

    public ConsoleUi(Scanner scanner, ComputerService computerService) {
        this.computerService = computerService;
        this.scanner = scanner;
    }

    public void loop(){
        initData();
        welcomeMessage();
        String firstChoice;

        do {
            firstChoice = showMainMenu();
            if (firstChoice.equals("1")){
                SortOption sortOption = chooseSortOption();
                String query = enterSearchQuery();
                List<Computer> computers = computerService.findComputers(sortOption, query);
                System.out.println("Wyniki wyszukiwania:");

                System.out.println("-----------------------------------");
                computers.forEach(System.out::println);
                System.out.println("-----------------------------------");

            } else if (!firstChoice.equals("2")){
                System.out.println("Nie rozpoznano wyboru. Spróbuj ponownie.");
            }
        }while (!firstChoice.equals("2"));
    }

    private void initData(){

        System.out.println("Wczytywanie następująych danych...");
        System.out.println("""
                - komputer ACER Aspire – kwota 345 USD
                - komputer DELL Latitude – kwota 543 USD
                - komputer HP Victus – kwota 346 USD
                """);

        List<ComputerRequestDto> computerRequestDtos = List.of(
                new ComputerRequestDto("ACER Aspire", LocalDate.of(2026, 7, 3), BigDecimal.valueOf(345)),
                new ComputerRequestDto("DELL Latitude", LocalDate.of(2026, 7, 12), BigDecimal.valueOf(345)),
                new ComputerRequestDto("HP Victus", LocalDate.of(2026, 7, 15), BigDecimal.valueOf(345))
        );

        computerService.processOrder(computerRequestDtos, "./faktura.xml");
        System.out.println("Komputery zostały zapisane do bazy danych oraz utworzono fakturę w pliku faktura.xml.");
    }

    private void welcomeMessage(){
        System.out.println("***");
        System.out.println("Witaj w systemie FAKTUROMAT");
        System.out.println("Po wyborze \"pokaż wszytskie komputery\", wyświetlą się opcje sortowania i wyszukiwania.");
        System.out.println("***");
    }

    private String showMainMenu(){
        System.out.println("Wybierz jedną z opcji:");
        System.out.println("1. Pokaż wszytskie komputery");
        System.out.println("2. Wyjście");

        return scanner.nextLine().trim();
    }

    private String enterSearchQuery(){
        System.out.println("Szukanie po nazwie komputera (naciśnij enter żeby pominąć):");
        return scanner.nextLine().trim();
    }

    private SortOption chooseSortOption(){
        System.out.println("Sortuj po(naciśnij enter żeby pominąć): ");
        System.out.println("1. Nazwie");
        System.out.println("2. Dacie księgowania");
        
        String input = scanner.nextLine().trim();
        SortOption sortOption;
        
        switch (input) {
            case "1" -> sortOption = SortOption.NAME;
            case "2" -> sortOption = SortOption.POST_DATE;
            default -> sortOption = SortOption.NOTHING;
        }

        return sortOption;
    }
}
