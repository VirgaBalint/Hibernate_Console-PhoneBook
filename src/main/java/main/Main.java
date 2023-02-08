package main;

import dbcontrol.Hibernate;
import dbcontrol.tables.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("SpellCheckingInspection")
public class Main
{
    public static void main(String[] args)
    {
        Hibernate hibernate = new Hibernate();
        Scanner scn= new Scanner(System.in);

        while(true)
        {
            boolean vissza = false;
            System.out.println(
                    """
                                
                                Main menu
                        =========================
                        1 - Könyvek
                        2 - Szerzők
                        3 - Boltok
                        
                        0 - Kilépes
                        =========================
                        """
            );
            String q1= scn.nextLine();
            q1 = String.valueOf(q1.charAt(0));

            switch (q1)         // Main menu
            {
                case "1" ->                 // BOOK __ DONE??
                {
                    while(!vissza)     // Book almenu
                    {
                        vissza = false;
                        System.out.println(
                                """
                                    
                                              Books
                                    =========================
                                    1 - Könyv hozzáadása
                                    2 - Könyv keresés
                                    3 - Könyv módosítás
                                    4 - Forgalomba Helyezés
                                    
                                    0 - Vissza
                                    =========================
                                    """
                        );
                        String bq1 = scn.nextLine();
                        bq1 = String.valueOf(bq1.charAt(0));

                        switch (bq1)
                        {
                            case "1" ->                         // BOOK ADD -- DONE?
                            {
                                System.out.println("\n> Add meg a könyv címét (0 = Kilepes):");
                                String bookName = scn.nextLine();

                                if("0".equals(bookName))
                                    break;

                                System.out.println("\n> Add meg a könyv ISBN számát:");
                                String isbn = scn.nextLine();

                                System.out.println("\n> Add meg a könyv Kiadásának dátumát");


                                Date dop = datumBekeroKiado(scn);

                                Author author = null;

                                while(true)                     // AUTHOR
                                {
                                    System.out.println("\n> Add meg a szerző nevét (0 = Kilepes):");
                                    String name= scn.nextLine();

                                    if("0".equals(name))
                                        break;

                                    Author search = null;
                                    try
                                    {
                                        printAuthorsWithTheSameName(name,hibernate);
                                        Integer id = idBekero(scn);

                                        if(id == null)
                                            return;
                                        search = hibernate.searchAuthor(null, id).get(0);
                                    }
                                    catch (NullPointerException ignored){}

                                    if(search == null)
                                    {
                                        System.out.println(
                                                """
                                                    
                                                    Nem találtam ilyen nevű szerzőt!
                                                    szeretnél felvenni a rendszerbe egy új szerzőt? (y/n)
                                                    (Ha nem veszel fel szerőt akkor a könyv felvétele sem kerül mentésre)
                                                    """
                                        );
                                        String newSzerzo = scn.nextLine();

                                        if(newSzerzo.startsWith("y"))
                                        {
                                            author = addAuthor(scn,hibernate);
                                            if(author == null)
                                                break;
                                        }
                                        else
                                        {
                                            System.out.println(
                                                    """
                                                        
                                                        > Szeretnél újra keresni szerzőt név alapján? (y/n)
                                                        """
                                            );
                                            String temp = scn.nextLine();
                                            if(temp.startsWith("n"))
                                            {
                                                vissza = true;
                                                break;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        author = search;

                                        Book newBook = hibernate.addBook(author, isbn, bookName, dop);

                                        author.addBookToList(newBook);
                                        hibernate.commit(newBook);

                                        List<Book> books= author.getBookList();
                                        books.add(newBook);

                                        hibernate.modifyAuthor(author, books,null, null,false);
                                        vissza = true;
                                        break;
                                    }
                                    if(author != null)
                                    {
                                        Book newBook = hibernate.addBook(author, isbn, bookName, dop);

                                        hibernate.commit(newBook);
                                        author.addBookToList(newBook);
                                        hibernate.commit(author);
                                        vissza= true;
                                        break;
                                    }
                                }
                            }
                            case "2" ->                         // BOOK SEARCH -- DONE
                            {
                                System.out.println(
                                        """
                                                   
                                                   Book Search
                                            =========================
                                            1 - Cím alapján
                                            2 - Szerző alapján
                                            3 - Isbn alapján
                                            
                                            0 - Vissza
                                            =========================
                                            """
                                );
                                String bs1 = scn.nextLine();
                                bs1 = String.valueOf(bs1.charAt(0));
                                switch (bs1)
                                {
                                    case "1" ->
                                    {
                                        printBooksWithTheTitle(hibernate, scn);

                                        sleep(5);
                                    }
                                    case "2" ->
                                    {
                                        System.out.println("\n> Add meg a szerző nevét:");
                                        String name = scn.nextLine();

                                        printAuthorsWithTheSameName(name,hibernate);


                                        Integer id = idBekero(scn);
                                        if(id == null)
                                            break;
                                        List<Book> books = hibernate.searchBook(null,null, name,id);

                                        if(books.size() ==0)
                                        {
                                            System.out.printf("\nEnnek a szerzőnek nincsen könyve! (%s)\n",name);
                                            sleep(5);
                                            break;
                                        }
                                        else
                                            books.forEach(System.out::println);
                                        sleep(5);
                                    }
                                    case "3"->
                                    {
                                        System.out.println("\n> Add meg a könyv Isbn-jét:");
                                        String isbn= scn.nextLine();

                                        Book book = hibernate.searchBook(isbn, null, null,null).get(0);

                                        if(book == null)
                                        {
                                            System.out.printf("\nNincsen könyv ezzel az Isbn számmal! (%s)\n",isbn);
                                            sleep(5);
                                            break;
                                        }
                                        else
                                            System.out.println(book);
                                        sleep(5);
                                    }
                                    default -> vissza = true;
                                }
                            }
                            case "3" ->                         // BOOK MODIFY -- DONE
                            {
                                boolean modBook = true;
                                while(modBook)
                                {
                                    System.out.println(
                                            """
                                                
                                                      Modify Books
                                                =========================
                                                1 - Cím módosítás
                                                2 - Szerző változtatás
                                                3 - Kiadási dátum váltás
                                                4 - Isbn módosítás
                                                
                                                0 - Vissza
                                                =========================
                                                """
                                    );
                                    String modifyBook = scn.nextLine();
                                    modifyBook = String.valueOf(modifyBook.charAt(0));

                                    switch(modifyBook)
                                    {
                                        case "1" ->     // CHANGE TITLE     -- DONE
                                        {
                                            Book book = getBookByName(hibernate, scn);
                                            if(book == null)
                                                break;
                                            System.out.println("\n> Mire szeretnéd változtatni a címét?");
                                            String newTitle = scn.nextLine();
                                            hibernate.modifyBook(book.getId(), null,null,newTitle,null);
                                        }
                                        case "2" ->     // CHANGE AUTHOR    -- DONE
                                        {
                                            Book book = getBookByName(hibernate, scn);
                                            if(book == null)
                                                break;
                                            Author author = getAuthorByName(hibernate,scn);
                                            if(author == null)
                                                break;
                                            hibernate.modifyBook(book.getId(),author,null,null,null);
                                        }
                                        case "3" ->     // DOP CHANGE       -- DONE
                                        {
                                            Book book = getBookByName(hibernate,scn);
                                            if(book == null)
                                                break;
                                            System.out.println("\n> Írd be a az új kiadási dátumot:");
                                            Date dop = datumBekeroKiado(scn);
                                            if(dop== null)
                                                break;
                                            hibernate.modifyBook(book.getId(), null,null,null,dop);
                                        }
                                        case "4" ->     // CHANGE ISBN      -- DONE
                                        {
                                            Book book = getBookByName(hibernate,scn);
                                            if(book == null)
                                                break;
                                            System.out.println("\n> Írd be az új Isbn-t:");
                                            String isbn = scn.nextLine();
                                            hibernate.modifyBook(book.getId(),null,isbn,null,null);
                                        }
                                        default -> modBook=false;
                                    }
                                }

                            }
                            case "4" ->                         // Forgalomba HELYEZES -- DONE
                            {
                                Book book = getBookByName(hibernate, scn);
                                if(book == null)
                                    break;
                                if(book.isInMarket())
                                {
                                    System.out.println("\n> Biztosan ki akarod vezetni forgalomból? (y/n)");
                                    String igenNem = scn.nextLine();

                                    if(igenNem.startsWith("y"))
                                        hibernate.changeBookMarket(book, false);
                                }
                                else
                                {
                                    System.out.println("\n> Biztosan forgalomba akarod helyizni a könyvet? (y/n)");
                                    String igenNem = scn.nextLine();

                                    if(igenNem.startsWith("y"))
                                        hibernate.changeBookMarket(book, true);
                                }
                            }
                            default -> vissza = true;
                        }
                        //sleep(3);
                    }
                }

                case "2" ->                 // AUTHORS -- DONE
                {
                    System.out.println(
                            """
                                    
                                         Authors
                                =========================
                                1 - Szerző hozzáadása
                                2 - Szerző törlése
                                3 - Szerző módosítás
                                
                                0 - Vissza
                                =========================
                                """
                    );
                    String a1= scn.nextLine();
                    a1= String.valueOf(a1.charAt(0));

                    switch(a1)
                    {
                        case "3" ->             // MODIFY AUTHOR -- DONE
                        {
                            while(true)
                            {
                                System.out.println(
                                        """
                                                
                                                  Modify Author
                                            =========================
                                            1 - Név megváltoztatása
                                            2 - Születési év változtatás
                                            3 - Könyv hozzáadás
                                            
                                            0 - Vissza
                                            =========================
                                            """
                                );
                                String ach1 = scn.nextLine();
                                ach1 = String.valueOf(ach1.charAt(0));
                                switch(ach1)
                                {
                                    case "3"->          // ADD BOOK TO AUTHOR -- DONE ???
                                    {
                                        Author author = getAuthorByName(hibernate,scn);

                                        printBooksWithTheTitle(hibernate, scn);

                                        Integer bookId = idBekero(scn);
                                        if(bookId== null)
                                            break;

                                        Book book = hibernate.searchBook(null,null,null,bookId).get(0);
                                        List<Book> books= hibernate.searchBook(null,null,author.name,author.getId());
                                        books.add(book);
                                        
                                        hibernate.modifyAuthor(author, books,null,null,false);

                                    }
                                    case "1" ->             // CHANGE NAME -- DONE
                                    {
                                        Author author = getAuthorByName(hibernate, scn);
                                        System.out.println("\n> Mire akarod megváltozatni a nevét?:");
                                        String newName = scn.nextLine();
                                        hibernate.modifyAuthor(author,null,newName,null,false);
                                    }
                                    case "2" ->            // CHANGE DOB -- DONE
                                    {
                                        Author author = getAuthorByName(hibernate, scn);
                                        System.out.println("\n> Mire akarod megváltozatni a születési dátumát?");
                                        Date date = datumBekeroKiado(scn);
                                        hibernate.modifyAuthor(author,null,null,date,false);
                                    }
                                    default -> vissza = true;
                                }
                                if(vissza)
                                    break;
                            }
                        }
                        case "1" ->             // AUTHOR ADD    -- DONE
                        {
                            Author newAuthor = addAuthor(scn,hibernate);
                            if(newAuthor == null)
                                break;

                            hibernate.commit(newAuthor);
                        }
                        case "2" ->             // AUTHOR DELETE -- DONE
                        {
                            Author author = getAuthorByName(hibernate, scn);
                            System.out.println("\n> Biztosan törölni akarod? (y/n)");
                            String delete = scn.nextLine();
                            if(delete.startsWith("n"))
                            {
                                break;
                            }
                            else if(delete.startsWith("y"))
                            {
                                System.out.println("\n> törlés...");
                                sleep(5);
                                hibernate.modifyAuthor(author,null,null,null,true);
                            }
                            else
                            {
                                System.out.println("\n >Valami nem jó tesa<");
                                break;
                            }
                            System.out.println("> törlés beteljesítve");
                        }
                    }
                }
                case "3" ->                 // STORES
                {
                    

                }
                default -> {return;}
            }
        }
    }

    private static Author getAuthorByName(Hibernate hibernate, Scanner scn)
    {
        System.out.println("\n> Add meg a szerző nevét:");
        String name = scn.nextLine();
        List<Author> authors = hibernate.searchAuthor(name,null);

        if(authors.size()==0)
        {
            System.out.printf("\nNincsen szerző ezzel a névvel! (%s)\n",name);
            sleep(5);
            return null;
        }
        else
            authors.forEach(System.out::println);

        Integer id = idBekero(scn);
        if(id== null)
            return null;

        return hibernate.searchAuthor(null, id).get(0);
    }
    static Book getBookByName(Hibernate hibernate, Scanner scn)
    {
        System.out.println("\n> Add meg a könyv nevét:");
        String title = scn.nextLine();
        List<Book> books = hibernate.searchBook(null, title, null, null);

        if(books.size()==0)
        {
            System.out.printf("\nNincsen könyv ezzel a névvel! (%s)\n",title);
            sleep(5);
            return null;
        }
        else
            books.forEach(System.out::println);

        Integer id = idBekero(scn);
        if(id== null)
            return null;

        return hibernate.searchBook(null,null,null,id).get(0);
    }

    static void printBooksWithTheTitle(Hibernate hibernate, Scanner scn)
    {
        System.out.println("\n> Add meg a könyv címét:");
        String title = scn.nextLine();

        List<Book> books = hibernate.searchBook(null, title, null,null);

        if(books.size() ==0)
        {
            System.out.printf("\nNincsen könyv ezzel a névvel! (%s)\n",title);
            sleep(5);
        }
        else
            books.forEach(System.out::println);
    }

    static void sleep(int seconds)
    {
        try
        {
            TimeUnit.SECONDS.sleep(seconds);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    static Author addAuthor(Scanner scn, Hibernate hibernate)
    {
        String name;
        System.out.println("\n> Add meg a szerő nevét (0 = Kilepes):");
        name= scn.nextLine();

        if("0".equals(name))
            return null;

        System.out.println("\n> Add meg a szerző születési évét");
        Date dob = datumBekeroKiado(scn);

        return hibernate.authorAdd(name, dob);
    }

    static void printAuthorsWithTheSameName(String name, Hibernate hibernate)
    {
        List<Author> authors = hibernate.searchAuthor(name, null);
        if(authors.size()==0)
        {
            System.out.println("Nincs szerző ezzel a névvel");
            return;
        }
        authors.forEach(System.out::println);
    }

    static Date datumBekeroKiado(Scanner scn)
    {
        int year = -1;
        int month= -1;
        int day= -1;

        while(true)     // DATES
        {
            while(true)
            {
                try
                {
                    System.out.println("> Év:");
                    year = scn.nextInt(); scn.nextLine();

                    boolean honap = false;
                    while(!honap)
                    {
                        System.out.println(
                                """
                                    
                                    > 1 - Január
                                    > 2 - Február
                                    > 3 - Március
                                    > 4 - Április
                                    > 5 - Május
                                    > 6 - Június
                                    > 7 - Július
                                    > 8 - Augusztus
                                    > 9 - Szeptember
                                    > 10 - Október
                                    > 11 - November
                                    > 12 - December
                                    """
                        );
                        month = scn.nextInt(); scn.nextLine();
                        switch (month)
                        {
                            case 1,2,3,4,5,6,7,8,9,10,11,12 -> honap = true;
                            default -> System.out.println("\nnincsen ilyen hónap tesa");
                        }
                    }

                    while(true)
                    {
                        System.out.println("\n> Nap (szám):");
                        day = scn.nextInt(); scn.nextLine();

                        if(!(day < 0) && !(day > 31))
                        {
                            break;
                        }
                        else System.out.println("\nNincsen ilyen nap tesa\n");
                    }


                }
                catch (InputMismatchException e)
                {
                    System.out.println("Nem jó input tesa");
                    e.printStackTrace();
                }
                break;
            }
            break;
        }
        switch (month)
        {
            case 1 -> {return new GregorianCalendar(year, Calendar.JANUARY, day).getTime();}
            case 2 -> {return new GregorianCalendar(year, Calendar.FEBRUARY, day).getTime();}
            case 3 -> {return new GregorianCalendar(year, Calendar.MARCH, day).getTime();}
            case 4 -> {return new GregorianCalendar(year, Calendar.APRIL, day).getTime();}
            case 5 -> {return new GregorianCalendar(year, Calendar.MAY, day).getTime();}
            case 6 -> {return new GregorianCalendar(year, Calendar.JUNE, day).getTime();}
            case 7 -> {return new GregorianCalendar(year, Calendar.JULY, day).getTime();}
            case 8 -> {return new GregorianCalendar(year, Calendar.AUGUST, day).getTime();}
            case 9 -> {return new GregorianCalendar(year, Calendar.SEPTEMBER, day).getTime();}
            case 10 -> {return new GregorianCalendar(year, Calendar.OCTOBER, day).getTime();}
            case 11 -> {return new GregorianCalendar(year, Calendar.NOVEMBER, day).getTime();}
            case 12 -> {return new GregorianCalendar(year, Calendar.DECEMBER, day).getTime();}
        }
        return null;
    }

    static Integer idBekero(Scanner scn)
    {
        Integer id= null;
        while(id == null)
        {
            try
            {
                System.out.println("> Add meg annak az Id-ját amit használni szeretnél (-1 = Kilépés):");
                id = scn.nextInt(); scn.nextLine();

                if(id == -1)
                    return null;
            }
            catch (InputMismatchException e)
            {
                System.out.println("Rossz input tesa!");
                e.printStackTrace();
            }
        }
        return id;
    }
}

