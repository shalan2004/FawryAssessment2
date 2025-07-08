import java.util.*;

abstract class Book {
    protected String isbn, title, author;
    protected int year;
    protected double price;

    public Book(String isbn, String title, String author, int year, double price) {
        if (isbn == null || isbn.isEmpty()) throw new IllegalArgumentException("Quantum book store: ISBN needed");
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.price = price;
    }

    public abstract boolean isAvailable();
    public abstract void buy(int q, String mail, String addr);

    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public double getPrice() { return price; }

    public boolean tooOld(int now, int limit) {
        return now - year > limit;
    }

    public String toString() {
        return "Quantum book store: " + title + " by " + author + " (" + year + ") - " + price + " EGP";
    }
}

class PaperBook extends Book {
    private int stock;

    public PaperBook(String isbn, String title, String author, int year, double price, int stock) {
        super(isbn, title, author, year, price);
        this.stock = stock;
    }

    public boolean isAvailable() {
        return stock > 0;
    }

    public void buy(int q, String mail, String addr) {
        if (q <= 0) throw new IllegalArgumentException("Quantum book store: Quantity invalid");
        if (stock < q) {
            System.out.println("Quantum book store: Not enough in stock!");
            throw new RuntimeException("Quantum book store: Only " + stock + " left");
        }

        stock -= q;
        ShippingService.send(this, addr, q);
    }

    public String toString() {
        return super.toString() + ", Stock: " + stock;
    }
}

class EBook extends Book {
    private String type;

    public EBook(String isbn, String title, String author, int year, double price, String type) {
        super(isbn, title, author, year, price);
        if (type == null) throw new IllegalArgumentException("Quantum book store: Missing file type");
        this.type = type;
    }

    public boolean isAvailable() {
        return true;
    }

    public void buy(int q, String mail, String addr) {
        if (q <= 0) throw new IllegalArgumentException("Quantum book store: Qty must be > 0");
        MailService.send(this, mail, q);
    }

    public String toString() {
        return super.toString() + " [Type: " + type + "]";
    }
}

class ShowcaseBook extends Book {
    public ShowcaseBook(String isbn, String title, String author, int year, double price) {
        super(isbn, title, author, year, price);
    }

    public boolean isAvailable() {
        return false;
    }

    public void buy(int q, String mail, String addr) {
        throw new UnsupportedOperationException("Quantum book store: This book is just for show!");
    }
}

class ShippingService {
    public static void send(PaperBook b, String addr, int q) {
        // TODO: actually send stuff someday
        System.out.println("Quantum book store: Sending " + q + " x '" + b.getTitle() + "' to " + addr);
    }
}

class MailService {
    public static void send(EBook b, String mail, int q) {
        // TODO: real email system
        System.out.println("Quantum book store: Emailing copy of '" + b.getTitle() + "' to " + mail);
    }
}

class QuantumBookstore {
    Map<String, Book> books = new HashMap<>();

    void add(Book b) {
        if (books.containsKey(b.getIsbn())) {
            System.out.println("Quantum book store: Book already there - " + b.getTitle());
        } else {
            books.put(b.getIsbn(), b);
            System.out.println("Quantum book store: Book added - " + b.getTitle());
        }
    }

    void printAll() {
        if (books.isEmpty()) {
            System.out.println("Quantum book store: Inventory is empty!");
            return;
        }
        for (Book b : books.values()) {
            System.out.println(b);
        }
    }

    double buyBook(String isbn, int q, String mail, String addr) {
        Book b = books.get(isbn);
        if (b == null){
            System.out.println("Quantum book store: Sorry, book not found");
            return 0;
        }else{
            if (!b.isAvailable()) {
                throw new RuntimeException("Quantum book store: Cannot purchase this book.");
            }
            b.buy(q, mail, addr);
            double total = b.getPrice() * q * 1.14; 
            System.out.println("Quantum book store: Done Paid: " + total + " EGP");
            return total;
        }
    }

    void removeOld(int years) {
        int now = 2024;
        List<String> out = new ArrayList<>();

        for (Book b : books.values()) {
            if (b.tooOld(now, years)) {
                out.add(b.getIsbn());
                System.out.println("Quantum book store: Removed outdated - " + b.getTitle());
            }
        }

        for (String k : out) books.remove(k);
    }
}

public class QuantumBookstoreFullTest {
    public static void main(String[] args) {
        QuantumBookstore store = new QuantumBookstore();

        store.add(new PaperBook("P1", "Java 101", "Salma Nabil", 2020, 200, 6));
        store.add(new EBook("E1", "Microservices Intro", "Mohamed Tarek", 2021, 180, "pdf"));
        store.add(new ShowcaseBook("S1", "Architecture Visuals", "Abdulrahman Shalan", 2023, 500));
        store.add(new PaperBook("P2", "Old School Code", "Omar Galal", 2008, 90, 2));

        store.printAll();

        store.buyBook("P1", 2, "ahmed@a.com", "Alexandria");
        store.buyBook("E1", 1, "fatma@b.com", "Cairo");

        try {
            store.buyBook("S1", 1, "demo@demo.com", "Zamalek");
        } catch (Exception ex) {
            System.out.println("Quantum book store: As expected, can't buy showcase.");
        }

        store.removeOld(10);
    }
}
