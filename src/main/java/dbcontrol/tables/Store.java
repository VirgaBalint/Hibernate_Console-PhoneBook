package dbcontrol.tables;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "store")
public class Store
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    int id;

    @ManyToMany(mappedBy = "stores", cascade = CascadeType.DETACH)
    List<Book> bookList;
        public void addBook(Book book, int num)
        {
            bookList.add(book);
            bookNums.addBookNums(book,num);
        }

    @Column
    String name;

    @Column
    String address;

    @Column
    String owner;

    public Store(){}
    public Store(String name, String adress, String owner)
    {
        this.name = name;
        this.address = adress;
        this.owner = owner;
    }

    abstract class bookNums
    {
        static Map<Book, Integer> bookNums;
        public static void addBookNums(Book book, Integer num)
        {
            bookNums.put(book, bookNums.get(book) + num);
        }
    }
}
