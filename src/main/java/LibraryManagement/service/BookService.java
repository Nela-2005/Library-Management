package LibraryManagement.service;
import LibraryManagement.entity.Book;
import LibraryManagement.entity.Loan;
import LibraryManagement.repository.BookRepository;
import LibraryManagement.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }
    public void updateBookAvailabilityOnLoan(Loan loan, String action) {
        if (loan == null || loan.getBook() == null) {
            System.out.println("Loan or book is null. No action needed.");
            return;
        }

        System.out.println("Inside updateBookAvailabilityOnLoan: " + loan);
        System.out.println("Action: " + action);
        System.out.println("Loan ID: " + loan.getId());

        Book book = bookRepository.findById(loan.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        String oldStatus;

        if (loan.getId() != null) {
            Loan loanFromDb = loanRepository.findById(loan.getId())
                    .orElseThrow(() -> new RuntimeException("Loan not found"));
            oldStatus = loanFromDb.getStatus(); // Gjendja aktuale në DB
        } else {
            oldStatus = "Returned"; // Nëse huazimi është i ri, supozojmë që libri ishte i lirë më parë
        }

        String newStatus = loan.getStatus(); // Gjendja e re e dërguar nga klienti
        System.out.println("Old status: " + oldStatus + ", New status: " + newStatus);

        // Nëse statusi nuk ka ndryshuar dhe nuk është një fshirje, nuk bëjmë asnjë ndryshim
        if (oldStatus.equals(newStatus) && !"DELETE".equals(action)) {
            System.out.println("Status not changed. No action needed.");
            return;
        }

        // Nëse fshihet huazimi, e rikthejmë librin në gjendjen e mëparshme
        if ("DELETE".equals(action)) {
            System.out.println("Deleting loan. Restoring book availability.");
            if ("Borrowed".equals(oldStatus)) {
                System.out.println("Book was borrowed. Restoring availability.");
                adjustBookCounts(book, -1, 1); // Kthejmë librin në dispozicion
            }
        }
        // Nëse statusi ndryshon nga "Returned" -> "Borrowed"
        else if ("Borrowed".equals(newStatus) && "Returned".equals(oldStatus) && book.getAvailable() > 0) {
            System.out.println("Book is available. Borrowing book.");
            adjustBookCounts(book, 1, -1);
        }
        // Nëse statusi ndryshon nga "Borrowed" -> "Returned"
        else if ("Returned".equals(newStatus) && "Borrowed".equals(oldStatus)) {
            System.out.println("Returning book.");
            adjustBookCounts(book, -1, 1);
        }

        bookRepository.save(book);
    }

    /**
     * Ndryshon numrin e librave të huazuar dhe të disponueshëm.
     */
    private void adjustBookCounts(Book book, int borrowedChange, int availableChange) {
        System.out.println("Adjusting book counts. Borrowed change: " + borrowedChange + ", Available change: " + availableChange);
        book.setBorrowed(book.getBorrowed() + borrowedChange);
        book.setAvailable(book.getAvailable() + availableChange);
        System.out.println("Updated book counts. Borrowed: " + book.getBorrowed() + ", Available: " + book.getAvailable());
    }


    public void addBook(Book book) {
        System.out.println("Inside addBook: " + book);
        book.setAvailable(book.getQuantity());  // Të gjithë librat janë të disponueshëm
        book.setBorrowed(0);  // Asnjë libër nuk është huazuar akoma

        bookRepository.save(book);  // Shto libër në DB
    }


    public void updateBookQuantity(Book book) {
        // Merr librin nga DB për të kontrolluar gjendjen e tij aktuale
        Book bookFromDb = bookRepository.findById(book.getId()).orElseThrow(() -> new RuntimeException("Book not found"));
        System.out.println("Book from DB: " + bookFromDb);
        System.out.println("Book from form: " + book);
        int quantityDifference = book.getQuantity() - bookFromDb.getQuantity();

        int newAvailable = book.getAvailable() + quantityDifference;
        System.out.println("New available: " + newAvailable);
        book.setAvailable(newAvailable);
        book.setBorrowed(book.getBorrowed());

        if (newAvailable < 0) {
            book.setAvailable(0);
        }

        bookRepository.save(book);  // Përshtatni librin në bazën e të dhënave
    }


    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(new Book());
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

}
