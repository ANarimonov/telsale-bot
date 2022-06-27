package com.anarimonov.phonesalebot.bot;

import com.anarimonov.phonesalebot.model.*;
import com.anarimonov.phonesalebot.model.abs.AbsEntity;
import com.anarimonov.phonesalebot.model.dto.ProductDto;
import com.anarimonov.phonesalebot.repository.*;
import com.anarimonov.phonesalebot.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.PromoteChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static com.anarimonov.phonesalebot.utils.Constants.*;


@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final UserActivityService userActivityService;
    private final ChannelRepository channelRepo;
    private final BrandRepository brandRepo;
    private final ProductRepository productRepo;
    private final ColorRepository colorRepo;
    private final StorageRepository storageRepo;
    private final CountryRepository countryRepo;
    private final Map<Long, ProductDto> productDtoMap = new HashMap<>();
    private final Map<Long, Product> productMap = new HashMap<>();
    private final PenaltyRepository penaltyRepo;
    private final BatteryRepository batteryRepo;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        UserActivity userActivity = checkUserActivity(update);
        if (update.hasMessage()) {
            if (checkJoinedChannels(userActivity))
                getMessage(update, userActivity);
        } else if (update.hasCallbackQuery()) {
            getCallBackQuery(update, userActivity);
        }
    }

    private boolean checkJoinedChannels(UserActivity userActivity) {
        List<Channel> all = channelRepo.findAll();
        Long userId = userActivity.getUser().getId();
        for (Channel channel : all) {
            PromoteChatMember promoteChatMember = new PromoteChatMember("@" + channel.getChannelId(), userId);
            try {
                execute(promoteChatMember);
            } catch (TelegramApiException e) {
                if (e.getMessage().equals("Error promoting chat member: [400] Bad Request: bots can't add new chat members")) {
                    sendTextMessage(userActivity, userActivity.getLanguageCode().equals("uz") ? "Iltimos, avvalo kanallarga obuna bo'ling va qayta \n/start buyrug'ini yuboring"
                            : "Пожалуйста, подпишитесь на каналы сначала и зарегистрируйтесь снова\nОтправьте команду /start");
                    return false;
                }
            }
        }
        return true;
    }

    private void getCallBackQuery(Update update, UserActivity userActivity) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        switch (userActivity.getStep()) {
            case 1 -> {

            }
            case 2 -> {
                String data = callbackQuery.getData();
                System.out.println(data);
            }
        }
    }

    private void getMessage(Update update, UserActivity userActivity) {
        Message message = update.getMessage();
        if (userActivity.getStep() == 30 && userActivity.getRole().equals("admin"))
            if (message.hasText() && !(message.getText().equals("\uD83D\uDD1D Asosiy Menyu") || message.getText().equals("\uD83D\uDD1D Главное Меню") || message.getText().equals("/start"))) {
                for (User user : userActivityService.userRepository().findAll())
                    sendForwardMessage(user.getId().toString(), message.getFrom().getId().toString(), message.getMessageId());
                userActivity.setStep(0);
            }
        if (message.hasText()) {
            switch (message.getText()) {
                case "/start", "\uD83D\uDD1D Asosiy Menyu", "\uD83D\uDD1D Главное Меню" -> startMessage(userActivity);
                case "Новые цены \uD83D\uDCB2", "Yangi narxlar \uD83D\uDCB2" -> getNewPrices(userActivity);
                case "Связаться с админом", "Adminga murojaat qilish" -> connectToAdmin(userActivity);
                case "Sozlamalar ⚙️", "Опции ⚙️" -> settings(userActivity);
                case "Telefonni narxlash \uD83D\uDCB8", "Ценообразование телефонов\uD83D\uDCB2" ->
                        pricingMyPhone(userActivity);
                default -> getDefaultMessage(message, userActivity);
            }
        }
        userActivityService.update(userActivity);
    }

    private void pricingMyPhone(UserActivity userActivity) {
        sendTextMessage(userActivity.setStep(4), userActivity.getLanguageCode().equals("uz") ?
                "Kategoriyalardan birini tanlang" : "Выберите одну из категорий");
    }

    private void settings(UserActivity userActivity) {

        sendTextMessage(userActivity.setStep(2),
                userActivity.getLanguageCode().equals("uz") ?
                        "Kategoriyalardan birini tanlang" : "Выберите одну из категорий");
    }

    private void connectToAdmin(UserActivity userActivity) {
        sendTextMessage(userActivity.setStep(0), userActivity.getLanguageCode().equals("uz") ? "Adminga bog'lanish uchun ushbu \"username\" ustiga bosing ➡️ @" +
                getAdmin().getUsername() : "Нажмите на это «имя пользователя», чтобы связаться с администратором ➡️@" + getAdmin().getUsername());
    }

    private StringBuilder getProductList(Brand byName) {
        List<Product> products;
        products = productRepo.findByBrandId(byName.getId());
        StringBuilder productList = new StringBuilder();
        for (Product product1 : products) {
            productList.append("ID:").append(product1.getId()).append(", ").append(product1.getName()).append("\n");
        }
        productList.append("Mahsulot IDsini kiriting.");
        return productList;
    }

    private void getDefaultMessage(Message message, UserActivity userActivity) {
        String text = message.getText();
        Long userId = userActivity.getUser().getId();
        String langCode = userActivity.getLanguageCode();
        int step = userActivity.getStep();
        ProductDto productDto = productDtoMap.get(userId);
        if (productDto == null)
            productDto = new ProductDto();
        if (userActivity.getRole().equals("user")) {
            switch (step) {
                case 1 -> {
                    Brand byName = brandRepo.findByName(text);
                    if (byName != null) {
                        sendTextMessage(userActivity.setStep(0), byName.getMessage());
                    } else getNewPrices(userActivity);
                }
                case 2 -> {
                    if (text.equals("Сменить язык \uD83C\uDF0E") || text.equals("Tilni o`zgartirish \uD83C\uDF0E"))
                        changeLanguage(userActivity);
                }
                case 3 -> {
                    if (text.equals("O'zbek \uD83C\uDDFA\uD83C\uDDFF"))
                        userActivity.setLanguageCode("uz");
                    else if (text.equals("Русский \uD83C\uDDF7\uD83C\uDDFA")) userActivity.setLanguageCode("ru");
                    sendTextMessage(userActivity.setStep(0), userActivity.getLanguageCode().equals("uz") ? "Til o'rnatildi" : "Язык установлен");
                }
                case 4 -> {
                    Brand brand = brandRepo.findByName(text);
                    if (brand != null) {
                        productDto.setBrand(brand.getName());
                        productDtoMap.put(userId, productDto);
                        sendTextMessage(userActivity.setStep(5), langCode.equals("uz") ? "Telefon modelini tanlang" :
                                "Введите модель телефона");
                    }
                }
                case 5 -> {
                    Product product = productRepo.findByName(text);
                    if (product != null) {
                        productDto.setModel(text);
                        productDto.setPrice(product.getPrice());
                        productDto.setDamage1(product.getDamage1());
                        productDto.setDamage2(product.getDamage2());
                        productDto.setId(product.getId());
                        productDtoMap.put(userId, productDto);
                        if (product.getBrand().getName().equalsIgnoreCase("iphone")) {
                            sendTextMessage(userActivity.setStep(6), langCode.equals("uz") ? "Batareykangizni sig`imini kiriting!\uD83D\uDD0B" : "Введите емкость аккумулятора!\uD83D\uDD0B");
                        } else
                            sendTextMessage(userActivity.setStep(7), langCode.equals("uz") ? "Telefoningizni korobka dokumenti bormi?" :
                                    "У вас есть коробка и документы на ваш телефон?");
                    }
                }
                case 6 -> {
                    Battery byName = batteryRepo.findByName(text);
                    if (byName != null) {
                        Penalty penalty = penaltyRepo.findByBatteryId(byName.getId());
                        productDto.setBatteryCapacity(byName.getName());
                        productDto.setPrice(productDto.getPrice() - penalty.getAmount());
                        productDtoMap.put(userId, productDto);
                        sendTextMessage(userActivity.setStep(7), langCode.equals("uz") ? "Telefoningizni korobka dokumenti bormi?" :
                                "У вас есть коробка и документы на ваш телефон?");
                    }
                }
                case 7 -> {
                    if (text.equals("Ha") || text.equals("Да")) {
                        productDto.setDocuments(true);
                    } else if (text.equals("Yo'q") || text.equals("Нет")) {
                        productDto.setDocuments(false);
                        productDto.setPrice(productDto.getPrice() - productDto.getDocumentPenalty());
                    } else break;
                    productDtoMap.put(userId, productDto);
                    sendTextMessage(userActivity.setStep(8), langCode.equals("uz") ? "Telefoningiz rangini belgilang!" : "Выберите цвет вашего телефона!");
                }
                case 8 -> {
                    Color color = colorRepo.findByName(text);
                    if (color != null) {
                        productDto.setColor(text);
                        productDtoMap.put(userId, productDto);
                        sendTextMessage(userActivity.setStep(9), langCode.equals("uz") ? "Telefoningiz xotirasini belgilang!" :
                                "Укажите память вашего телефона!");
                    }
                }
                case 9 -> {
                    Storage storage = storageRepo.findByName(text);
                    if (storage != null) {
                        Penalty penalty = penaltyRepo.findByStorageId(storage.getId());
                        productDto.setStorage(text);
                        productDto.setPrice(productDto.getPrice() - penalty.getAmount());
                        productDtoMap.put(userId, productDto);
                        sendTextMessage(userActivity.setStep(10), langCode.equals("uz") ? "Telefoningiz ishlab chiqarilgan joyni kiriting!" :
                                "Введите место, где был изготовлен ваш телефон!");
                    }
                }
                case 10 -> {
                    Country country = countryRepo.findByName(text);
                    if (country != null) {
                        Penalty penalty = penaltyRepo.findByCountryId(country.getId());
                        productDto.setPrice(productDto.getPrice() - penalty.getAmount());
                        productDto.setCountry(text);
                        productDtoMap.put(userId, productDto);
                        sendTextMessage(userActivity.setStep(11), langCode.equals("uz") ? "Telefoningizga shikast yetganmi?" : "Ваш телефон поврежден?");
                    }
                }
                case 11 -> {
                    if (text.equals("Ha") || text.equals("Да")) {
                        sendTextMessage(userActivity.setStep(12), langCode.equals("uz") ? """
                                Telefoningizni necha foizi shikastlangan?

                                0-10% - telefon qirilgan, chaqasi bor, batareyka almashgan.

                                30-50% - ekran almashgan, barmoq skanneri ishlamaydi, Face ID ishlamaydi, juda katta miqdorda zarar yetgan, dog`i bor.""" :
                                """
                                        На сколько процентов поврежден ваш телефон?
                                            
                                        0-10% - телефон сломан, есть бут, батарея заменена.
                                            
                                        30-50% - заменен экран, не работает Touch ID пальцев, не работает Face ID, большое количество повреждений, есть пятна.""");
                    } else if (text.equals("Yo'q") || text.equals("Нет")) {
                        finallyMessage(userActivity);
                    }
                }
                case 12 -> {
                    if (text.equals("0~10%")) {
                        productDto.setPrice(productDto.getPrice() - productDto.getDamage1());
                    } else if (text.equals("30~50%")) {
                        productDto.setPrice(productDto.getPrice() - productDto.getDamage2());
                    } else break;
                    productDto.setDamage(text);
                    productDtoMap.put(userId, productDto);
                    finallyMessage(userActivity);
                }
            }
        } else if (userActivity.getRole().equals("admin")) {
            final String msg = "Bajariladigan amalni tanlang";
            Product product = productMap.get(userId);
            if (product == null)
                product = new Product();
            switch (text) {
                case "CRUD" -> sendTextMessage(userActivity.setStep(1), "Kategoriyalardan birini tanlang");
                case "Admin menu" -> sendTextMessage(userActivity.setStep(26), msg);
                case "Reklama jo'natish" -> sendTextMessage(userActivity.setStep(30), "Xabarni yuboring");
                case "Kanal CRUD" -> sendTextMessage(userActivity.setStep(2), msg);
                case "Mahsulot CRUD" -> sendTextMessage(userActivity.setStep(29), "Bittasini tanlang");
                case "Sotish uchun" -> sendTextMessage(userActivity.setStep(19), "Brandni tanlang");
                case "Olish uchun" -> sendTextMessage(userActivity.setStep(5), msg);
            }
            switch (step) {
                case 2 -> {
                    if (text.equals("Qo'shish"))
                        sendTextMessage(userActivity.setStep(3), "Kanal usernameni kiriting");
                    else if (text.equals("O'chirish"))
                        sendTextMessage(userActivity.setStep(4), "Kanal usernameni tanlang");
                }
                case 3 -> {
                    channelRepo.save(new Channel(text));
                    sendTextMessage(userActivity.setStep(1), "Kanal qo'shildi");
                }
                case 4 -> {
                    Channel channel = channelRepo.findByChannelId(text);
                    if (channel != null) {
                        channelRepo.delete(channel);
                        sendTextMessage(userActivity.setStep(1), "o'chirildi");
                    }
                }
                case 5 -> {
                    switch (text) {
                        case "Qo'shish" -> sendTextMessage(userActivity.setStep(step + 1), "Brandni tanlang");
                        case "O'chirish" -> sendTextMessage(userActivity.setStep(21), "Brandni tanlang");
                        case "Narxini o'zgartirish" -> sendTextMessage(userActivity.setStep(23), "Brandni tanlang");
                    }
                }
                case 6, 19 -> {
                    Brand byName = brandRepo.findByName(text);
                    if (byName == null) byName = brandRepo.save(new Brand(text));
                    product.setBrand(byName);
                    productMap.put(userId, product);
                    if (step == 6) sendTextMessage(userActivity.setStep(7), "Modelni kiriting");
                    else sendTextMessage(userActivity.setStep(20), "Xabarni kiriting");
                }
                case 7 -> {
                    product.setName(text);
                    productMap.put(userId, product);
                    sendTextMessage(userActivity.setStep(8), "Ranglarini kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                }
                case 8 -> {
                    if (text.equals("Keyingi ➡️") && product.getColors() != null) {
                        sendTextMessage(userActivity.setStep(9), "Xotira sig'imlarini kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                        break;
                    }
                    Color byName = colorRepo.findByName(text);
                    Set<Color> colors = product.getColors();
                    if (colors == null) colors = new HashSet<>();
                    if (byName == null) byName = colorRepo.save(new Color(text));
                    colors.add(byName);
                    product.setColors(colors);
                    productMap.put(userId, product);
                }
                case 9 -> {
                    if (text.equals("Keyingi ➡️") && product.getStorages() != null) {
                        sendTextMessage(userActivity.setStep(11), "Narxini kiriting");
                        break;
                    }
                    Storage byName = storageRepo.findByName(text);
                    if (byName == null) {
                        byName = storageRepo.save(new Storage(text));
                    }
                    productDto.setStorage(byName.getName());
                    productDtoMap.put(userId, productDto);
                    sendTextMessage(userActivity.setStep(10), "Ayiriladigan miqdorni kiriting");
                }
                case 10 -> {
                    double amount = Double.parseDouble(text);
                    Map<Storage, Penalty> map = product.getStorages();
                    if (map == null) map = new HashMap<>();
                    Storage storage = storageRepo.findByName(productDtoMap.get(userId).getStorage());
                    Penalty penalty = penaltyRepo.findByAmount(amount);
                    if (penalty == null) penalty = penaltyRepo.save(new Penalty(amount));
                    map.put(storage, penalty);
                    product.setStorages(map);
                    productMap.put(userId, product);
                    sendTextMessage(userActivity.setStep(9), "Xotira sig'imlarini kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                }
                case 11 -> {
                    double price = Double.parseDouble(text);
                    product.setPrice(price);
                    productMap.put(userId, product);
                    sendTextMessage(userActivity.setStep(12), "Ishlab chiqariladigan joylarni kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                }
                case 12 -> {
                    if (text.equals("Keyingi ➡️") && product.getCountries() != null) {
                        sendTextMessage(userActivity.setStep(14), "Karobka & dokument yo'q bo'lganda olinadigan miqdorni kiriting");
                        break;
                    }
                    Country byName = countryRepo.findByName(text);
                    if (byName == null) byName = countryRepo.save(new Country(text));
                    productDto.setCountry(byName.getName());
                    productDtoMap.put(userId,productDto);
                }
                case 13 -> {
                    double amount = Double.parseDouble(text);
                    Map<Country, Penalty> countries = product.getCountries();
                    if (countries == null) countries = new HashMap<>();
                    Country byName = countryRepo.findByName(productDto.getCountry());
                    Penalty byAmount = penaltyRepo.findByAmount(amount);
                    if (byAmount == null) byAmount = penaltyRepo.save(new Penalty(amount));
                    countries.put(byName,byAmount);
                    product.setCountries(countries);
                    productMap.put(userId, product);
                    sendTextMessage(userActivity.setStep(12), "Ishlab chiqariladigan joylarni kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                }
                case 14 -> {
                    double documentPenalty = Double.parseDouble(text);
                    product.setDocumentPenalty(documentPenalty);
                    productMap.put(userId, product);
                    if (product.getBrand().getName().equalsIgnoreCase("iphone"))
                        sendTextMessage(userActivity.setStep(15), "Batareyka sig'imlarini kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                    else
                        sendTextMessage(userActivity.setStep(17), "0-10% orasidagi shikastlanganlik uchun olinadigan miqdorni kiriting");
                }
                case 15 -> {
                    if (text.equals("Keyingi ➡️") && product.getBattery() != null) {
                        sendTextMessage(userActivity.setStep(17), "0-10% orasidagi shikastlanganlik uchun olinadigan miqdorni kiriting");
                        break;
                    }
                    Battery byName = batteryRepo.findByName(text);
                    if (byName == null) byName = batteryRepo.save(new Battery(text));
                    productDto.setBatteryCapacity(byName.getName());
                    productDtoMap.put(userId, productDto);
                    sendTextMessage(userActivity.setStep(16), "Ayiriladigan miqdorni kiriting");
                }
                case 16 -> {
                    double amount = Double.parseDouble(text);
                    Map<Battery, Penalty> map = product.getBattery();
                    if (map == null) map = new HashMap<>();
                    Battery battery = batteryRepo.findByName(productDtoMap.get(userId).getBatteryCapacity());
                    Penalty penalty = penaltyRepo.findByAmount(amount);
                    if (penalty == null) penalty = penaltyRepo.save(new Penalty(amount));
                    map.put(battery, penalty);
                    product.setBattery(map);
                    sendTextMessage(userActivity.setStep(15), "Batareyka sig'imlarini kiriting. Kiritib bo'lgandan so'ng \"Keyingi ➡️\" tugmasini bosing");
                }
                case 17 -> {
                    int damage1 = Integer.parseInt(text);
                    product.setDamage1(damage1);
                    productMap.put(userId, product);
                    sendTextMessage(userActivity.setStep(18), "30-50% orasidagi shikastlanganlik uchun olinadigan miqdorni kiriting");
                }
                case 18 -> {
                    int damage2 = Integer.parseInt(text);
                    product.setDamage2(damage2);
                    productRepo.save(product);
                    sendTextMessage(userActivity.setStep(0), "Qo'shildi");
                }
                case 20 -> {
                    Brand brand = product.getBrand();
                    brand.setMessage(text);
                    brandRepo.save(brand);
                }
                case 21 -> {
                    Brand byName = brandRepo.findByName(text);
                    if (byName != null) {
                        product.setBrand(byName);
                        productMap.put(userId, product);
                        StringBuilder productList = getProductList(byName);
                        sendTextMessage(userActivity.setStep(22), productList.toString());
                    }
                }
                case 22 -> {
                    boolean flag = false;
                    try {
                        Product product1 = productRepo.findByIdAndBrandId(Integer.parseInt(text), product.getBrand().getId());
                        productRepo.delete(product1);
                    } catch (NumberFormatException e) {
                        sendTextMessage(userActivity, "Faqatgina mahsulot ID sini kiriting");
                        flag = true;
                    } catch (Exception e) {
                        sendTextMessage(userActivity, "O'chmadi");
                        flag = true;
                    }
                    if (!flag) {
                        sendTextMessage(userActivity.setStep(0), "O'chirildi");
                    }
                }
                case 23 -> {
                    Brand byName = brandRepo.findByName(text);
                    if (byName != null) {
                        product.setBrand(byName);
                        productMap.put(userId, product);
                        sendTextMessage(userActivity.setStep(24), getProductList(byName).toString());
                    }
                }
                case 24 -> {
                    Product product1 = productRepo.findByIdAndBrandId(Integer.parseInt(text), product.getBrand().getId());
                    if (product1 != null) {
                        product.setId(product1.getId());
                        sendTextMessage(userActivity.setStep(25), "Yangi narxni kiriting");
                    }
                }
                case 25 -> {
                    double newPrice = Double.parseDouble(text);
                    Product product1 = productRepo.findById(product.getId()).get();
                    product1.setPrice(newPrice);
                    productRepo.save(product1);
                    sendTextMessage(userActivity.setStep(0), "O'zgartirildi");
                }
                case 26 -> {
                    if (text.equals("Qo'shish")) {
                        sendTextMessage(userActivity.setStep(27), "Foydalanuvchi IDsini kiriting");
                    } else if (text.equals("O'chirish")) {
                        sendTextMessage(userActivity.setStep(28), "Foydalanuvchi IDsini kiriting");
                    }
                }
                case 27, 28 -> {
                    Long id = Long.parseLong(text);
                    if (!id.equals(userId)) {
                        UserActivity userActivity1 = userActivityService.userActivityRepository().findByUserId(id);
                        if (step == 27)
                            userActivity1.setRole("admin");
                        else
                            userActivity1.setRole("user");
                        userActivityService.userActivityRepository().save(userActivity1);
                    }
                    startMessage(userActivity);
                }
            }
        }
    }

    private ReplyKeyboard getReplyKeyboard(UserActivity userActivity, boolean isChannelMember) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(rows);
        Long userId = userActivity.getUser().getId();
        String langCode = userActivity.getLanguageCode();
        if (!isChannelMember) {
            return getJoinChannelRequest(userActivity);
        } else {
            ProductDto productDto = productDtoMap.get(userId);
            int step = userActivity.getStep();
            if (userActivity.getRole().equals("user")) {
                switch (step) {
                    case 0 -> getStep0Keyboard(langCode, rows);
                    case 1 -> {
                        List<Brand> brands = brandRepo.findByMessageNotNull();
                        getObjectsKeyboard(brands, rows);
                    }
                    case 2 -> getChangeLangKeyboard(langCode, rows);
                    case 3 -> getLangKeyboard(rows);
                    case 4 -> {
                        List<Brand> brands = brandRepo.findByExistsProducts();
                        getObjectsKeyboard(brands, rows);
                    }
                    case 5 -> {
                        Brand brand = brandRepo.findByName(productDto.getBrand());
                        List<Product> products = productRepo.findByBrandId(brand.getId());
                        getObjectsKeyboard(products, rows);
                    }
                    case 6 -> {
                        List<Battery> batteries = batteryRepo.findByProductId(productDto.getId());
                        getObjectsKeyboard(batteries, rows);
                    }
                    case 7, 11 -> getForBooleanKeyboard(langCode, rows);
                    case 8 -> {
                        List<Color> colors = colorRepo.findByProductId(productDto.getId());
                        getObjectsKeyboard(colors, rows);
                    }
                    case 9 -> {
                        List<Storage> storages = storageRepo.findByProductId(productDto.getId());
                        getObjectsKeyboard(storages, rows);
                    }
                    case 10 -> {
                        List<Country> countries = countryRepo.findByProductId(productDto.getId());
                        getObjectsKeyboard(countries, rows);
                    }
                    case 12 -> getStep11Keyboard(rows);
                }
            } else if (userActivity.getRole().equals("admin")) {
                switch (step) {
                    case 0 -> {
                        KeyboardRow row = new KeyboardRow();
                        row.add("CRUD");
                        rows.add(row);
                        row = new KeyboardRow();
                        row.add("Admin menu");
                        rows.add(row);
                        row = new KeyboardRow();
                        row.add("Reklama jo'natish");
                        rows.add(row);
                    }
                    case 1 -> {
                        KeyboardRow row1 = new KeyboardRow();
                        row1.add("Kanal CRUD");
                        row1.add("Mahsulot CRUD");
                        rows.add(row1);
                    }
                    case 2, 26 -> getCrudKeyboard(rows);
                    case 6, 19, 21, 23 -> {
                        List<Brand> all = brandRepo.findAll();
                        getObjectsKeyboard(all, rows);
                    }
                    case 29 -> {
                        KeyboardRow row = new KeyboardRow();
                        row.add("Sotish uchun");
                        row.add("Olish uchun");
                        rows.add(row);
                    }
                    case 4 -> {
                        for (Channel channel : channelRepo.findAll()) {
                            KeyboardRow row = new KeyboardRow();
                            row.add(channel.getChannelId());
                            rows.add(row);
                        }
                    }
                    case 5 -> {
                        getCrudKeyboard(rows);
                        KeyboardRow row = new KeyboardRow();
                        row.add("Narxini o'zgartirish");
                        rows.add(row);
                    }
                    case 8 -> {
                        List<Color> all = colorRepo.findAll();
                        getObjectsKeyboard(all, rows);
                    }
                    case 9 -> {
                        List<Storage> all = storageRepo.findAll();
                        getObjectsKeyboard(all, rows);
                    }
                    case 12 -> {
                        List<Country> all = countryRepo.findAll();
                        getObjectsKeyboard(all, rows);
                    }
                    case 15 -> {
                        List<Battery> all = batteryRepo.findAll();
                        getObjectsKeyboard(all, rows);
                    }
                }
                if (step == 8 || step == 9 || step == 12 || step == 15) {
                    KeyboardRow row = new KeyboardRow();
                    row.add("Keyingi ➡️");
                    rows.add(row);
                }
            }
            if (step == 2 || step == 6 || step == 9) replyKeyboardMarkup.setOneTimeKeyboard(true);
            if (step > 0 && step != 3) {
                KeyboardRow row = new KeyboardRow();
                row.add(langCode.equals("uz") ? "\uD83D\uDD1D Asosiy Menyu" : "\uD83D\uDD1D Главное Меню");
                rows.add(row);
            }
            return replyKeyboardMarkup;
        }
    }

    private void changeLanguage(UserActivity userActivity) {
        sendTextMessage(userActivity.setStep(3), """
                Assalomu Alaykum  rasmiy botiga xush kelibsiz iltimos tilni tanlang!\uD83D\uDC47
                   \s
                Здравствуйте, добро пожаловать в официальный бот!,
                Пожалуйста, выберите язык!"""
        ); //todo bot nomi yoziladi
    }

    private void startMessage(UserActivity userActivity) {
        String text;
        if (userActivity.getLanguageCode().equals("uz"))
            text = menuMessageUz;
        else
            text = menuMessageRu;
        sendTextMessage(userActivity.setStep(0), text);
    }

    private void getNewPrices(UserActivity userActivity) {
        sendTextMessage(userActivity.setStep(1), userActivity.getLanguageCode().equals("uz") ?
                "Kategoriyalardan birini tanlang" : "Выберите одну из категорий");
    }

    private void getCrudKeyboard(List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add("Qo'shish");
        row.add("O'chirish");
        rows.add(row);
    }

    private void getStep11Keyboard(List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add("0~10%");
        row.add("30~50%");
        rows.add(row);
    }

    private void getForBooleanKeyboard(String langCode, List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        if (langCode.equals("uz")) {
            row.add("Ha");
            row.add("Yo'q");
        } else {
            row.add("Да");
            row.add("Нет");
        }
        rows.add(row);
    }

    private void finallyMessage(UserActivity userActivity) {
        ProductDto productDto = productDtoMap.get(userActivity.getUser().getId());
        if (userActivity.getLanguageCode().equals("uz"))
            sendTextMessage(userActivity.setStep(0), "Brendi:" + productDto.getBrand() + "\n" +
                    "Modeli:" + productDto.getModel() + "\n" +
                    "\n" +
                    (productDto.getBrand().equalsIgnoreCase("iphone") ? "Batareyka foizi:" + productDto.getBatteryCapacity() +"\n": "") +
                    "Korobka dokumenti:" + (productDto.isDocuments() ? "Bor" : "Yo'q") + "\n" +
                    "Rangi:" + productDto.getColor() + "\n" +
                    "Xotirasi:" + productDto.getStorage() + "\n" +
                    "Ishlab chiqarilgan joyi:" + productDto.getCountry() + "\n" +
                    "Shikast yetganmi?:\n" + (productDto.getDamage() != null ? productDto.getDamage() : "Yo'q") + "\n" +
                    "\n" +
                    "Narxi:" + productDto.getPrice() + "$");
        else
            sendTextMessage(userActivity.setStep(0), "Бренд:" + productDto.getBrand() + "\n" +
                    "Модель:" + productDto.getModel() + "\n" +
                    "\n" +
                    (productDto.getBrand().equalsIgnoreCase("iphone") ? "Процент батареи:" + productDto.getBatteryCapacity() +"\n": "") +
                    "Коробка и документ:" + (productDto.isDocuments() ? "Есть" : "Нет") + "\n" +
                    "Цвет:" + productDto.getColor() + "\n" +
                    "Память:" + productDto.getStorage() + "\n" +
                    "Место изготовления:" + productDto.getCountry() + "\n" +
                    "Поврежден ли ваш тел?:\n" + (productDto.getDamage() != null ? productDto.getDamage() : "Нет") + "\n" +
                    "\n" +
                    "Цена:" + productDto.getPrice() + "$");
    }

    private <T extends AbsEntity> void getObjectsKeyboard(List<T> list, List<KeyboardRow> rows) {
        int count = 0;
        KeyboardRow row = new KeyboardRow();
        for (AbsEntity item : list) {
            count++;
            row.add(item.getName());
            if (count == 2) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        rows.add(row);
    }

    private void getLangKeyboard(List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add("O'zbek \uD83C\uDDFA\uD83C\uDDFF");
        row.add("Русский \uD83C\uDDF7\uD83C\uDDFA");
        rows.add(row);
    }

    private void getChangeLangKeyboard(String langCode, List<KeyboardRow> rows) {
        KeyboardRow row = new KeyboardRow();
        row.add(langCode.equals("uz") ? "Tilni o`zgartirish \uD83C\uDF0E" : "Сменить язык \uD83C\uDF0E");
        rows.add(row);
    }

    private void getStep0Keyboard(String langCode, List<KeyboardRow> rows) {
        if (langCode.equals("uz"))
            for (String s : menuButtonsUz) {
                KeyboardRow row = new KeyboardRow(1);
                row.add(s);
                rows.add(row);
            }
        else
            for (String s : menuButtonsRu) {
                KeyboardRow row = new KeyboardRow(1);
                row.add(s);
                rows.add(row);
            }

    }

    private InlineKeyboardMarkup getJoinChannelRequest(UserActivity userActivity) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

        for (Channel channel : channelRepo.findAll()) {
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(userActivity.getLanguageCode().equals("uz") ? "Kanal" : "Канал");
            button.setUrl("t.me/" + channel.getChannelId());
            keyboardRow.add(button);
            keyboardRows.add(keyboardRow);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private UserActivity checkUserActivity(Update update) {
        return userActivityService.findByUserId(update);
    }

    private void sendTextMessage(UserActivity userActivity, String text) {
        ReplyKeyboard replyKeyboard;
        if (text.equals("Iltimos, avvalo kanallarga obuna bo'ling va qayta \n" +
                "/start buyrug'ini yuboring") || text.equals("Пожалуйста, подпишитесь на каналы сначала и зарегистрируйтесь снова\n" +
                "Отправьте команду /start")) {
            replyKeyboard = getReplyKeyboard(userActivity,
                    false);
        } else replyKeyboard = getReplyKeyboard(userActivity, true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userActivity.getUser().getId().toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendForwardMessage(String toChatId, String fromChatId, int messageId) {
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChatId(toChatId);
        forwardMessage.setMessageId(messageId);
        forwardMessage.setFromChatId(fromChatId);
        try {
            execute(forwardMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private User getAdmin() {
        return userActivityService.userActivityRepository().findByRole("admin").get(0).getUser();
    }
}