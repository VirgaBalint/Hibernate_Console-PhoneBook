package dbcontrol.tables;

import jakarta.persistence.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "book")
@NamedQueries({
        @NamedQuery(
            name = "searchByAny",
            query = "from Book where title like :title or author.name like :author or isbn like :isbn"
        ),
        @NamedQuery(name = "searchByBookId", query = "from Book where id = :id"),
        @NamedQuery(name = "searchByTitle", query = "from Book where title like :title"),
        @NamedQuery(name = "searchByAuthor", query = "from Book where author.name like :author and author.id = :id"),
        @NamedQuery(name = "searchByIsbn", query = "from Book where isbn like :isbn"),
        @NamedQuery(
                name = "updateISBN",
                query = "update Book set isbn = :newIsbn where id = :id"
        ),
        @NamedQuery(
                name = "updateTitle",
                query = "update Book set title = :newTitle where id = :id"
        ),
        @NamedQuery(
                name = "updateDOP",
                query = "update Book set publishDate = :newDop where id = :id"
        ),
        @NamedQuery(
                name= "updateAuthor",
                query = "update Book set author = :newAuthor where id = :id"
        ),
        @NamedQuery(
                name= "updateMarket",
                query = "update Book set inMarket = :inMarket where id = :id"
        )
})
public class Book
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    int id;

    @ManyToOne
    @JoinColumn
    Author author;

    @Column
    String isbn;

    @Column
    String title;

    @Column(name = "dop")
    Date publishDate;

    boolean inMarket;
        public void setInMarket(boolean inMarket)
        {
            this.inMarket = inMarket;
        }
        public boolean isInMarket()
        {
            return inMarket;
        }

    @ManyToMany
    @JoinColumn
    List<Store> stores;

    public Book(){}
    public Book(Author author, String isbn, String title, Date publishDate)
    {
        this.author = author;
        this.isbn = isbn;
        this.title = title;
        this.publishDate = publishDate;
    }

    public Author getAuthor()
    {
        return author;
    }
    public void setAuthor(Author author)
    {
        this.author = author;
    }
    public String getIsbn()
    {
        return isbn;
    }
    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public Date getPublishDate()
    {
        return publishDate;
    }
    public void setPublishDate(Date publishDate)
    {
        this.publishDate = publishDate;
    }
    public int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return String.format(
                """
                        
                    %s:
                    > Author: %s
                    > Isbn: %s
                    > Publish date: %s
                    > %s
                    > Stores: %s\s
                    """, title, author.name, isbn, publishDate,
                    inMarket ? "Available for purchase" : "Not available for purchase",
                    Arrays.toString(stores.toArray())
        );
    }
}
