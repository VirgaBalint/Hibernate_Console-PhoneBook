package dbcontrol.tables;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "store")
@NamedQueries({
    @NamedQuery(name = "searchByStoreName", query = "from Store where name like :name"),
    @NamedQuery(name = "searchByStoreAddress", query = "from Store where address like :address"),
    @NamedQuery(name = "searchByStoreOwner", query = "from Store where owner like :owner"),
    @NamedQuery(name = "searchByStoreId", query = "from Store where id = :id"),
    @NamedQuery(
            name = "updateStoreName",
            query = "update Store set name = :name where id = :id"
    ),
    @NamedQuery(
            name = "updateStoreAddress",
            query = "update Store set address = :address where id = :id"
    ),
    @NamedQuery(
            name = "updateStoreOwner",
            query = "update Store set owner = :owner where id = :id"
    ),
    @NamedQuery(
            name = "updateStoreBookList",
            query = "update Store set bookList = :bookList where id = :id"
    ),
    @NamedQuery(name = "changeStoreLicense", query = "update Store set licensed = :licensed where id = :id")
})
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
        public List<Book> getBookList()
        {
            return bookList;
        }

    @Column
    String name;

    boolean licensed;
        public boolean isLicensed()
        {
            return licensed;
        }

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

    public abstract class bookNums
    {
        static Map<Book, Integer> bookNums;
        public static void addBookNums(Book book, Integer num)
        {
            bookNums.put(book, bookNums.get(book) + num);
        }
    }

    public int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        List<String> books = new ArrayList<>();
        bookList.forEach(book -> books.add(book.title));

        return String.format(
                """
                    
                    %s:
                    > Id: %d
                    > Address: %s
                    > Owner: %s
                    > Books: %s    
                    """, name, id, address, owner, books
        );
    }
}
