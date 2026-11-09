package ru.pokemonbattle;

import ru.pokemonbattle.framework.Config;

public class TestData {
    public static final String BASE_URL = Config.getUrl("/");

    public static final String LOGIN =  Config.get("test.login", "default_test_user");
    public static final String PASSWORD =  Config.get("test.password", "default_password");
    public static final String EXPECTED_TRAINER_ID = Config.get("test.trainer_id", "12345");


}
