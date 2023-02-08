package dbcontrol.tables;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "author")
@NamedQueries({
        @NamedQuery(name = "searchByAuthorName", query = "from Author where name like :name"),
        @NamedQuery(name= "searchByAuthorId", query = "from Author  where id = :id"),
        @NamedQuery(name = "updateBooks", query = "update Author set bookList = :books where id = :id"),
        @NamedQuery(name = "updateAuthorName", query = "update Author set name = :name where id = :id"),
        @NamedQuery(name = "updateDob", query = "update Author set dateOfBirth = :dob where id = :id"),
        @NamedQuery(name = "deleteAuthor", query = "delete from Author where id = :id")
})
public class Author
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @OneToMany(mappedBy = "author", cascade = CascadeType.DETACH)
    List<Book> bookList;
        public void addBookToList(Book book)
        {
            bookList.add(book);
        }
        public void removeBookFromList(Book book)
        {
            bookList.remove(book);
        }
        public List<Book> getBookList()
        {
            return bookList;
        }

    @Column
    public String name;

    @Column(name = "dob")
    Date dateOfBirth;

    public Author(){}
    public Author(String name, Date dateOfBirth)
    {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
    }

        public int getId()
        {
            return id;
        }
    @Override
    public String toString()
    {
        List<String> books = new ArrayList<>();
        for(var x: bookList)
            books.add(x.title);

        return String.format(
                """
                    
                    %s:
                    > id: %d
                    > Date of birth: %s
                    > Books: %s
                    """, name, id, dateOfBirth.toString(), books
        );
    }
}
