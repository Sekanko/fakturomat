package pl.sekankodev.service;

import pl.sekankodev.dto.ComputerRequestDto;
import pl.sekankodev.enums.SortDirection;
import pl.sekankodev.enums.SortOption;
import pl.sekankodev.model.Computer;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
                SortDirection sortDirection = SortDirection.ASC;
                if (sortOption != SortOption.NOTHING) {
                    sortDirection = chooseSortDirection();
                }
                String query = enterSearchQuery();
                List<Computer> computers = computerService.findComputers(sortOption, sortDirection, query);
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
        System.out.println();
        System.out.println("Wczytywanie następujących danych...");
        System.out.println("""
                - komputer ACER Aspire – kwota 345 USD
                - komputer DELL Latitude – kwota 543 USD
                - komputer HP Victus – kwota 346 USD
                """);

        List<ComputerRequestDto> computerRequestDtos = List.of(
                new ComputerRequestDto("ACER Aspire", LocalDate.of(2026, 7, 3), BigDecimal.valueOf(345).setScale(2, RoundingMode.HALF_UP)),
                new ComputerRequestDto("DELL Latitude", LocalDate.of(2026, 7, 12), BigDecimal.valueOf(543).setScale(2, RoundingMode.HALF_UP)),
                new ComputerRequestDto("HP Victus", LocalDate.of(2026, 7, 15), BigDecimal.valueOf(346).setScale(2, RoundingMode.HALF_UP))
        );

        computerService.processOrder(computerRequestDtos, "./faktura.xml");
        System.out.println("Komputery zostały zapisane do bazy danych oraz utworzono fakturę w pliku faktura.xml.");
    }

    private void welcomeMessage(){
        System.out.println();

        System.out.println("***");
        System.out.println("Witaj w systemie FAKTUROMAT");
        System.out.println("Po wyborze \"pokaż wszytskie komputery\" wyświetlą się opcje sortowania i wyszukiwania.");
        System.out.println("***");
    }

    private String showMainMenu(){
        System.out.println();
        System.out.println("Wybierz jedną z opcji:");
        System.out.println("1. Pokaż wszytskie komputery");
        System.out.println("2. Wyjście");

        return scanner.nextLine().trim();
    }

    private String enterSearchQuery(){
        System.out.println();
        System.out.println("Szukanie po nazwie komputera (naciśnij enter żeby pominąć):");
        return scanner.nextLine().trim();
    }

    private SortOption chooseSortOption(){
        System.out.println();
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

    private SortDirection chooseSortDirection(){
        System.out.println();
        System.out.println("Jak chcesz sortować:");
        System.out.println("1. Rosnąco");
        System.out.println("2. Malejąco");

        SortDirection sortDirection;
        String input = scanner.nextLine().trim();

        switch (input) {
            case "1" -> sortDirection = SortDirection.ASC;
            case "2" -> sortDirection = SortDirection.DESC;
            default -> {
                System.out.println("Wprowadzono niepoprawną komendę. Zostanie zastosowane sortowanie rosnące");
                sortDirection = SortDirection.ASC;
            }
        }
        return sortDirection;
    }
}
