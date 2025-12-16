package com.cinemastore.config;

import com.cinemastore.entity.*;
import com.cinemastore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Инициализация начальных данных при запуске приложения
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final StudioRepository studioRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public DataInitializer(UserRepository userRepository,
                          GenreRepository genreRepository,
                          StudioRepository studioRepository,
                          MovieRepository movieRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.studioRepository = studioRepository;
        this.movieRepository = movieRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public void run(String... args) {
        // Создаем пользователей, если их нет
        if (userRepository.count() == 0) {
            createUsers();
        }
        
        // Создаем жанры, если их нет
        if (genreRepository.count() == 0) {
            createGenres();
        }
        
        // Создаем студии, если их нет
        if (studioRepository.count() == 0) {
            createStudios();
        }
        
        // Создаем фильмы, если их нет
        if (movieRepository.count() == 0) {
            createMovies();
        }
    }
    
    private void createUsers() {
        // Администратор
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEmail("director@cinema.ru");
        admin.setFirstName("Александр");
        admin.setLastName("Волков");
        admin.setRole(Role.ADMIN);
        admin.setPhone("+7 (812) 555-11-22");
        userRepository.save(admin);
        
        // Менеджер
        User manager = new User();
        manager.setUsername("manager");
        manager.setPassword(passwordEncoder.encode("manager"));
        manager.setEmail("manager@cinema.ru");
        manager.setFirstName("Елена");
        manager.setLastName("Соколова");
        manager.setRole(Role.MANAGER);
        manager.setPhone("+7 (812) 555-22-33");
        userRepository.save(manager);
        
        // Кассир
        User cashier = new User();
        cashier.setUsername("cashier");
        cashier.setPassword(passwordEncoder.encode("cashier"));
        cashier.setEmail("kassa@cinema.ru");
        cashier.setFirstName("Дмитрий");
        cashier.setLastName("Новиков");
        cashier.setRole(Role.CASHIER);
        cashier.setPhone("+7 (812) 555-33-44");
        userRepository.save(cashier);
        
        // Зритель
        User customer = new User();
        customer.setUsername("customer");
        customer.setPassword(passwordEncoder.encode("customer"));
        customer.setEmail("ivanov@email.ru");
        customer.setFirstName("Сергей");
        customer.setLastName("Иванов");
        customer.setRole(Role.CUSTOMER);
        customer.setPhone("+7 (812) 555-44-55");
        customer.setAddress("г. Санкт-Петербург, пр. Невский, д. 28, кв. 15");
        userRepository.save(customer);
        
        System.out.println("✓ Пользователи созданы:");
        System.out.println("  - admin / admin (Администратор)");
        System.out.println("  - manager / manager (Менеджер)");
        System.out.println("  - cashier / cashier (Кассир)");
        System.out.println("  - customer / customer (Зритель)");
    }
    
    private void createGenres() {
        genreRepository.save(new Genre("Боевик", "Экшн-фильмы с динамичными сценами и погонями"));
        genreRepository.save(new Genre("Комедия", "Веселые и смешные фильмы для поднятия настроения"));
        genreRepository.save(new Genre("Драма", "Серьезные фильмы с глубоким сюжетом"));
        genreRepository.save(new Genre("Фантастика", "Фильмы о будущем, космосе и технологиях"));
        genreRepository.save(new Genre("Ужасы", "Страшные фильмы с элементами мистики"));
        genreRepository.save(new Genre("Триллер", "Напряженные фильмы с интригующим сюжетом"));
        genreRepository.save(new Genre("Мелодрама", "Романтические истории о любви"));
        genreRepository.save(new Genre("Приключения", "Захватывающие путешествия и открытия"));
        
        System.out.println("✓ Жанры созданы");
    }
    
    private void createStudios() {
        Studio studio1 = new Studio();
        studio1.setCompanyName("ООО \"Киностудия Мосфильм\"");
        studio1.setContactPerson("Иванов Петр Сергеевич");
        studio1.setPhone("+7 (495) 111-22-33");
        studio1.setEmail("info@mosfilm.ru");
        studio1.setAddress("г. Москва, ул. Мосфильмовская, д. 1");
        studio1.setDescription("Крупнейшая киностудия России");
        studioRepository.save(studio1);
        
        Studio studio2 = new Studio();
        studio2.setCompanyName("ООО \"Ленфильм\"");
        studio2.setContactPerson("Петрова Анна Владимировна");
        studio2.setPhone("+7 (812) 222-33-44");
        studio2.setEmail("contact@lenfilm.ru");
        studio2.setAddress("г. Санкт-Петербург, Каменноостровский пр., д. 10");
        studio2.setDescription("Старейшая киностудия России");
        studioRepository.save(studio2);
        
        Studio studio3 = new Studio();
        studio3.setCompanyName("ООО \"СТВ\"");
        studio3.setContactPerson("Смирнов Игорь Петрович");
        studio3.setPhone("+7 (812) 333-44-55");
        studio3.setEmail("info@ctv.ru");
        studio3.setAddress("г. Санкт-Петербург, ул. Караванная, д. 12");
        studio3.setDescription("Производство качественного российского кино");
        studioRepository.save(studio3);
        
        Studio studio4 = new Studio();
        studio4.setCompanyName("ООО \"Кинокомпания СТВ\"");
        studio4.setContactPerson("Кузнецова Татьяна Александровна");
        studio4.setPhone("+7 (495) 444-55-66");
        studio4.setEmail("contact@ctvfilm.ru");
        studio4.setAddress("г. Москва, ул. Тверская, д. 7");
        studio4.setDescription("Современное российское кино");
        studioRepository.save(studio4);
        
        System.out.println("✓ Студии созданы");
    }
    
    private void createMovies() {
        Genre action = genreRepository.findByName("Боевик").orElse(null);
        Genre comedy = genreRepository.findByName("Комедия").orElse(null);
        Genre drama = genreRepository.findByName("Драма").orElse(null);
        Genre sciFi = genreRepository.findByName("Фантастика").orElse(null);
        Genre horror = genreRepository.findByName("Ужасы").orElse(null);
        Genre thriller = genreRepository.findByName("Триллер").orElse(null);
        Genre romance = genreRepository.findByName("Мелодрама").orElse(null);
        Genre adventure = genreRepository.findByName("Приключения").orElse(null);
        
        Studio mosfilm = studioRepository.findByCompanyName("ООО \"Киностудия Мосфильм\"").orElse(null);
        Studio lenfilm = studioRepository.findByCompanyName("ООО \"Ленфильм\"").orElse(null);
        Studio stv = studioRepository.findByCompanyName("ООО \"СТВ\"").orElse(null);
        Studio ctv = studioRepository.findByCompanyName("ООО \"Кинокомпания СТВ\"").orElse(null);
        
        // Боевики
        createMovie("Терминатор", "Классический боевик о киборге из будущего", new BigDecimal("350.00"), 107, action, mosfilm, LocalDate.of(2023, 1, 15), new BigDecimal("8.5"));
        createMovie("Матрица", "Фантастический боевик о виртуальной реальности", new BigDecimal("400.00"), 136, sciFi, stv, LocalDate.of(2023, 3, 20), new BigDecimal("9.2"));
        createMovie("Безумный Макс", "Постапокалиптический боевик", new BigDecimal("380.00"), 120, action, ctv, LocalDate.of(2023, 5, 10), new BigDecimal("8.8"));
        
        // Комедии
        createMovie("Ирония судьбы", "Классическая новогодняя комедия", new BigDecimal("300.00"), 184, comedy, mosfilm, LocalDate.of(2022, 12, 1), new BigDecimal("9.0"));
        createMovie("Операция Ы", "Легендарная комедия Леонида Гайдая", new BigDecimal("280.00"), 95, comedy, lenfilm, LocalDate.of(2022, 11, 15), new BigDecimal("9.5"));
        createMovie("Джентльмены", "Современная комедия-боевик", new BigDecimal("350.00"), 113, comedy, stv, LocalDate.of(2023, 2, 5), new BigDecimal("8.3"));
        
        // Драмы
        createMovie("Легенда №17", "Драма о хоккеисте Валерии Харламове", new BigDecimal("320.00"), 134, drama, mosfilm, LocalDate.of(2023, 4, 12), new BigDecimal("8.7"));
        createMovie("Движение вверх", "Спортивная драма о победе сборной СССР", new BigDecimal("340.00"), 133, drama, stv, LocalDate.of(2023, 6, 1), new BigDecimal("8.9"));
        createMovie("Время первых", "Драма о первом выходе в открытый космос", new BigDecimal("330.00"), 140, drama, ctv, LocalDate.of(2023, 7, 20), new BigDecimal("8.6"));
        
        // Фантастика
        createMovie("Интерстеллар", "Эпическая фантастика о путешествии в космос", new BigDecimal("420.00"), 169, sciFi, stv, LocalDate.of(2023, 8, 15), new BigDecimal("9.1"));
        createMovie("Бегущий по лезвию", "Киберпанк-фантастика", new BigDecimal("380.00"), 117, sciFi, ctv, LocalDate.of(2023, 9, 5), new BigDecimal("8.4"));
        
        // Ужасы
        createMovie("Оно", "Экранизация романа Стивена Кинга", new BigDecimal("360.00"), 135, horror, stv, LocalDate.of(2023, 10, 1), new BigDecimal("7.8"));
        createMovie("Сияние", "Классический фильм ужасов", new BigDecimal("340.00"), 146, horror, lenfilm, LocalDate.of(2023, 10, 20), new BigDecimal("8.9"));
        
        // Триллеры
        createMovie("Исчезнувшая", "Психологический триллер", new BigDecimal("370.00"), 149, thriller, ctv, LocalDate.of(2023, 11, 10), new BigDecimal("8.2"));
        createMovie("Семь", "Детективный триллер", new BigDecimal("350.00"), 127, thriller, stv, LocalDate.of(2023, 11, 25), new BigDecimal("8.7"));
        
        // Мелодрамы
        createMovie("Титаник", "Эпическая романтическая драма", new BigDecimal("400.00"), 194, romance, mosfilm, LocalDate.of(2023, 12, 1), new BigDecimal("9.0"));
        createMovie("Влюбленные", "Современная романтическая комедия", new BigDecimal("320.00"), 102, romance, ctv, LocalDate.of(2023, 12, 15), new BigDecimal("7.9"));
        
        // Приключения
        createMovie("Индиана Джонс", "Приключенческий фильм о археологе", new BigDecimal("380.00"), 122, adventure, stv, LocalDate.of(2023, 1, 20), new BigDecimal("8.6"));
        createMovie("Пираты Карибского моря", "Морские приключения", new BigDecimal("390.00"), 143, adventure, ctv, LocalDate.of(2023, 2, 10), new BigDecimal("8.5"));
        
        System.out.println("✓ Фильмы созданы");
    }
    
    private void createMovie(String name, String description, BigDecimal price, int duration,
                            Genre genre, Studio studio, LocalDate releaseDate, BigDecimal rating) {
        Movie movie = new Movie();
        movie.setName(name);
        movie.setDescription(description);
        movie.setPrice(price);
        movie.setDuration(duration);
        movie.setGenre(genre);
        movie.setStudio(studio);
        movie.setReleaseDate(releaseDate);
        movie.setRating(rating);
        movie.setAvailable(true);
        movieRepository.save(movie);
    }
}


