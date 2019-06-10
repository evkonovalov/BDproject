import psycopg2
import datetime
import sys

def make_output(sql_table, genres=None):
    s = ''
    for elem in sql_table:
        if genres is None:
            s += '#'.join(map(str, elem)) + '\n'
        else:
            s += '#'.join(list(map(str, elem)) + list(map(str, genres))) + '\n'
    return s

def calc_date_diff(dates):
    d0 = datetime.datetime.now().date()
    sum_diff = 0
    for elem in dates:
        delta = d0 - elem
        sum_diff += delta.days
    return sum_diff


class db:
    def __init__(self):
        self.conn_ = None
        self.cur_ = None
    
    def connect(self, dbname, user, password, port, host='localhost'):
        self.conn_ = psycopg2.connect(
            """dbname='{}' 
            user='{}' 
            host='{}' 
            port='{}' 
            password='{}'""".format(dbname, user, host, port, password))
        self.cur_ = self.conn_.cursor()
        self.update_fine()
    
    def commit(self):
        self.conn_.commit()

    def close(self):
        self.cur_.close()
        self.conn_.close()
        self.cur_ = None
        self.conn_ = None

    def update_fine(self):
        self.cur_.execute("""select clientid from client""")
        client_ids = map(lambda x: x[0], self.cur_.fetchall())
        for clientid in client_ids:
            self.cur_.execute("""select dateto from "order" where dateto<current_date and active=true and clientid={}""".format(clientid))
            dates = list(map(lambda x: x[0], self.cur_.fetchall()))
            new_fine = 0
            if len(dates) == 0:
                continue
            else:
                new_fine = calc_date_diff(dates)
            self.cur_.execute("""update client set fine={} where clientid={}""".format(new_fine, clientid))
        self.commit()

    def increase_book_quantity_(self, bookid, libraryid, quantity_to_add):
        self.cur_.execute("""update public.book 
        set quantity=quantity+{} where bookid={} and libraryid={}""".format(quantity_to_add, bookid, libraryid))
        return

    def id_from_name_(self, name, table):
        assert table in ['client', 'genre', 'library', 'author'], 'Wrong value of argument table'
        self.cur_.execute("""select {}id from public.{} where name='{}'""".format(table, table, name))
        tmp_res = self.cur_.fetchone()
        if tmp_res is None:
            return None
        return tmp_res[0]

    def format_date_(self, out):
        for i in range(len(out)):
            for j in range(len(out[i])):
                if type(out[i][j]) == datetime.date:
                    out[i] = out[i][:j] + tuple([out[i][j].strftime('%Y-%m-%d'),]) + out[i][j+1:]
        return out

    def bookid_from_name_and_lib_(self, book_name, libraryid):
        self.cur_.execute("""select bookid from public.book where name='{}' and libraryid={}""".format(book_name, libraryid))
        tmp_res = self.cur_.fetchone()
        if tmp_res is None:
            return None
        return tmp_res[0]


    def add_client(self, client_name, client_addr):
        self.cur_.execute("""insert into public.client (name, address, checkindate, fine)
        values ('{}', '{}', NOW(), 0)
        on conflict (name) do nothing""".format(client_name, client_addr))
        self.commit()
        return

    def add_order(self, client_name, book_name, library_name):
        clientid = self.id_from_name_(client_name, 'client')
        libraryid = self.id_from_name_(library_name, 'library')
        if clientid is None or libraryid is None:
            return 1
        bookid = self.bookid_from_name_and_lib_(book_name, libraryid)
        if bookid is None:
            return 1
        self.cur_.execute("""select bookid from public.book 
        where bookid={} and libraryid={} and quantity>0""".format(bookid, libraryid))
        check = self.cur_.fetchone()
        if check is None:
            return 1
        self.cur_.execute("""insert into public.order (clientid, bookid, libraryid, datefrom, dateto, active)
        values ({}, {}, {}, NOW(), NOW()::date + 14, true)""".format(clientid, bookid, libraryid))
        self.cur_.execute("""update public.book set quantity=quantity-1 
        where bookid={} and libraryid={}""".format(bookid, libraryid))
        self.commit()
        return 0

    def add_book(self, book_name, isbn, author_name, library_name, quantity, book_genres):
        libraryid = self.id_from_name_(library_name, 'library')
        if libraryid is None:
            return 1
        bookid = self.bookid_from_name_and_lib_(book_name, libraryid)
        if bookid is not None:
            self.increase_book_quantity_(bookid, libraryid, quantity)
            self.commit()
            return 0
        else:
            self.cur_.execute("""insert into public.book (name, isbn, quantity, libraryid) values ('{}', '{}', {}, {})
            returning bookid""".format(book_name, isbn, quantity, libraryid))
            bookid = self.cur_.fetchone()[0]
        authorid = self.id_from_name_(author_name, 'author')
        if authorid is None:
            self.cur_.execute("""insert into public.author (name, century) values ('{}', NULL)
            returning authorid""".format(author_name))
            authorid = self.cur_.fetchone()[0]
        self.cur_.execute("""insert into public.author_book (authorid, bookid) values ({}, {})""".format(authorid, bookid))
        for genre_name in book_genres:
            genreid = self.id_from_name_(genre_name, 'genre')
            if genreid is None:
                self.cur_.execute("""insert into public.genre (name) values ('{}')
                returning genreid""".format(genre_name))
                genreid = self.cur_.fetchone()[0]
            self.cur_.execute("""insert into public.genre_book (genreid, bookid) 
            values ({}, {})""".format(genreid, bookid)) 
        self.commit()        
        return 0

    def edit_client(self, old_client_name, new_client_name=None, new_client_addr=None):
        if new_client_name is None and new_client_addr is not None:
            self.cur_.execute("""update client
            set address='{}' where "name"='{}'
            returning "name";""".format(new_client_addr, old_client_name))
        elif new_client_name is not None and new_client_addr is None:
            self.cur_.execute("""update client
            set "name"='{}' where "name"='{}'
            returning "name";""".format(new_client_name, old_client_name))
        elif new_client_name is not None and new_client_addr is not None:
            self.cur_.execute("""update client
            set "name"='{}', address='{}' where "name"='{}'
            returning "name";""".format(new_client_name, new_client_addr, old_client_name))
        else:
            return 1
        tmp_lst = self.cur_.fetchall()
        if len(tmp_lst) == 0:
            return 1
        self.commit()
        return 0

    def close_order(self, client_name, book_name, library_name):
        self.cur_.execute("""update "order" set active = false, dateto = current_date
        from "order"
        join client on "order".clientid = client.clientid
        join book on "order".bookid = book.bookid
        join library on "order".libraryid = library.libraryid
        where client."name" = '{}' and book."name" = '{}' and library."name" = '{}'
        returning orderid""".format(client_name, book_name, library_name))
        tmp_lst = self.cur_.fetchall()
        if len(tmp_lst) == 0:
            return 1
        self.commit()
        return 0

    def extend_order(self, client_name, book_name, library_name, date):
        self.cur_.execute("""update "order" set dateto = '{}'
        from "order"
        join client on "order".clientid = client.clientid
        join book on "order".bookid = book.bookid
        join library on "order".libraryid = library.libraryid
        where client."name" = '{}' and book."name" = '{}' and library."name" = '{}'
        returning orderid""".format(date, client_name, book_name, library_name))
        tmp_lst = self.cur_.fetchall()
        if len(tmp_lst) == 0:
            return 1
        self.commit()
        return 0

    def remove_fine(self, client_name):
        self.cur_.execute("""update client set fine = 0 where "name" = '{}' returning clientid""".format(client_name))
        tmp_lst = self.cur_.fetchall()
        if len(tmp_lst) == 0:
            return 1
        self.commit()
        return 0

    def decrease_book_quantity(self, book_name, library_name, quantity):
        # quantity - количество, на которое уменьшить
        # возвращает 0 в случае успешного завершения, 1 если книги или библиотеки нет, или в наличии меньше книг, чем хотим убрать
        libraryid = self.id_from_name_(library_name, 'library')
        if libraryid is None:
            return 1
        bookid = self.bookid_from_name_and_lib_(book_name, libraryid)
        if bookid is None:
            return 1
        self.cur_.execute("""update public.book 
        set quantity=quantity-{} where bookid={} and quantity>={}
        returning bookid""".format(quantity, bookid, quantity))
        tmp_lst = self.cur_.fetchall()
        if len(tmp_lst) == 0:
            return 1
        self.commit()
        return 0

    def get_clients_by_name(self, client_name):
        self.cur_.execute("""select "name", address, checkindate, fine from client where "name" = '{}';""".format(client_name))
        return make_output(self.format_date_(self.cur_.fetchall()))

    def get_clients_by_addr(self, client_addr):
        self.cur_.execute("""select "name", address, checkindate, fine from client where address = '{}';""".format(client_addr))
        return make_output(self.format_date_(self.cur_.fetchall()))

    def get_clients_by_fine(self, fine):
        self.cur_.execute("""select "name", address, checkindate, fine from client where fine > {};""".format(fine))
        return make_output(self.format_date_(self.cur_.fetchall()))
    
    def get_clients_by_date(self, date):
        self.cur_.execute("""select "name", address, checkindate, fine from client where checkindate = '{}';""".format(date))
        return make_output(self.format_date_(self.cur_.fetchall()))

    def get_orders_by_client(self, client_name, only_active, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto, "order".active
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where client.name = '{}'
            and ("order".active = true or "order".active = {});""".format(client_name, bool(only_active)))
        else:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto, "order".active
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where client.name = '{}'
            and ("order".active = true or "order".active = {})
            and library.name = '{}';""".format(client_name, bool(only_active), library_name))
        return make_output(self.format_date_(self.cur_.fetchall()))
    
    def get_orders_by_date(self, date, only_active, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto, "order".active
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where "order".datefrom = '{}'
            and ("order".active = true or "order".active = {});""".format(date, bool(only_active)))
        else:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto, "order".active
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where "order".datefrom = '{}'
            and ("order".active = true or "order".active = {})
            and library.name = '{}';""".format(date, bool(only_active), library_name))
        return make_output(self.format_date_(self.cur_.fetchall()))
    
    def get_overdue_orders(self, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where "order".dateto <= current_date;""")
        else:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where "order".dateto <= current_date and library.name = '{}';""".format(library_name))     
        return make_output(self.format_date_(self.cur_.fetchall()))

    def get_orders_by_book(self, book_name, only_active, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto, "order".active
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where book."name" = '{}'
            and ("order".active = true or "order".active = {});""".format(book_name, bool(only_active)))
        else:
            self.cur_.execute("""select book."name", library.name, client."name", "order".datefrom, "order".dateto, "order".active
            from client
            join "order" on (client.clientid = "order".clientid)
            join library on ("order".libraryid = library.libraryid)
            join book on ("order".bookid = book.bookid)
            where book."name" = '{}'
            and ("order".active = true or "order".active = {})
            and library.name = '{}';""".format(book_name, bool(only_active), library_name))
        return make_output(self.format_date_(self.cur_.fetchall()))
    
    def get_books_by_name(self, book_name, library_name=None):
        self.cur_.execute("""select distinct genre.name
        from book
        join genre_book on (book.bookid = genre_book.bookid)
        join genre on genre.genreid = genre_book.genreid
        where book."name" = '{}'""".format(book_name))
        genre_list = list(map(lambda x: x[0], self.cur_.fetchall()))
        if library_name is None:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join library on (book.libraryid = library.libraryid)
            where book."name" = '{}';""".format(book_name))
        else:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join library on (book.libraryid = library.libraryid)
            where book."name" = '{}' and library.name = '{}';""".format(book_name, library_name))
        return make_output(self.cur_.fetchall(), genre_list)

    def get_books_by_author(self, author_name, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join library on (book.libraryid = library.libraryid)
            where author."name" = '{}';""".format(author_name))
        else:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join library on (book.libraryid = library.libraryid)
            where author."name" = '{}' and library.name = '{}';""".format(author_name, library_name))
        return make_output(self.cur_.fetchall())

    def get_books_by_genre(self, genre_name, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity, genre."name"
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join genre_book on (book.bookid = genre_book.bookid)
            join genre on (genre_book.genreid = genre.genreid)
            join library on (book.libraryid = library.libraryid)
            where genre."name" = '{}'""".format(genre_name))
        else:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity, genre."name"
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join genre_book on (book.bookid = genre_book.bookid)
            join genre on (genre_book.genreid = genre.genreid)
            join library on (book.libraryid = library.libraryid)
            where genre."name" = '{}' and library.name = '{}'""".format(genre_name, library_name))
        return make_output(self.cur_.fetchall())

    def get_books_by_author_and_genre(self, author_name, genre_name, library_name=None):
        if library_name is None:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity, genre."name"
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join genre_book on (book.bookid = genre_book.bookid)
            join genre on (genre_book.genreid = genre.genreid)
            join library on (book.libraryid = library.libraryid)
            where genre."name" = '{}' and author.name = '{}'""".format(genre_name, author_name))
        else:
            self.cur_.execute("""select book."name", author."name", library.name, book.quantity, genre."name"
            from book
            join author_book on (book.bookid = author_book.bookid)
            join author on (author.authorid = author_book.authorid)
            join genre_book on (book.bookid = genre_book.bookid)
            join genre on (genre_book.genreid = genre.genreid)
            join library on (book.libraryid = library.libraryid)
            where genre."name" = '{}' and author.name = '{}' and library.name = '{}'""".format(genre_name, author_name, library_name))
        return make_output(self.cur_.fetchall())


library_system = db()
library_system.connect(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])
if sys.argv[5] == 'add_client':
    print(library_system.add_client(sys.argv[7], sys.argv[8]))
elif sys.argv[5] == 'add_order':
    print(library_system.add_order(sys.argv[7], sys.argv[8], sys.argv[9]))
elif sys.argv[5] == 'add_book':
    print(library_system.add_book(sys.argv[7], sys.argv[8], sys.argv[9], sys.argv[10], int(sys.argv[11]), sys.argv[12:len(sys.argv)]))
elif sys.argv[5] == 'edit_client':
    if sys.argv[6] == '0':
        print(library_system.edit_client(sys.argv[7], sys.argv[8]))
    elif sys.argv[6] == '1':
        print(library_system.edit_client(sys.argv[7], new_client_addr=sys.argv[8]))
    else:
        print(library_system.edit_client(sys.argv[7], sys.argv[8], sys.argv[9]))
elif sys.argv[5] == 'close_order':
    print(library_system.close_order(sys.argv[7], sys.argv[8], sys.argv[9]))
elif sys.argv[5] == 'extend_order':
    print(library_system.extend_order(sys.argv[7], sys.argv[8], sys.argv[9], sys.argv[10]))
elif sys.argv[5] == 'remove_fine':
    print(library_system.remove_fine(sys.argv[7]))
elif sys.argv[5] == 'decrease_book_quantity':
    print(library_system.decrease_book_quantity(sys.argv[7], sys.argv[8], int(sys.argv[9])))
elif sys.argv[5] == 'get_clients_by_name':
    print(library_system.get_clients_by_name(sys.argv[7]))
elif sys.argv[5] == 'get_clients_by_addr':
    print(library_system.get_clients_by_addr(sys.argv[7]))
elif sys.argv[5] == 'get_clients_by_date':
    print(library_system.get_clients_by_date(sys.argv[7]))
elif sys.argv[5] == 'get_clients_by_fine':
    print(library_system.get_clients_by_fine(float(sys.argv[7])))
elif sys.argv[5] == 'get_orders_by_client':
    if int(sys.argv[6]) == 2:
        print(library_system.get_orders_by_client(sys.argv[7], int(sys.argv[8])))
    else:
        print(library_system.get_orders_by_client(sys.argv[7], int(sys.argv[8],), sys.argv[9]))
elif sys.argv[5] == 'get_orders_by_date':
    if int(sys.argv[6]) == 2:
        print(library_system.get_orders_by_date(sys.argv[7], int(sys.argv[8])))
    else:
        print(library_system.get_orders_by_date(sys.argv[7], int(sys.argv[8],), sys.argv[9]))
elif sys.argv[5] == 'get_orders_by_book':
    if int(sys.argv[6]) == 2:
        print(library_system.get_orders_by_book(sys.argv[7], int(sys.argv[8])))
    else:
        print(library_system.get_orders_by_book(sys.argv[7], int(sys.argv[8],), sys.argv[9]))
elif sys.argv[5] == 'get_overdue_orders':
    if int(sys.argv[6]) == 0:
        print(library_system.get_overdue_orders())
    else:
        print(library_system.get_overdue_orders(sys.argv[7]))
elif sys.argv[5] == 'get_books_by_name':
    if int(sys.argv[6]) == 1:
        print(library_system.get_books_by_name(sys.argv[7]))
    else:
        print(library_system.get_books_by_name(sys.argv[7], sys.argv[8]))
elif sys.argv[5] == 'get_books_by_author':
    if int(sys.argv[6]) == 1:
        print(library_system.get_books_by_author(sys.argv[7]))
    else:
        print(library_system.get_books_by_author(sys.argv[7], sys.argv[8]))
elif sys.argv[5] == 'get_books_by_genre':
    if int(sys.argv[6]) == 1:
        print(library_system.get_books_by_genre(sys.argv[7]))
    else:
        print(library_system.get_books_by_genre(sys.argv[7], sys.argv[8]))
elif sys.argv[5] == 'get_books_by_author_and_genre':
    if int(sys.argv[6]) == 2:
        print(library_system.get_books_by_author_and_genre(sys.argv[7], int(sys.argv[8])))
    else:
        print(library_system.get_books_by_author_and_genre(sys.argv[7], int(sys.argv[8],), sys.argv[9]))
library_system.close()