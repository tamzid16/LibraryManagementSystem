import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

class ISBN {
    private String isbn;

    public ISBN(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}

class Book extends ISBN {
    private String title;
    private String category;
    private int quantity;
    private int availableQuantity;

    public Book(String title, String category, String isbn, int quantity) {
        super(isbn);
        this.title = title;
        this.category = category;
        this.quantity = quantity;
        this.availableQuantity = quantity;
    }

    // Getters for book information
    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    // Borrow the book
    public boolean borrowBook() {
        if (availableQuantity > 0) {
            availableQuantity--;
            return true;
        } else {
            return false;
        }
    }

    // Return the book
    public boolean returnBook() {
        if (availableQuantity < quantity) {
            availableQuantity++;
            return true;
        } else {
            return false;
        }
    }
}

class Library extends ISBN {
    private ArrayList<Book> books = new ArrayList<>();
    private static final String BOOKS_FILE = "books.txt";
    private static final String BORROW_FILE = "borrow-book.txt";
    private static final String RETURN_FILE = "return-book.txt";

    public Library() {
        super("");
        loadBooksFromFile();
    }

    private void loadBooksFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(BOOKS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] bookInfo = line.split(",");
                if (bookInfo.length == 4) {
                    String title = bookInfo[0].trim();
                    String category = bookInfo[1].trim();
                    String isbn = bookInfo[2].trim();
                    int quantity = Integer.parseInt(bookInfo[3].trim());
                    books.add(new Book(title, category, isbn, quantity));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading books from file: " + e.getMessage());
        }
    }

    private void saveBooksToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                bw.write(book.getTitle() + "," + book.getCategory() + "," + book.getIsbn() + "," + book.getQuantity());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books to file: " + e.getMessage());
        }
    }

    public void addBook(String title, String category, String isbn, int quantity) {
        books.add(new Book(title, category, isbn, quantity));
        saveBooksToFile();
    }

    public void listBooks() {
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            System.out.println("Available Books:");
            for (Book book : books) {
                System.out.println("Title: " + book.getTitle());
                System.out.println("Category: " + book.getCategory());
                System.out.println("ISBN: " + book.getIsbn());
                System.out.println("Available Quantity: " + book.getAvailableQuantity());
                System.out.println();
            }
        }
    }

    public Book searchBookByISBN(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                return book;
            }
        }
        return null;
    }

    public boolean borrowBook(String isbn, String studentName) {
        Book book = searchBookByISBN(isbn);
        if (book != null) {
            boolean success = book.borrowBook();
            if (success) {
                // Record the borrow operation in the borrow-book file
                recordBorrowDetails(studentName, isbn);
                saveBooksToFile();
                return true;
            } else {
                System.out.println("Book is out of stock.");
            }
        } else {
            System.out.println("Book not found.");
        }
        return false;
    }

    public boolean returnBook(String isbn, String studentName) {
        Book book = searchBookByISBN(isbn);
        if (book != null) {
            boolean success = book.returnBook();
            if (success) {
                // Record the return operation in the return-book file
                recordReturnDetails(studentName, isbn);
                saveBooksToFile();
                return true;
            } else {
                System.out.println("Invalid return operation.");
            }
        } else {
            System.out.println("Book not found.");
        }
        return false;
    }

    private void recordBorrowDetails(String studentName, String isbn) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BORROW_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            bw.write(studentName + "," + isbn + "," + timestamp);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error recording borrow details: " + e.getMessage());
        }
    }

    private void recordReturnDetails(String studentName, String isbn) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RETURN_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            bw.write(studentName + "," + isbn + "," + timestamp);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error recording return details: " + e.getMessage());
        }
    }

    public void listBorrowedBooks() {
        try (BufferedReader br = new BufferedReader(new FileReader(BORROW_FILE))) {
            String line;
            System.out.println("List of Borrowed Books:");
            while ((line = br.readLine()) != null) {
                String[] borrowInfo = line.split(",");
                if (borrowInfo.length == 3) {
                    String studentName = borrowInfo[0].trim();
                    String isbn = borrowInfo[1].trim();
                    String timestamp = borrowInfo[2].trim();
                    System.out.println("Student Name: " + studentName);
                    System.out.println("ISBN: " + isbn);
                    System.out.println("Borrow Date: " + timestamp);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading borrow-book file: " + e.getMessage());
        }
    }

    public void listReturnedBooks() {
        try (BufferedReader br = new BufferedReader(new FileReader(RETURN_FILE))) {
            String line;
            System.out.println("List of Returned Books:");
            while ((line = br.readLine()) != null) {
                String[] returnInfo = line.split(",");
                if (returnInfo.length == 3) {
                    String studentName = returnInfo[0].trim();
                    String isbn = returnInfo[1].trim();
                    String timestamp = returnInfo[2].trim();
                    System.out.println("Student Name: " + studentName);
                    System.out.println("ISBN: " + isbn);
                    System.out.println("Return Date: " + timestamp);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading return-book file: " + e.getMessage());
        }
    }
}

public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println("Library Management System");
        System.out.print("Enter your role (Student/Staff): ");
        String role = scanner.nextLine().trim();

        while (true) {
            if (role.equalsIgnoreCase("Staff")) {
                System.out.println("\nStaff Options:");
                System.out.println("1. Add Book");
                System.out.println("2. List Student Borrowed Books");
                System.out.println("3. List Student Returned Books");
                System.out.println("4. Exit to Role Selection");
            } else if (role.equalsIgnoreCase("Student")) {
                System.out.println("\nStudent Options:");
                System.out.println("1. View All Available Books");
                System.out.println("2. Search a Book by ISBN");
                System.out.println("3. Borrow a Book");
                System.out.println("4. Return a Book");
                System.out.println("5. Exit to Role Selection");
            }

            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (role.equalsIgnoreCase("Staff")) {
                    switch (choice) {
                        case 1:
                            // Add a book (as before)
                            System.out.print("Enter the title of the new book: ");
                            String newBookTitle = scanner.nextLine();
                            System.out.print("Enter the category of the new book: ");
                            String newBookCategory = scanner.nextLine();
                            System.out.print("Enter the ISBN of the new book: ");
                            String newBookISBN = scanner.nextLine();
                            System.out.print("Enter the quantity of the new book: ");
                            int newBookQuantity = scanner.nextInt();
                            scanner.nextLine();
                            library.addBook(newBookTitle, newBookCategory, newBookISBN, newBookQuantity);
                            System.out.println("Book added successfully.");
                            break;

                        case 2:
                            library.listBorrowedBooks();
                            break;

                        case 3:
                            library.listReturnedBooks();
                            break;

                        case 4:
                            System.out.println("Returning to Role Selection.");
                            role = null; // Reset the role to allow reselection
                            break;

                        default:
                            System.out.println("Invalid choice. Please select a valid option.");
                    }
                } else if (role.equalsIgnoreCase("Student")) {
                    switch (choice) {
                        case 1:
                            library.listBooks();
                            break;
                        case 2:
                            System.out.print("Enter the ISBN of the book you want to search for: ");
                            String isbnToSearch = scanner.nextLine();
                            Book searchedBook = library.searchBookByISBN(isbnToSearch);
                            if (searchedBook != null) {
                                System.out.println("Title: " + searchedBook.getTitle());
                                System.out.println("Category: " + searchedBook.getCategory());
                                System.out.println("Available Quantity: " + searchedBook.getAvailableQuantity());
                            } else {
                                System.out.println("Book not found.");
                            }
                            break;
                        case 3:
                            System.out.print("Enter your name: ");
                            String studentNameBorrow = scanner.nextLine(); // Rename the variable
                            System.out.print("Enter the ISBN of the book you want to borrow: ");
                            String isbnToBorrow = scanner.nextLine();
                            boolean borrowSuccess = library.borrowBook(isbnToBorrow, studentNameBorrow);
                            if (borrowSuccess) {
                                System.out.println("Book successfully borrowed.");
                            }
                            break;
                        case 4:
                            System.out.print("Enter your name: ");
                            String studentNameReturn = scanner.nextLine(); // Rename the variable
                            System.out.print("Enter the ISBN of the book you want to return: ");
                            String isbnToReturn = scanner.nextLine();
                            boolean returnSuccess = library.returnBook(isbnToReturn, studentNameReturn);
                            if (returnSuccess) {
                                System.out.println("Book successfully returned.");
                            }
                            break;
                        case 5:
                            System.out.println("Returning to Role Selection.");
                            role = null; // Reset the role to allow reselection
                            break;
                        default:
                            System.out.println("Invalid choice. Please select a valid option.");
                    }
                }

                if (role == null) {
                    System.out.print("Enter your role (Student/Staff): ");
                    role = scanner.nextLine().trim();
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid numeric choice.");
                scanner.nextLine();
            }
        }
    }
}
